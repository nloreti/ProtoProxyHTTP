package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.EncodingException;
import exceptions.MessageException;

public abstract class HttpMsg {
	public enum HttpVersion {
		HTTP_1_0, HTTP_1_1, BAD_REQUEST
	}

	// private Map<String,String> headers = new HashMap<String, String>();
	// final InputStream in;
	int read = 0;
	int written = 0;
	private Map<String, List<String>> headers;
	private String protocol;
	private byte[] body;
	private InputStream in;

	public HttpMsg(final InputStream in) {
		this.in = in;
		this.headers = new HashMap<String, List<String>>();
	}

	public void parseHeaderLine(final String line) {
		final String[] aux = line.split(":", 2);
		if (aux.length > 1) {
			this.appendHeader(aux[0].trim(), aux[1].trim());
		}
	}

	public void appendHeader(final String header, final String value) {
		List<String> values = this.headers.get(header);
		if (values == null) {
			values = new ArrayList<String>();
			this.headers.put(header, values);
		}
		values.add(value);
	}

	public void replaceHeader(final String header, final String value) {
		final List<String> values = new ArrayList<String>();
		values.add(value);
		this.headers.put(header, values);
	}

	public void addHeader(final String header, final String value) {
		List<String> values = this.headers.get(header);
		if (values == null) {
			values = new ArrayList<String>();
			this.headers.put(header, values);
		}
		this.headers.get(header).add(value);
	}

	public String getHeader(final String key) {
		final List<String> values = this.headers.get(key);
		if (values == null) {
			return null;
		}
		return values.get(0);
	}

	public void removeHeader(final String header) {
		if (this.headers.containsKey(header)) {
			this.headers.remove(header);
		}
	}

	public List<String> getHeaders(final String key) {
		return this.headers.get(key);
	}

	public Map<String, List<String>> getHeaders() {
		return this.headers;
	}

	public void setHeaders(final Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public void setHeader(final String header, final List<String> value) {
		this.headers.put(header, value);
	}

	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(final String protocol) {
		this.protocol = protocol;
	}

	public byte[] getBody() {
		return this.body;
	}

	public void setBody(final byte[] body) {
		this.body = body;
	}

	public String readLine() throws EncodingException, ServerException {
		final ByteArrayOutputStream b = new ByteArrayOutputStream();
		final String encoding = "ISO-8859-1";// Default Encoding

		int c;
		while ((c = this.read()) != '\r' && c != '\n' && c != -1) {
			b.write(c);
		}

		if ((c == '\r' && this.read() != '\n') || c == -1) {
			if (c != -1) {
				System.out.println("Fin de línea incorrecto.");
			}
		}

		String line = null;
		try {
			line = new String(b.toByteArray(), encoding);
		} catch (final UnsupportedEncodingException e) {
			throw new EncodingException("ISO-8859-1");
		}

		return line;
	}

	public int read() throws ServerException {
		try {
			// System.out.println("READ: " + this.in.toString());
			this.read++;
			return this.in.read();
		} catch (final IOException e) {
			throw new ServerException("Connection Problem");
		}
	}

	public abstract void writeStream(OutputStream out) throws ServerException,
			MessageException;

	abstract void writeBodyStream(OutputStream out) throws ServerException;

	abstract String getHost();

	abstract void parseFirstLine(String[] line);

	public int getRead() {
		return this.read;
	}

	public int getWritten() {
		return this.written;
	}
}
