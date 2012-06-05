package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FilterSocketServer implements Runnable {

	/**
	 * @uml.property  name="handler"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	ConnectionHandler handler;
	/**
	 * @uml.property  name="server"
	 */
	ServerSocket server;

	public FilterSocketServer(final int port, final InetAddress interfaz,
			final ConnectionHandler handler) throws IOException {
		server = new ServerSocket(port, 50, interfaz);
		this.handler = handler;
		System.out.printf("Configuraci�n de filtro escuchando en %s\n",
				server.getLocalSocketAddress());
	}

	public void run() {
		try {
			while (true) {
				final Socket socket = server.accept();
				String s = socket.getRemoteSocketAddress().toString();
				System.out.printf("Se conecto %s\n", s);
				handler.handle(socket);
				if (!socket.isClosed()) {
					socket.close();
					System.out.printf("Terminando %s\n", s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
