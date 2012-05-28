package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;

import exceptions.BadMessageException;
import exceptions.BadResponseException;
import exceptions.BadServerException;
import exceptions.ResponseTooLargeException;

public class HttpResponseImpl extends HttpMsg implements HttpResponse {

	private int statusCode;
	private String reasonPhrase;
	private String pVersion;
	private byte[] entityBody;
	private int bytesSent = 0;
	private int bytesRecieved = 0;
	private boolean completed = false;
	private int limit;
	private boolean contentByClosedConnection = false;

	public HttpResponseImpl(final InputStream in) throws BadResponseException {
		super(in);

		String s = null;
		while (!"".equals(s = readLine())) {
			final String[] headerLine = s.split(":", 2);
			this.appendHeader(headerLine[0].trim(), headerLine[1].trim());
		}

		if ("close".equals(getHeader("Proxy-Connection"))
				|| "close".equals(getHeader("Connection"))) {
			contentByClosedConnection = true;
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

	public void writeToStream(OutputStream out) throws BadMessageException {
		byte[] bytes;
		try {
			bytes = (getProtocolVersion() + " " + getStatusCode() + " "
					+ getReasonPhrase() + "\r\n").getBytes();
			out.write(bytes);
			bytesSent += bytes.length;
			for (Entry<String, String> e : getHeaders().entrySet()) {
				bytes = (e.getKey() + ": " + e.getValue() + "\r\n").getBytes();
				out.write(bytes);
				bytesSent += bytes.length;
			}
			bytes = "\r\n".getBytes();
			out.write(bytes);
			bytesSent += bytes.length;

			if (completed) {
				bytes = getEntityBody();
				out.write(getEntityBody());
				bytesSent += bytes.length;
			} else {
				// Se transfiere directamente, sin bufferear el contenido.
				int transferred = writeBodyToStream(out);
				bytesRecieved += transferred;
				bytesSent += transferred;
			}

		} catch (IOException e) {
			throw new BadResponseException();
		}
	}

	private int writeBodyToStream(OutputStream out) throws BadResponseException {
		int transfered = 0;
		try {
			if (getHeader("Content-Length") != null) {
				int clength = Integer.valueOf(getHeader("Content-Length"));

				validateLimit(transfered += clength);
				while (--clength >= 0) {
					int c = read();
					if (c == -1) {
						throw new BadServerException();
					}
					write(out, c);
				}
			} else if (contentByClosedConnection) {
				int c;
				while ((c = read()) != -1) {
					write(out, c);
					validateLimit(transfered += 1);
				}
			} else if ("chunked".equals(getHeader("Transfer-Encoding"))) {
				byte[] bytes;
				String stringChunkLength = null;
				while (!(stringChunkLength = readLine()).matches("0+")) {
					bytes = stringChunkLength.getBytes();
					write(out, bytes);
					transfered = bytes.length;
					write(out, "\r\n".getBytes());
					validateLimit(transfered += 2);

					int chunkLength;
					try {
						chunkLength = Integer.parseInt(stringChunkLength, 16);
					} catch (NumberFormatException e) {
						throw new BadResponseException();
					}

					validateLimit(transfered += chunkLength);
					while (--chunkLength >= 0) {
						int c = read();
						if (c == -1) {
							throw new BadServerException();
						}
						write(out, c);
					}

					readLine();
					write(out, "\r\n".getBytes());
					validateLimit(transfered += 2);
				}
				write(out, '0');
				validateLimit(transfered += 1);
				write(out, "\r\n".getBytes());
				validateLimit(transfered += 2);

				readLine();
				write(out, "\r\n".getBytes());
				validateLimit(transfered += 2);
			}
			completed = true;
		} catch (ResponseTooLargeException e) {
			throw e;
		}
		return transfered;
	}

	private void validateLimit(Integer count) {
		if (this.limit != 0 && count > limit) {
			throw new ResponseTooLargeException();
		}
	}

	public void setLengthLimit(Integer limit) {
		this.limit = limit;
	}

	public byte[] getEntityBody() throws BadMessageException {
		if (!completed) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bytesRecieved += writeBodyToStream(out);
			entityBody = out.toByteArray();
		}
		return entityBody;
	}
}
