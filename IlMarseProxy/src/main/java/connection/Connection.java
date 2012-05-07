package connection;

import java.io.InputStream;
import java.net.InetAddress;

import model.HttpRequest;
import model.HttpResponse;
import exceptions.BadResponseException;

public interface Connection {

	public void send(HttpResponse response);

	public void send(HttpRequest request);

	public HttpResponse receive() throws BadResponseException;

	public InputStream getInputStream();

	public InetAddress getRemoteIP();

	public void close();

	public boolean isClosed();

}
