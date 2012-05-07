package application;

import java.io.InputStream;

import model.HTTPRequest;
import model.HTTPResponse;
import model.HttpRequestImpl;
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
		HTTPRequest request = null;
		HTTPResponse response = null;

		request = this.getRequest();
		response = this.getResponse();
		this.sendResponse(response);

	}

	private void sendResponse(final HTTPResponse response) {
		this.client.send(response);
	}

	private HTTPResponse getResponse(final HTTPRequest request) {

		HTTPResponse response;

		response = this.recursiveGetResponse(request);

		return response;
	}

	private HTTPResponse recursiveGetResponse(final HTTPRequest request) {

		HTTPResponse response = null;

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

	private HTTPRequest getRequest() {

		HTTPRequest request;
		final InputStream input = this.client.getInputStream();
		request = new HttpRequestImpl(input);
		return request;
	}

}
