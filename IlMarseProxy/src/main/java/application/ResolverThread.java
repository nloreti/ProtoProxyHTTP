package application;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.ServerException;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import connection.CollectionConnectionHandler;
import connection.CollectionConnectionHandlerImpl;
import connection.Connection;
import connection.EndPointConnectionHandler;
import exceptions.ClientException;
import exceptions.CloseException;
import exceptions.ConnectionException;
import exceptions.EncodingException;
import exceptions.ResponseException;
import exceptions.ServerTimeOutException;

public class ResolverThread implements Runnable {

	// Conexion con el Cliente
	/**
	 * @uml.property  name="client"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Connection client;
	// Conexion con el Servidor
	/**
	 * @uml.property  name="server"
	 * @uml.associationEnd  
	 */
	Connection server;
	// Conexion con el Host
	/**
	 * @uml.property  name="hostConnections"
	 * @uml.associationEnd  
	 */
	volatile EndPointConnectionHandler hostConnections;
	// Para conexiones persistentes
	/**
	 * @uml.property  name="proxyKeepAlive"
	 */
	boolean proxyKeepAlive = true;
	// Logger
	static final Logger logger = Logger.getLogger(ResolverThread.class);
	// Se usa para el Max-Fowards para el Proxy Chain
	private static Integer MAX_FORWARDS = 5;

	// Configuracion del proxy
	/**
	 * @uml.property  name="configuration"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	// Configuracion de las conexiones
	/**
	 * @uml.property  name="connections"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private CollectionConnectionHandler connections = CollectionConnectionHandlerImpl
			.getInstance();

	public ResolverThread(final Connection client) {
		this.client = client;
	}

	public void run() {
		HttpRequestImpl request = null;
		HttpResponseImpl response = null;
		BasicConfigurator.configure();

		do {
			try {
				request = this.getRequest();
				try {
					response = this.getResponse(request);
				} catch (final CloseException e) {
					// System.out.println("Fallo el R & Response");
					this.proxyKeepAlive = false;
					this.close();
					return;
				}
			} catch (final Exception e) {
				this.proxyKeepAlive = false;
				this.close();
				return;
			}

			// logger.warn("Request: " + request.getLogString());
			// logger.warn("Response: " + response.getLogString());
			// Retornamos la respuesta.
			try {
				final boolean respKeepAlive = this.keepAlive(response);
				this.setHeaders(response, request);
				this.sendResponse(response);
				if (this.hostConnections != null) {
					if (respKeepAlive) {
						this.hostConnections.free(this.server);
					} else {
						this.hostConnections.drop(this.server);
					}
				}
				this.server = null;
			} catch (final ClientException client) {
				this.close();
				return;
			} catch (final CloseException close) {
				this.close();
				return;
			}

		} while (this.proxyKeepAlive && !this.client.isClosed()
				&& this.keepAlive(request));
		this.close();
	}

	private void close() {
		if (this.server != null) {
			if (this.hostConnections == null) {
				throw new CloseException("Connexion con el host erronea");
				// System.out.println("HostConnections es null!");
			}
			this.hostConnections.drop(this.server);
		}
		this.client.close();

	}

	private boolean keepAlive(final HttpRequestImpl request) {

		if (!this.configuration.isClientPersistent() || request == null) {
			return false;
		}

		boolean hasToClose;

		if ("HTTP/1.1".equals(request.getProtocol())) {
			hasToClose = "close".equals(request.getHeader("Connection"));
			hasToClose |= "close".equals(request.getHeader("Proxy-Connection"));
		} else {
			hasToClose = true;
		}

		return !hasToClose;
	}

	private void setHeaders(final HttpResponseImpl response,
			final HttpRequestImpl request) {

		String viaHeader = null;

		if (this.keepAlive(request)) {
			response.replaceHeader("Connection", "keep-alive");
		} else {
			response.replaceHeader("Connection", "close");
		}

		if (response.getHeader("Via") != null) {
			viaHeader = response.getHeader("Via");
			viaHeader = viaHeader + "," + response.getProtocol()
					+ " IlMarseProxy";
			response.replaceHeader("Via", viaHeader);
		} else {
			response.addHeader("Via", response.getProtocol() + " IlMarseProxy");
		}

	}

	private boolean keepAlive(final HttpResponseImpl response) {
		boolean keepAlive;

		keepAlive = "keep-alive".equals(response.getHeader("Connection"));

		keepAlive &= response.getProtocol().equals("HTTP/1.1");

		return keepAlive;
	}

	private void sendResponse(final HttpResponseImpl response) {
		try {
			this.client.send(response);
		} catch (final ResponseException e) {
			throw new CloseException("Error en el Response");
		} catch (final exceptions.ServerException e) {
			throw new CloseException("Error en el Response");
		} catch (final ServerTimeOutException e) {
			throw new CloseException("Time out Server");
		} catch (final ConnectionException e) {
			throw new CloseException("Error en la Conexión");
		}

	}

	private HttpResponseImpl getResponse(final HttpRequestImpl request) {

		HttpResponseImpl response = null;

		this.server = this.getConnection(request.getHost());
		this.server.send(request);
		try {
			response = this.server.receive();
		} catch (final ServerException e) {
			throw new CloseException("Error en el Server");
		} catch (final ResponseException e) {
			throw new CloseException("Error en el Response");
		} catch (final EncodingException e) {
			throw new CloseException("Error en el Econding");
		}

		response = RequestFilter.getInstance().doFilter(request, response);
		// response = rf.doFilter(request, response);
		// System.out.println(response);
		return response;
	}

	// TODO: Implementar el getConnection desde un host porque no hay otra
	// forma, agregar un mapa con conexiones a host y que el mismo tenga varias
	// conexiones disponbiles para ese host.

	public Connection getConnection(final String host) {
		this.hostConnections = this.connections
				.getEndPointConnectionHandler(host);
		// System.out.println("Host Connections: " + hostConnections);
		return this.hostConnections.getConnection();
	}

	private void modifyRequest(final HttpRequestImpl req) {
		String url = null;
		if (req.getHeader("Via") != null) {
			req.appendHeader("Via", req.getProtocol() + " IlMarseProxy");
		} else {
			req.addHeader("Via", req.getProtocol() + " IlMarseProxy");
		}

		Integer currentforwards;
		if (req.getHeader("Max-Forwards") != null) {
			currentforwards = Integer.valueOf(req.getHeader("Max-Forwards"));
			if (currentforwards > 0) {
				currentforwards--;
			}
		} else {
			currentforwards = MAX_FORWARDS;
		}
		req.replaceHeader("Max-Forwards", String.valueOf(currentforwards));

		if (!this.configuration.hasProxy()) {
			final String query = req.getRequestURI().getRawQuery() != null ? "?"
					+ req.getRequestURI().getRawQuery()
					: "";
			url = req.getRequestURI().getRawPath() + query;
		} else {
			if (req.getRequestURI().isAbsolute()) {
				url = req.getRequestURI().toString();
			} else {
				final String query = req.getRequestURI().getRawQuery() != null ? "?"
						+ req.getRequestURI().getRawQuery()
						: "";
				url = "http://" + req.getHeader("Host")
						+ req.getRequestURI().getRawPath() + query;
			}
		}

		try {
			req.setRequestURI(new URI(url));
		} catch (final URISyntaxException e) {
			throw new CloseException("Fallo en la creacion de la URI");
		}
	}

	private HttpRequestImpl getRequest() throws ServerException,
			ResponseException, EncodingException {

		HttpRequestImpl request = null;
		InputStream stream = null;
		try {
			stream = this.client.getInputStream();
			// System.out.println("STREAM: " + stream);
		} catch (final Exception e) {
			throw new CloseException("Falla al traer el InputStream");
		}
		try {
			request = new HttpRequestImpl(stream);
			this.modifyRequest(request);
		} catch (final Exception e) {
			throw new CloseException(
					"El cliente no response o cerro la conexion");
		}
		return request;
	}
}
