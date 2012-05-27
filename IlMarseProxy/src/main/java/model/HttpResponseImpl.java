package model;

import java.io.InputStream;

import exceptions.BadResponseException;

public class HttpResponseImpl extends HttpMsg implements HttpResponse {

	private int statusCode;
	private String reasonPhrase;
	private String pVersion;
	private byte[] entityBody;

	public HttpResponseImpl(final InputStream in) throws BadResponseException {
		super(in);
		

		String s = null;
		while (!"".equals(s = readLine())) {
			final String[] headerLine = s.split(":", 2);
			this.appendHeader(headerLine[0].trim(), headerLine[1].trim());
		}
	}

	private void appendHeader(final String key, final String value) {
		setHeader(key, value);
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
	void parseFirstLine(String line) {

		final String[] status_line = readLine().split(" ", 3);
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

		
	}
}
