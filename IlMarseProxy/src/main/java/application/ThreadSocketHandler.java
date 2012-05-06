package application;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadSocketHandler implements SocketHandler {

	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	// Executor para solucionar todo el tema de pool threads de lado de java.
	private ExecutorService executor = Executors
			.newFixedThreadPool(this.configuration.getInicialThreads());

	// Atiende la conexion entrante por el socket.
	public void attend(final Socket socket) {
		final Connection connection = new ConnectionImpl(socket);
		this.executor.execute(new ResolverThread());
	}

}
