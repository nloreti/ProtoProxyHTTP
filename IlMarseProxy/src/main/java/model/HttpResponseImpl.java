package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import exceptions.BadResponseException;

public class HttpResponseImpl implements HttpResponse {

	private InputStream in;
	private int statusCode;
	private String reasonPhrase;
	private Map<String, String> headers;
	private String pVersion;
	private byte[] entityBody;

	public HttpResponseImpl(final InputStream in) throws BadResponseException {
		this.in = in;
		this.headers = new HashMap<String, String>();

		final String[] status_line = this.readLine(in).split(" ", 3);
		if (status_line.length != 2 && status_line.length != 3) {
			throw new BadResponseException();
		}

		final String pVersion = status_line[0];
		if (!"HTTP/1.1".equals(pVersion) && !"HTTP/1.0".equals(pVersion)) {
			throw new BadResponseException("Protocolo no soportado.");
		}
		this.pVersion = pVersion;

		try {
			this.statusCode = Integer.valueOf(status_line[1]);
		} catch (final NumberFormatException e) {
			throw new BadResponseException();
		}

		if (status_line.length == 3) {
			this.reasonPhrase = (status_line[2]);
		} else {
			this.reasonPhrase = "";
		}

		String s = null;
		while (!"".equals(s = this.readLine(in))) {
			final String[] headerLine = s.split(":", 2);
			this.appendHeader(headerLine[0].trim(), headerLine[1].trim());
		}
	}

	private void appendHeader(final String key, final String value) {
		this.headers.put(key, value);
	}

	private String readLine(final InputStream in) throws BadResponseException {
		final ByteArrayOutputStream b = new ByteArrayOutputStream();

		int c;
		while ((c = this.read(in)) != '\r' && c != '\n' && c != -1) {
			b.write(c);
		}

		if ((c == '\r' && this.read(in) != '\n') || c == -1) {
			if (c != -1) {
				throw new BadResponseException();
			} else {
				throw new BadConnectionException();
			}
		}
		String line = null;
		try {
			line = new String(b.toByteArray(), "ISO-8859-1");
		} catch (final UnsupportedEncodingException e) {
		}
		return line;
	}

	private int read(final InputStream in) {
		try {
			return in.read();
		} catch (final InterruptedIOException e) {
			throw new BadServerException();
		} catch (final IOException e) {
			throw new BadConnectionException();
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
}
