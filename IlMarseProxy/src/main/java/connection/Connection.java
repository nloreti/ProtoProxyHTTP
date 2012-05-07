package connection;

import java.io.InputStream;
import java.net.InetAddress;

import model.HttpRequest;
import model.HttpResponse;

public interface Connection {

	public void send(HttpResponse response);

	public void send(HttpRequest request);

	public HttpResponse receive();

	public InputStream getInputStream();

	public InetAddress getRemoteIP();

	public void close();

	public boolean isClosed();

}
