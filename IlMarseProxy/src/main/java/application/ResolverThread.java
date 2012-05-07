package application;

import java.io.InputStream;

import model.HttpRequest;
import model.HttpRequestImpl;
import model.HttpResponse;
import connection.Connection;

public class ResolverThread implements Runnable {

	// Cliente
	Connection client;
	Connection server;

	// Configuracion del proxy
	private ProxyConfiguration configuration = DinamicProxyConfiguration
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

		HttpResponse response;

		response = this.recursiveGetResponse(request);

		return response;
	}

	private HttpResponse recursiveGetResponse(final HttpRequest request) {

		HttpResponse response = null;

		this.server = this.getConnection(request.getHost());
		this.server.send(request);

		response = this.server.receive();

		return response;

	}

	// TODO: Implementar el getConnection desde un host porque no hay otra
	// forma.
	public Connection getConnection(final String host) {
		final Connection connection = null;
		return connection;
	}

	private HttpRequest getRequest() {

		HttpRequest request;
		final InputStream input = this.client.getInputStream();
		request = new HttpRequestImpl(input);
		return request;
	}

}
