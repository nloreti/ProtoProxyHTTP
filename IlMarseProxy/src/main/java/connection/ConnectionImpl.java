package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.ServerException;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import application.DinamicProxyConfiguration;
import application.Statistics;
import exceptions.CloseException;
import exceptions.ConnectionException;
import exceptions.EncodingException;
import exceptions.MessageException;
import exceptions.RequestException;
import exceptions.ResponseException;

public class ConnectionImpl implements Connection {

	private Socket socket;
	private String host;
	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	/* Constructores */

	public ConnectionImpl(final String host) {
		try {
			final String[] hostInfo = host.split(":", 2);
			if (hostInfo.length > 2) {
				throw new Exception();
			}
			this.host = hostInfo[0];
			int port = 80;
			if (hostInfo.length == 2) {
				try {
					port = Integer.valueOf(hostInfo[1]);
				} catch (final NumberFormatException e) {
					throw new Exception();
				}
			}
			final InetAddress addr = InetAddress.getByName(hostInfo[0]);
			this.setupConnection(addr, port);
		} catch (final Exception e) {
			throw new CloseException("Cierre de la conexion");
		}
	}

	private void setupConnection(final InetAddress ip, final int port)
			throws CloseException {
		try {
			this.socket = new Socket(ip, port);
			this.socket.setSoTimeout(this.configuration.getTimeOutToServer());
		} catch (final SocketException e) {
			throw new CloseException("Cierre de la conexion");
		} catch (final IOException e) {
			throw new CloseException("Cierre de la conexion");
		}
	}

	public ConnectionImpl(final Socket socket) {
		this.socket = socket;
		try {
			this.socket.setSoTimeout(this.configuration.getTimeOutToClient());
			Statistics.getInstance().connectionOpened();
		} catch (final SocketException e) {
			throw new CloseException("Cierre de la conexion");
		}

	}

	public ConnectionImpl(final InetAddress ip, final Integer port) {
		try {
			this.socket = new Socket(ip, port);
			this.socket.setSoTimeout(this.configuration.getTimeOutToServer());
			Statistics.getInstance().connectionOpened();
		} catch (final IOException e) {
			throw new CloseException("Cierre de la conexion");
		}

	}

	public ConnectionImpl(final InetSocketAddress address) {
		this(address.getAddress(), address.getPort());
	}

	/* Fin de Constructores */

	@Override
	public void send(final HttpResponseImpl response) {
		OutputStream out;
		try {
			out = this.socket.getOutputStream();
		} catch (final IOException e) {
			throw new ConnectionException("Fallo el send Response");
		}
		try {
			response.writeStream(out);
		} catch (final MessageException e) {
			throw new ResponseException();
		}

	}

	@Override
	public void send(final HttpRequestImpl request) {
		OutputStream out;
		try {
			out = this.socket.getOutputStream();
			request.writeStream(out);
		} catch (final IOException e) {
			throw new RequestException("Fallo al enviar el Request");
		}
	}

	@Override
	public HttpResponseImpl receive() throws ResponseException,
			ServerException, EncodingException {
		InputStream stream = null;
		try {
			stream = this.socket.getInputStream();
		} catch (final IOException e) {
			throw new CloseException("Fallo el recieve de Connection");
		}
		return new HttpResponseImpl(stream);

	}

	public OutputStream getOutputStream() {
		try {
			return this.socket.getOutputStream();
		} catch (final IOException e) {
			throw new CloseException(
					"Cierre de la conexion por fallo del Stream");
		}
	}

	@Override
	public InputStream getInputStream() {

		try {
			return this.socket.getInputStream();
		} catch (final IOException e) {
			throw new CloseException(
					"Cierre de la conexion por fallo del Stream");
		}
	}

	@Override
	public InetAddress getRemoteIP() {
		return this.socket.getInetAddress();
	}

	@Override
	public void close() {
		// Si el socket ya esta cerrado, retorno.
		if (this.isClosed()) {
			return;
		}
		try {
			// si no intento cerrarlo.
			this.socket.close();
			Statistics.getInstance().connectionClosed();
		} catch (final IOException e) {
			Statistics.getInstance().connectionClosed();
			throw new CloseException("Cierre de la conexion tras fallo");
		}
	}

	public String getHost() {
		return this.host;
	}

	public InetAddress getSourceIP() {
		return this.socket.getInetAddress();
	}

	@Override
	public boolean isClosed() {
		return this.socket.isClosed();
	}

}
