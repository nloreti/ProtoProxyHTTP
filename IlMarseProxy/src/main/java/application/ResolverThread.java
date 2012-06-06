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
	Connection client;
	// Conexion con el Servidor
	Connection server;
	// Conexion con el Host
	volatile EndPointConnectionHandler hostConnections;
	// Para conexiones persistentes
	boolean KeepAlive = true;
	// Logger
	static final Logger logger = Logger.getLogger(ResolverThread.class);
	// Se usa para el Max-Fowards para el Proxy Chain
	private static Integer MAX_FORWARDS = 5;

	// Configuracion del proxy
	private ProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	// Configuracion de las conexiones
	private CollectionConnectionHandler connections = CollectionConnectionHandlerImpl
			.getInstance();

	public ResolverThread(final Connection client) {
		this.client = client;
	}

	@Override
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
					this.KeepAlive = false;
					this.close();
					return;
				}
			} catch (final Exception e) {
				this.KeepAlive = false;
				this.close();
				return;
			}

			logger.debug("Request: " + request + "\n\n\n\n\n");
			logger.debug("Response: " + response);
			// Retornamos la respuesta.
			try {
				final boolean respKeepAlive = this.keepAlive(response);
				// System.out.println("resp: " + respKeepAlive);
				// respKeepAlive = true;

				this.setHeaders(response, request);
				this.sendResponse(response);
				Statistics.getInstance().incrementProxyServerBytes(
						response.getWritten());
				// System.out.println(response.getWritten());
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

		} while (this.KeepAlive && !this.client.isClosed()
				&& this.keepAlive(request));
		this.close();
	}

	public Connection getConnection(final String host) {
		this.hostConnections = this.connections
				.getEndPointConnectionHandler(host);
		return this.hostConnections.getConnection();
	}

	private void close() {
		if (this.server != null) {
			if (this.hostConnections == null) {
				throw new CloseException("Connexion con el host erronea");
			}
			this.hostConnections.drop(this.server);
		}
		this.client.close();

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
			this.alterateRequest(request);
		} catch (final Exception e) {
			throw new CloseException(
					"El cliente no response o cerro la conexion");
		}
		Statistics.getInstance().incrementProxyClientBytes(request.getRead());
		return request;
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
		return response;
	}

	private void sendResponse(final HttpResponseImpl response) {
		try {
			this.client.send(response);
			// System.out.println(response.getWritten());
			Statistics.getInstance().incrementProxyClientBytes(
					response.getWritten());
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

	private boolean keepAlive(final HttpRequestImpl request) {

		boolean hasToClose;

		if (!this.configuration.isClientPersistent() || request == null) {
			return false;
		}

		if ("HTTP/1.1".equals(request.getProtocol())) {
			final boolean connection = "close".equals(request
					.getHeader("Connection"));
			final boolean proxyConnection = "close".equals(request
					.getHeader("Proxy-Connection"));
			hasToClose = connection | proxyConnection;
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
		keepAlive = response.getHeader("Connection") == null ? false
				: "keep-alive".compareToIgnoreCase(response
						.getHeader("Connection")) == 0;

		keepAlive &= response.getProtocol().equals("HTTP/1.1");
		return keepAlive;
	}

	private void alterateRequest(final HttpRequestImpl req) {
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

}
