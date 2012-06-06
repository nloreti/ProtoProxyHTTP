package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.ServerException;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import exceptions.ClientException;
import exceptions.CloseException;
import exceptions.EncodingException;
import exceptions.MessageException;
import exceptions.ResponseException;

public class HttpResponseImpl extends HttpMsg {

	private String reason;
	private String pVersion;
	private int statusCode;
	private boolean isBodyCached = false;
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
			this.reason = (line[2]);
		} else {
			this.reason = "";
		}

	}

	@Override
	public byte[] getBody() {
		if (!this.isBodyCached) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				this.writeBodyStream(out);
			} catch (final ServerException e) {
				throw new CloseException("Server Error in getBody");
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
			this.written++;
		} catch (final IOException e) {
			throw new ClientException("Problema en Conexion del Cliente");
		}

	}

	void write(final OutputStream out, final byte[] bytes) {

		try {
			out.write(bytes);
			this.written += bytes.length;
		} catch (final IOException e) {
			throw new ClientException("Problema en Conexion del Cliente");
		}
	}

	@Override
	public void writeStream(final OutputStream out) throws MessageException {

		byte[] bytes;

		try {
			bytes = (this.getProtocolVersion() + " " + this.getStatusCode()
					+ " " + this.getReasonPhrase() + "\r\n").getBytes();
			out.write(bytes);

			for (final Entry<String, List<String>> e : this.getHeaders()
					.entrySet()) {
				for (final String header : e.getValue()) {
					bytes = (e.getKey() + ": " + header + "\r\n").getBytes();
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

		} catch (final IOException e) {
			throw new MessageException("Message Error");
		}
	}

	@Override
	void writeBodyStream(final OutputStream out) throws ServerException {

		try {
			if (this.getHeader("Content-Length") != null) {
				int clength = Integer.valueOf(this.getHeader("Content-Length"));

				while (--clength >= 0) {
					final int c = this.read();
					if (c == -1) {
						System.out.println("Se cerro la conexion del cliente");
					}
					this.write(out, c);
				}
			} else if (this.contentByClosedConnection) {
				int c;
				while ((c = this.read()) != -1) {
					this.write(out, c);
				}
			} else if ("chunked".equals(this.getHeader("Transfer-Encoding"))) {
				// System.out.println("CAYO EN CHUNKED - IMPLEMENTAR!");
				byte[] bytes;
				String stringChunk = null;
				final String CRLF = "\r\n";
				final int radix = 16;
				int chunkLength;
				while (!(stringChunk = this.readLine()).matches("0+")) {
					bytes = stringChunk.getBytes();
					this.write(out, bytes);
					this.write(out, CRLF.getBytes());
					try {
						chunkLength = Integer.parseInt(stringChunk, radix);
					} catch (final NumberFormatException e) {
						throw new ResponseException();
					}

					while (--chunkLength >= 0) {
						final int c = this.read();
						if (c == -1) {
							throw new ServerException("Fallo del server");
						}
						this.write(out, c);
					}
					this.readLine();
					this.write(out, CRLF.getBytes());
				}
				this.write(out, '0');
				this.write(out, CRLF.getBytes());
				this.readLine();
				this.write(out, CRLF.getBytes());

			}
			this.isBodyCached = true;
		} catch (final EncodingException e) {
			throw new CloseException("Error de Encoding");
		}

	}

	public byte[] getBodyBytes() {

		byte[] data = this.getBody();
		GZIPInputStream isZipped;
		final ByteArrayOutputStream unZipped = new ByteArrayOutputStream();

		if (data == null) {
			return null;
		}

		try {
			if ("gzip".equals(this.getHeader("Content-Encoding"))) {
				isZipped = new GZIPInputStream(new ByteArrayInputStream(data));
				int b;
				while ((b = isZipped.read()) != -1) {
					unZipped.write(b);
				}
				data = unZipped.toByteArray();
			}
		} catch (final IOException e) {
			throw new ResponseException();
		}
		return data;
	}

	public String getProtocolVersion() {
		return this.pVersion;
	}

	public String getReasonPhrase() {
		return this.reason;
	}

	public void setReasonPhrase(final String reason) {
		this.reason = reason;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(final int code) {
		this.statusCode = code;
	}

	@Override
	String getHost() {
		return this.getHeader("Host");

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

	public String getLogString() {
		return String.valueOf(this.getStatusCode());
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

}
