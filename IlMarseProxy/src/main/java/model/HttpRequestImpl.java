package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.ServerException;
import java.util.List;
import java.util.Map.Entry;

import exceptions.BadMessageException;
import exceptions.EncodingException;
import exceptions.RequestException;
import exceptions.ResponseException;

public class HttpRequestImpl extends HttpMsg {

	/**
	 * @author Nloreti
	 */
	public enum ImplementedMethod {
		GET, HEAD, POST;

		ImplementedMethod() {
		}

		public static ImplementedMethod getMethod(final String name) {
			final ImplementedMethod[] methods = ImplementedMethod.values();

			for (final ImplementedMethod m : methods) {
				if (m.name().equals(name)) {
					return m;
				}
			}
			return null;// TODO: Devolver Error;

		}
	}

	private ImplementedMethod method;
	private URI requestURI;
	private HttpVersion version;

	public HttpRequestImpl(final InputStream in) throws ResponseException,
			EncodingException, ServerException, BadMessageException {

		// this.in = in;
		super(in);

		final String[] firstLine = this.readLine().split(" ");
		this.parseFirstLine(firstLine);

		final String empty = "";
		String headerLine = null;
		while (!empty.equals(headerLine = this.readLine())) {
			this.parseHeaderLine(headerLine);
		}

		if (this.getHost() == null) {
			System.out.println("FALTA HOST EN EL REQUEST");
		}

	}

	public void setRequestURI(final URI requestURI) {
		this.requestURI = requestURI;
	}

	@Override
	void parseFirstLine(final String[] line) throws RequestException {
		if (line.length != 3) {
			throw new RequestException("Error parseando la primera linea");
		}

		this.method = ImplementedMethod.getMethod(line[0]);

		if (!this.isSupportedMethod()) {
			System.out.println("METHOD INVALIDO");
		}

		try {
			this.requestURI = new URI(line[1]);
		} catch (final URISyntaxException e) {
			System.out.println("BAD URI");
		}

		final String protocol = line[2];

		if (protocol.equals("HTTP/1.0")) {
			this.version = HttpVersion.HTTP_1_0;
		} else {
			if (protocol.equals("HTTP/1.1")) {
				this.version = HttpVersion.HTTP_1_1;
			} else {
				System.out.println("BAD REQUEST");
				this.version = HttpVersion.BAD_REQUEST;
			}
		}

		this.setProtocol(protocol);

	}

	public ImplementedMethod getMethod() {
		return this.method;
	}

	public URI getRequestURI() {
		return this.requestURI;
	}

	public HttpVersion getVersion() {
		return this.version;
	}

	@Override
	public String toString() {
		if (this.version.equals(HttpVersion.BAD_REQUEST)) {
			return "";
		}
		final String v = this.version.equals(HttpVersion.HTTP_1_0) ? "HTTP/1.0"
				: "HTTP/1.1";
		String s = this.method + " " + this.requestURI + " " + v + "\r\n";
		for (final String header : this.getHeaders().keySet()) {
			s = s + header + ": " + this.getHeader(header) + "\r\n";
		}
		return s + "\r\n";

	}

	@Override
	public void writeStream(final OutputStream out) throws ServerException {
		byte[] bytes;
		bytes = (this.method + " " + this.requestURI + " " + this.getProtocol() + "\r\n")
				.getBytes();
		this.write(out, bytes);
		for (final Entry<String, List<String>> e : this.getHeaders().entrySet()) {
			for (final String headerValue : e.getValue()) {
				bytes = (e.getKey() + ": " + headerValue + "\r\n").getBytes();
				this.write(out, bytes);
			}
		}
		bytes = "\r\n".getBytes();
		this.write(out, bytes);
		this.writeBodyStream(out);

	}

	@Override
	public String getHost() {
		return this.getHeader("Host");
	}

	@Override
	void writeBodyStream(final OutputStream out) throws ServerException,
			NumberFormatException {

		if ("HEAD".equals(this.method)) {
			return;
		}

		if (this.getHeader("Content-Length") != null) {
			int contentLength = Integer.valueOf(this
					.getHeader("Content-Length"));

			while (--contentLength >= 0) {
				final byte b = (byte) this.read();
				this.write(out, b);
			}
		} else if ("close".equals(this.getHeader("Proxy-Connection"))) {

			int b;
			while ((b = this.read()) != -1) {
				this.write(out, b);
			}
		}
		this.setBody(new byte[0]);

	}

	public void write(final OutputStream out, final int c) {
		try {
			out.write(c);
			this.written++;
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(final OutputStream out, final byte[] bytes) {
		try {
			out.write(bytes);
			this.written += bytes.length;
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isSupportedMethod() {
		return this.method.equals(ImplementedMethod.HEAD)
				|| this.method.equals(ImplementedMethod.GET)
				|| this.method.equals(ImplementedMethod.POST);
	}

	public String getDestinationIp() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLogString() {
		return "to: " + this.getRequestURI() + "\nheaders: "
				+ this.getHeaders().toString();
	}
}
