package application;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadSocketHandler implements SocketHandler {

	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	private ExecutorService executor = Executors
			.newFixedThreadPool(this.configuration.getInicialThreads());

	public void attend(final Socket socket) {
		// TODO: Agregar acá la conexion que implement runnable para pasarla al
		// excute.
		this.executor.execute(null);
	}

}
