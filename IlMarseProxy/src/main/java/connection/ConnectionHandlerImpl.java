package connection;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import application.DinamicProxyConfiguration;

public class ConnectionHandlerImpl implements ConnectionHandler {

	InetSocketAddress sockAddress;
	private BlockingQueue<Connection> connections;
	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	public ConnectionHandlerImpl(final InetSocketAddress sockAddress) {
		this.sockAddress = sockAddress;
		this.connections = new LinkedBlockingQueue<Connection>();
	}

	public synchronized Connection getConnection() {

		if (!this.connections.isEmpty()) {
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

		return connection;
	}

	public void free(final ConnectionImpl connection) {
		if (connection != null) {
			connection.close();// TODO:que pasa si no se cierra
			this.connections.offer(connection);
		}

	}

}
