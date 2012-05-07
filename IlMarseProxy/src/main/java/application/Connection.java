package application;

import java.io.InputStream;
import java.net.InetAddress;

import model.HttpRequestStringImpl;
import model.HTTPResponse;

public interface Connection {

	public void send(HTTPResponse response);

	public void send(HttpRequestStringImpl request);

	public HTTPResponse receive();

	public InputStream getInputStream();

	public InetAddress getRemoteIP();

	public void close();

	public boolean isClosed();

}
