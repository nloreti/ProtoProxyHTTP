package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyHTTP implements Runnable {

	DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	SocketHandler socketHandler = new ThreadSocketHandler();

	public ProxyHTTP() {
		super();
	}

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
		System.out.println("Chain Proxy IP: "
				+ this.configuration.getChainProxyIP());
		System.out.println("Chain Proxy Port: "
				+ this.configuration.getChainProxyPort());
		System.out.println("Proxy Backlog: "
				+ this.configuration.getProxyBackLog());
		System.out.println("Time out client: "
				+ this.configuration.getTimeOutToClient());
		System.out.println("Time out Server: "
				+ this.configuration.getTimeOutToServer());
		System.out.println("Servers per connection: "
				+ this.configuration.getMaxServersPerConnection());

	}

}
