package connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import model.HttpRequest;
import model.HttpResponse;
import model.HttpResponseImpl;
import application.DinamicProxyConfiguration;
import exceptions.BadResponseException;

public class ConnectionImpl implements Connection {

	private Socket socket;
	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	/* Constructores */

	public ConnectionImpl(final Socket socket) {
		this.socket = socket;
		try {
			this.socket.setSoTimeout(this.configuration.getTimeOutToClient());
		} catch (final SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ConnectionImpl(final InetAddress ip, final Integer port) {
		try {
			this.socket = new Socket(ip, port);
			this.socket.setSoTimeout(this.configuration.getTimeOutToServer());
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ConnectionImpl(final InetSocketAddress address) {
		this(address.getAddress(), address.getPort());
	}

	/* Fin de Constructores */

	public void send(final HttpResponse response) {
		// TODO Auto-generated method stub
		// Una vez que se tiene la respuesta hay que mandar por el OutPutStream
		// del socket la misma. Para esto tiene que haber un metodo en
		// HttpResponse que dado un OutPutStream permita sacarlo por ahi

	}

	public void send(final HttpRequest request) {
		// TODO Auto-generated method stub
		// IDEM QUE PARA EL PUNTO ANTERIOR.

	}

	public HttpResponse receive() throws BadResponseException {
		// TODO Auto-generated method stub
		// TODO: Esto hay que arreglarlo con Marse o quien haya hecho la
		// response. Tiene que recibir un stream
		// y pasarselo al HttpResponse como parametro para que devuelva la
		// respuesta

		InputStream stream = null;
		try {
			stream = this.socket.getInputStream();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new HttpResponseImpl(stream);

	}

	public InputStream getInputStream() {

		try {
			return this.socket.getInputStream();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return null;
	}

	public InetAddress getRemoteIP() {
		return this.socket.getInetAddress();
	}

	public void close() {
		// Si el socket ya esta cerrado, retorno.
		if (this.isClosed()) {
			return;
		}
		try {
			// si no intento cerrarlo.
			this.socket.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isClosed() {
		return this.socket.isClosed();
	}

}
