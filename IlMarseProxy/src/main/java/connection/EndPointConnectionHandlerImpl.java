package connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import application.DinamicProxyConfiguration;

public class EndPointConnectionHandlerImpl implements EndPointConnectionHandler {

	InetSocketAddress sockAddress;
	AtomicInteger con = new AtomicInteger();
	private BlockingQueue<Connection> connections;
	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	public EndPointConnectionHandlerImpl(final InetSocketAddress sockAddress) {
		this.sockAddress = sockAddress;
		this.connections = new LinkedBlockingQueue<Connection>();
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
