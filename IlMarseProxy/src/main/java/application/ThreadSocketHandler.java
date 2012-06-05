package application;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import connection.Connection;
import connection.ConnectionImpl;

public class ThreadSocketHandler implements SocketHandler {

	/**
	 * @uml.property  name="configuration"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	// Executor para solucionar todo el tema de pool threads de lado de java.
	/**
	 * @uml.property  name="executor"
	 */
	private ExecutorService executor = Executors
			.newFixedThreadPool(this.configuration.getInicialThreads());

	// Atiende la conexion entrante por el socket.
	public void attend(final Socket socket) {
		final Connection connection = new ConnectionImpl(socket);
		this.executor.execute(new ResolverThread(connection));
	}

}
