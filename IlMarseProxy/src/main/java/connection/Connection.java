package connection;

import java.io.InputStream;
import java.net.InetAddress;
import java.rmi.ServerException;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import exceptions.EncodingException;
import exceptions.ResponseException;

public interface Connection {

	public void send(HttpRequestImpl request);

	public void send(HttpResponseImpl response);

	public HttpResponseImpl receive() throws ResponseException,
			ServerException, EncodingException;

	public InputStream getInputStream();

	public InetAddress getRemoteIP();

	public InetAddress getSourceIP();

	public void close();

	public boolean isClosed();

}
