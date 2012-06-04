package application;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.ServerException;

import logger.ErrorLogger;
import logger.FullLogger;
import logger.HumanLogger;
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

	// Cliente
	Connection client;
	Connection server;
	volatile EndPointConnectionHandler hostConnections;
	boolean proxyKeepAlive = true;
	static final Logger fullLogger = Logger.getLogger(FullLogger.class);
	static final Logger humanLogger = Logger.getLogger(HumanLogger.class);
	static final Logger errorLogger = Logger.getLogger(ErrorLogger.class);
	
	private static Integer MAX_FORWARDS = 5;

	// Configuracion del proxy
	private ProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	private CollectionConnectionHandler connections = CollectionConnectionHandlerImpl
			.getInstance();

	public ResolverThread(final Connection client) {
		this.client = client;
	}

	public void run() {
		HttpRequestImpl request = null;
		HttpResponseImpl response = null;
		BasicConfigurator.configure();

		// final RequestFilter rf = RequestFilter.getInstance();
		do {
			// Obtenemos Request y Response.
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

			fullLogger.info("Request: " + request + "\n\n\n\n\n");
			fullLogger.info("Response: " + response + "________________________________________________");
			humanLogger.info(request.getLogString());
			humanLogger.info(response.getLogString());
			humanLogger.info("________________________________");
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

		} while (this.proxyKeepAlive && this.keepAlive(request)
				&& !this.client.isClosed());
		this.close();
	}

	private void close() {
		if (this.server != null) {
			if (this.hostConnections == null) {
				System.out.println("HostHand es null!");
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

		if (this.configuration.hasProxy()) {
			if (req.getRequestURI().isAbsolute()) {
				url = req.getRequestURI().toString();
			} else {
				final String query = req.getRequestURI().getRawQuery() != null ? "?"
						+ req.getRequestURI().getRawQuery()
						: "";
				url = "http://" + req.getHeader("Host")
						+ req.getRequestURI().getRawPath() + query;
			}
		} else {
			final String query = req.getRequestURI().getRawQuery() != null ? "?"
					+ req.getRequestURI().getRawQuery()
					: "";
			url = req.getRequestURI().getRawPath() + query;
		}

		try {
			req.setRequestURI(new URI(url));
		} catch (final URISyntaxException e) {
		}
	}

	private HttpRequestImpl getRequest() throws ServerException,
			ResponseException, EncodingException {

		// HttpRequestImpl request;
		// final InputStream input = this.client.getInputStream();
		// request = new HttpRequestImpl(input);
		// return request;

		HttpRequestImpl request = null;
		InputStream stream = null;
		try {
			stream = this.client.getInputStream();
			// System.out.println("STREAM: " + stream);
		} catch (final Exception e) {
			throw new CloseException("Falla al traer el InputStream");
			// System.out.println("El cliente no responde o cerro la conexion");
		}
		try {
			request = new HttpRequestImpl(stream);
			this.modifyRequest(request);
		} catch (final Exception e) {
			throw new CloseException(
					"El cliente no response o cerro la conexion");
			// System.out.println("El cliente no responde o cerro la conexion");
		}
		return request;
	}
}
