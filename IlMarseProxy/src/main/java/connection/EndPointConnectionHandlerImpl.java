package connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import application.DinamicProxyConfiguration;

public class EndPointConnectionHandlerImpl implements EndPointConnectionHandler {
	private final int idleConnectionTimeMs=3000;
	private long lastModified; 
	private Timer timer = new Timer();
	InetSocketAddress sockAddress;
	AtomicInteger con = new AtomicInteger();
	private BlockingQueue<Connection> connections;
	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	public EndPointConnectionHandlerImpl(final InetSocketAddress sockAddress) {
		this.sockAddress = sockAddress;
		this.connections = new LinkedBlockingQueue<Connection>();
		lastModified = System.currentTimeMillis();
		final TimerTask task = new TimerTask() {
			@Override
			public synchronized void run() {
				if(lastModified + idleConnectionTimeMs < System.currentTimeMillis()){
					if(!connections.isEmpty()){
						Connection c = connections.poll();
						if(!c.isClosed()){
							c.close();
						}
					}
				}
			}
		};

		this.timer.scheduleAtFixedRate(task, 0, 1000);
	}

	public synchronized Connection getConnection() {
		
		if (!this.connections.isEmpty()) {
			System.out.println("se reuso una conexion");
			return this.connections.poll();
		}

		Connection connection;

		if (this.configuration.hasProxy()) {
			connection = new ConnectionImpl(
					this.configuration.getChainProxyIP(),
					this.configuration.getChainProxyPort());
		} else {
			connection = new ConnectionImpl(this.sockAddress);
		}
		lastModified = System.currentTimeMillis();
		System.out.println(sockAddress + ": " + con.incrementAndGet());
		System.out.println("Connexion: " + connection);
		return connection;
	}

	public void free(final Connection connection) {
		if (connection != null) {
			try {
				if (connection.getInputStream().available() != 0) {
					// Por precaución no se reusa la conexión (podría estar
					// sucio el inputStream).
					this.drop(connection);
					return;
				}
				lastModified = System.currentTimeMillis();
				this.connections.offer(connection);
				System.err.println("Se ofrece una conexion -------------");
			} catch (final IOException e) {
				this.drop(connection);
			}
		}else{
		System.out.println("La conexion fue null");}
	}

	public void drop(final Connection connection) {
		if (connection != null) {
			connection.close();
			System.out.println(sockAddress + ": " + con.decrementAndGet());
			System.out.println("Se cerro una conexion");
		}
	}

}
