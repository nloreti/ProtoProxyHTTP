package connection;

import java.io.InputStream;
import java.net.InetAddress;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import exceptions.BadResponseException;

public interface Connection {

	public void send(HttpRequestImpl request);

	public void send(HttpResponseImpl response);

	public HttpResponseImpl receive() throws BadResponseException;

	public InputStream getInputStream();

	public InetAddress getRemoteIP();

	public void close();

	public boolean isClosed();

}
