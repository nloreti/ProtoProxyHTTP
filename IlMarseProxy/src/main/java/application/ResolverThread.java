package application;

import java.io.InputStream;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import connection.CollectionConnectionHandler;
import connection.CollectionConnectionHandlerImpl;
import connection.Connection;
import connection.EndPointConnectionHandler;
import exceptions.BadResponseException;

public class ResolverThread implements Runnable {

	// Cliente
	Connection client;
	Connection server;

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

		request = this.getRequest();
		response = this.getResponse(request);
		this.sendResponse(response);

	}

	private void sendResponse(final HttpResponseImpl response) {
		this.client.send(response);

	}

	private HttpResponseImpl getResponse(final HttpRequestImpl request) {

		HttpResponseImpl response = null;

		// TODO: Cableado, hay que pedirlo al request.
		this.server = this.getConnection(request.getHost());
		this.server.send(request);
		try {
			response = this.server.receive();
		} catch (final BadResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	// TODO: Implementar el getConnection desde un host porque no hay otra
	// forma, agregar un mapa con conexiones a host y que el mismo tenga varias
	// conexiones disponbiles para ese host.

	public Connection getConnection(final String host) {
		final EndPointConnectionHandler hostConnections = this.connections
				.getEndPointConnectionHandler(host);
		return hostConnections.getConnection();
	}

	private HttpRequestImpl getRequest() {

		HttpRequestImpl request;
		final InputStream input = this.client.getInputStream();
		request = new HttpRequestImpl(input);
		return request;
	}

}
