package connection;

import java.io.InputStream;
import java.net.InetAddress;

import model.HTTPRequest;
import model.HTTPResponse;
import model.HttpResponseImpl;

public interface Connection {

	public void send(HTTPResponse response);

	public void send(HTTPRequest request);

	public HttpResponseImpl receive();

	public InputStream getInputStream();

	public InetAddress getRemoteIP();

	public void close();

	public boolean isClosed();

}
