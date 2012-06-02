package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.ServerException;
import java.util.List;
import java.util.Map.Entry;

import exceptions.EncodingException;
import exceptions.ResponseException;

public class HttpResponseImpl extends HttpMsg {

	private int statusCode;
	private String reasonPhrase;
	private String pVersion;
	private boolean isBodyCached = false;
	// private int limit;
	private boolean contentByClosedConnection = false;

	public HttpResponseImpl(final InputStream in) throws ResponseException,
			EncodingException, ServerException {
		super(in);

		final String[] requestLine = this.readLine().split(" ", 3);

		this.parseFirstLine(requestLine);

		final String empty = "";
		String s = null;
		while (!empty.equals(s = this.readLine())) {
			final String[] headerLine = s.split(":", 2);
			this.appendHeader(headerLine[0].trim(), headerLine[1].trim());
		}

		if ("close".equals(this.getHeader("Proxy-Connection"))
				|| "close".equals(this.getHeader("Connection"))) {
			this.contentByClosedConnection = true;
		}
	}

	public String getProtocolVersion() {
		return this.pVersion;
	}

	public String getReasonPhrase() {
		return this.reasonPhrase;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	@Override
	void parseFirstLine(final String[] line) {

		if (line.length != 2 && line.length != 3) {
			throw new ResponseException();
		}

		final String pVersion = line[0];
		if (!"HTTP/1.1".equals(pVersion) && !"HTTP/1.0".equals(pVersion)) {
			throw new ResponseException("Protocolo no soportado.");
		}
		this.pVersion = pVersion;
		this.setProtocol(pVersion);
		try {
			this.statusCode = Integer.valueOf(line[1]);
		} catch (final NumberFormatException e) {
			throw new ResponseException();
		}

		if (line.length == 3) {
			this.reasonPhrase = (line[2]);
		} else {
			this.reasonPhrase = "";
		}

	}

	// public void setLengthLimit(final Integer limit) {
	// this.limit = limit;
	// }

	@Override
	public byte[] getBody() {
		if (!this.isBodyCached) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				this.writeBodyStream(out);
			} catch (final ServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.setBody(out.toByteArray());
		}
		return super.getBody();
	}

	@Override
	public void setBody(final byte[] body) {
		this.isBodyCached = true;
		super.setBody(body);
	}

	void write(final OutputStream out, final int b) {

		try {
			out.write(b);
		} catch (final IOException e) {
			System.out.println("PROBLEMA DE CONEXION CON EL CLIENTE");
			e.printStackTrace();
		}

	}

	void write(final OutputStream out, final byte[] bytes) {

		try {
			out.write(bytes);
		} catch (final IOException e) {
			System.out.println("PROBLEMA DE CONEXION CON EL CLIENTE");
			e.printStackTrace();
		}
	}

	@Override
	public void writeStream(final OutputStream out) {

		byte[] bytes;

		try {
			bytes = (this.getProtocolVersion() + " " + this.getStatusCode()
					+ " " + this.getReasonPhrase() + "\r\n").getBytes();
			out.write(bytes);

			for (final Entry<String, List<String>> e : this.getHeaders()
					.entrySet()) {
				for (final String headerValue : e.getValue()) {
					bytes = (e.getKey() + ": " + headerValue + "\r\n")
							.getBytes();
					out.write(bytes);
				}
			}
			bytes = "\r\n".getBytes();
			out.write(bytes);
			if (!this.isBodyCached) {
				this.writeBodyStream(out);
			} else {
				out.write(this.getBody());
			}

		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	void writeBodyStream(final OutputStream out) throws ServerException {

		// final ByteArrayOutputStream outArray = new ByteArrayOutputStream();

		try {
			if (this.getHeader("Content-Length") != null) {
				int clength = Integer.valueOf(this.getHeader("Content-Length"));

				while (--clength >= 0) {
					final int c = this.read();
					if (c == -1) {
						System.out.println("Se cerro la conexion del cliente");
					}
					this.write(out, c);
					// outArray.write(c);
				}
			} else if (this.contentByClosedConnection) {
				int c;
				while ((c = this.read()) != -1) {
					this.write(out, c);
					// outArray.write(c);
				}
			} else if ("chunked".equals(this.getHeader("Transfer-Encoding"))) {
				System.out.println("CAYO EN CHUNKED!");
			}
			// if (!this.completed) {
			// this.setBody(outArray.toByteArray());
			// }
			this.isBodyCached = true;
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	String getHost() {
		return this.getHeader("Host");
	}

	@Override
	public String toString() {
		final StringBuffer b = new StringBuffer();

		b.append(this.getProtocol() + " " + this.getStatusCode() + " "
				+ this.getReasonPhrase() + "\r\n");
		for (final Entry<String, List<String>> e : this.getHeaders().entrySet()) {
			for (final String headerValue : e.getValue()) {
				b.append(e.getKey() + ": " + headerValue + "\r\n");
			}
		}
		b.append("\r\n");
		return b.toString();
	}

	public String getContentLength() {
		return this.getHeader("Content-Length");
	}

	public boolean containsType(final String string) {

		if (this.getHeader("Content-Type") != null) {
			return this.getHeader("Content-Type").matches(string);
		}

		return false;
	}

	public String getLogString(){
		return String.valueOf(getStatusCode());
	}
}
