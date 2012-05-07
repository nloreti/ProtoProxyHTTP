package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import exceptions.BadConnectionException;
import exceptions.BadResponseException;
import exceptions.BadServerException;

public class HttpResponseImpl implements HttpResponse {

	private InputStream in;
	private int statusCode;
	private String reasonPhrase;
	private Map<String, String> headers;
	private String pVersion;
	private byte[] entityBody;

	public HttpResponseImpl(InputStream in) throws BadResponseException {
		this.in = in;
		headers = new HashMap<String, String>();

		String[] status_line = readLine(in).split(" ", 3);
		if (status_line.length != 2 && status_line.length != 3) {
			throw new BadResponseException();
		}

		String pVersion = status_line[0];
		if (!"HTTP/1.1".equals(pVersion) && !"HTTP/1.0".equals(pVersion)) {
			throw new BadResponseException("Protocolo no soportado.");
		}
		this.pVersion = pVersion;

		try {
			statusCode = Integer.valueOf(status_line[1]);
		} catch (NumberFormatException e) {
			throw new BadResponseException();
		}

		if (status_line.length == 3) {
			reasonPhrase = (status_line[2]);
		} else {
			reasonPhrase = "";
		}

		String s = null;
		while (!"".equals(s = readLine(in))) {
			String[] headerLine = s.split(":", 2);
			appendHeader(headerLine[0].trim(), headerLine[1].trim());
		}
	}

	private void appendHeader(String key, String value) {
		headers.put(key, value);
	}

	private String readLine(InputStream in) throws BadResponseException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();

		int c;
		while ( (c = read(in)) != '\r' && c != '\n' && c != -1 ) {
			b.write(c);
		}

		if ( (c == '\r' && read(in) != '\n') || c == -1 ) {
			if ( c != -1 ) {
				throw new BadResponseException();
			} else {
				throw new BadConnectionException();
			}
		}
		String line = null;
		try {
			line = new String(b.toByteArray(), "ISO-8859-1"); 
		} catch ( UnsupportedEncodingException e ) {
		}
		return line;
	}
	
	private int read(InputStream in) {
		try {
			return in.read();
		} catch (InterruptedIOException e) {
			throw new BadServerException();
		} catch (IOException e) {
			throw new BadConnectionException();
		}
	}
	
	public String getProtocolVersion(){
		return pVersion;
	}
	
	public String getReasonPhrase(){
		return reasonPhrase;
	}
	
	public int getStatusCode(){
		return statusCode;
	}
}
