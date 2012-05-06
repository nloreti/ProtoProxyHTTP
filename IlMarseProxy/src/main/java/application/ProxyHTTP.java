package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyHTTP implements Runnable {

	DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	SocketHandler socketHandler = new ThreadSocketHandler();

	public void run() {

		// Chequeo si levanto la configuracion del proxy.
		this.testProxyConfiguration();

		ServerSocket listenerSocket = null;

		try {
			listenerSocket = new ServerSocket(
					this.configuration.getProxyPort(),
					this.configuration.getProxyBackLog());
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true) {
			Socket requestSocket = null;

			try {
				requestSocket = listenerSocket.accept();
				this.socketHandler.attend(requestSocket);
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void testProxyConfiguration() {

		System.out.println("Proxy Port: " + this.configuration.getProxyPort());
		System.out.println("Initial Threads: "
				+ this.configuration.getInicialThreads());
		System.out
				.println("Max Threads: " + this.configuration.getMaxThreads());
		System.out
				.println("Min Threads: " + this.configuration.getMinThreads());
		System.out.println("TimeOut Client: "
				+ this.configuration.getTimeOutToClient());
		System.out.println("TimeOut Server: "
				+ this.configuration.getTimeOutToServer());

	}

}
