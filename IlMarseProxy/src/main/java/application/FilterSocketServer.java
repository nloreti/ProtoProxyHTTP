package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FilterSocketServer implements Runnable {

	ConnectionHandler handler;
	ServerSocket server;
	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	public FilterSocketServer(final InetAddress interfaz,
			final ConnectionHandler handler) throws IOException {

		final int port = this.configuration.getFilterPort();
		final int backlog = this.configuration.getFilterBackLog();

		this.server = new ServerSocket(port, backlog, interfaz);
		this.handler = handler;
		System.out.printf("Configuracion de filtro escuchando en %s\n",
				this.server.getLocalSocketAddress());
	}

	public void run() {
		try {
			while (true) {
				final Socket socket = this.server.accept();
				final String s = socket.getRemoteSocketAddress().toString();
				System.out.printf("Se conecto %s\n", s);
				this.handler.handle(socket);
				if (!socket.isClosed()) {
					socket.close();
					System.out.printf("Terminando %s\n", s);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
