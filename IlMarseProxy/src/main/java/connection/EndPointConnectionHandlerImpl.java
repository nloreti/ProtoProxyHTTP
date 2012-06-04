package connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import application.DinamicProxyConfiguration;
import application.Statistics;

public class EndPointConnectionHandlerImpl implements EndPointConnectionHandler {

	InetSocketAddress sockAddress;
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
		System.out.println("Connexion: " + connection);
		Statistics.getInstance().connectionOpened();
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
			} catch (final IOException e) {
				this.drop(connection);
			}
			this.connections.offer(connection);
		}

	}

	public void drop(final Connection connection) {
		if (connection != null) {
			connection.close();
			Statistics.getInstance().connectionClosed();
			System.out.println("Se cerro una conexion");
		}
	}

}
