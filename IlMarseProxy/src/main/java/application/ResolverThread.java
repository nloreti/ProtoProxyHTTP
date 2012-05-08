package application;

import java.io.InputStream;

import model.HttpRequest;
import model.HttpRequestImpl;
import model.HttpResponse;
import connection.CollectionConnectionHandler;
import connection.CollectionConnectionHandlerImpl;
import connection.Connection;
import connection.EndPointConnectionHandler;

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
		HttpRequest request = null;
		HttpResponse response = null;

		request = this.getRequest();
		response = this.getResponse(request);
		this.sendResponse(response);

	}

	private void sendResponse(final HttpResponse response) {
		this.client.send(response);

	}

	private HttpResponse getResponse(final HttpRequest request) {

		HttpResponse response = null;

		// TODO: Cableado, hay que pedirlo al request.
		this.server = this.getConnection("google.com");
		this.server.send(request);
		response = this.server.receive();

		return response;
	}

	// private HttpResponse recursiveGetResponse(final HttpRequest request) {
	//
	// HttpResponse response = null;
	//
	// this.server = this.getConnection(request.getHost());
	// this.server.send(request);
	//
	// response = this.server.receive();
	//
	// return response;
	//
	// }

	// TODO: Implementar el getConnection desde un host porque no hay otra
	// forma, agregar un mapa con conexiones a host y que el mismo tenga varias
	// conexiones disponbiles para ese host.

	public Connection getConnection(final String host) {
		final EndPointConnectionHandler hostConnections = this.connections
				.getEndPointConnectionHandler(host);
		return hostConnections.getConnection();
	}

	private HttpRequest getRequest() {

		HttpRequest request;
		final InputStream input = this.client.getInputStream();
		request = new HttpRequestImpl(input);
		return request;
	}

}
