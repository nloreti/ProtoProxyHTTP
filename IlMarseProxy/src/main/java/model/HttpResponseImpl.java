package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;

import exceptions.BadResponseException;
import exceptions.BadServerException;

public class HttpResponseImpl extends HttpMsg {

	private int statusCode;
	private String reasonPhrase;
	private String pVersion;
	private byte[] entityBody;
	private int bytesSent = 0;
	private int bytesRecieved = 0;
	private boolean completed = false;
	private int limit;
	private boolean contentByClosedConnection = false;
	InputStream in;

	public HttpResponseImpl(final InputStream in) throws BadResponseException {
		this.in = in;

		String s = null;
		while (!"".equals(s = readLine())) {
			final String[] headerLine = s.split(":", 2);
			this.appendHeader(headerLine[0].trim(), headerLine[1].trim());
		}

		if ("close".equals(this.getHeader("Proxy-Connection"))
				|| "close".equals(this.getHeader("Connection"))) {
			this.contentByClosedConnection = true;
		}
	}

	@Override
	private void appendHeader(final String key, final String value) {
		this.setHeader(key, value);
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
	void parseFirstLine(final String line) {

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

	public void writeToStream(final OutputStream out)
			throws BadMessageException {
		byte[] bytes;
		try {
			bytes = (this.getProtocolVersion() + " " + this.getStatusCode()
					+ " " + this.getReasonPhrase() + "\r\n").getBytes();
			out.write(bytes);
			this.bytesSent += bytes.length;
			for (final Entry<String, String> e : this.getHeaders().entrySet()) {
				bytes = (e.getKey() + ": " + e.getValue() + "\r\n").getBytes();
				out.write(bytes);
				this.bytesSent += bytes.length;
			}
			bytes = "\r\n".getBytes();
			out.write(bytes);
			this.bytesSent += bytes.length;

			if (this.completed) {
				bytes = this.getEntityBody();
				out.write(this.getEntityBody());
				this.bytesSent += bytes.length;
			} else {
				// Se transfiere directamente, sin bufferear el contenido.
				final int transferred = this.writeBodyToStream(out);
				this.bytesRecieved += transferred;
				this.bytesSent += transferred;
			}

		} catch (final IOException e) {
			throw new BadResponseException();
		}
	}

	private int writeBodyToStream(final OutputStream out)
			throws BadResponseException {
		int transfered = 0;
		try {
			if (this.getHeader("Content-Length") != null) {
				int clength = Integer.valueOf(this.getHeader("Content-Length"));

				this.validateLimit(transfered += clength);
				while (--clength >= 0) {
					final int c = this.read();
					if (c == -1) {
						throw new BadServerException();
					}
					this.write(out, c);
				}
			} else if (this.contentByClosedConnection) {
				int c;
				while ((c = this.read()) != -1) {
					this.write(out, c);
					this.validateLimit(transfered += 1);
				}
			} else if ("chunked".equals(this.getHeader("Transfer-Encoding"))) {
				byte[] bytes;
				String stringChunkLength = null;
				while (!(stringChunkLength = readLine()).matches("0+")) {
					bytes = stringChunkLength.getBytes();
					this.write(out, bytes);
					transfered = bytes.length;
					this.write(out, "\r\n".getBytes());
					this.validateLimit(transfered += 2);

					int chunkLength;
					try {
						chunkLength = Integer.parseInt(stringChunkLength, 16);
					} catch (final NumberFormatException e) {
						throw new BadResponseException();
					}

					this.validateLimit(transfered += chunkLength);
					while (--chunkLength >= 0) {
						final int c = this.read();
						if (c == -1) {
							throw new BadServerException();
						}
						this.write(out, c);
					}

					readLine();
					this.write(out, "\r\n".getBytes());
					this.validateLimit(transfered += 2);
				}
				this.write(out, '0');
				this.validateLimit(transfered += 1);
				this.write(out, "\r\n".getBytes());
				this.validateLimit(transfered += 2);

				readLine();
				this.write(out, "\r\n".getBytes());
				this.validateLimit(transfered += 2);
			}
			this.completed = true;
		} catch (final ResponseTooLargeException e) {
			throw e;
		}
		return transfered;
	}

	private void validateLimit(final Integer count) {
		if (this.limit != 0 && count > this.limit) {
			throw new ResponseTooLargeException();
		}
	}

	public void setLengthLimit(final Integer limit) {
		this.limit = limit;
	}

	public byte[] getEntityBody() throws BadMessageException {
		if (!this.completed) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			this.bytesRecieved += this.writeBodyToStream(out);
			this.entityBody = out.toByteArray();
		}
		return this.entityBody;
	}

	protected int read() {
		try {
			return this.in.read();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void write(final OutputStream out, final int b) {

		try {
			out.write(b);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void write(final OutputStream out, final byte[] bytes) {

		try {
			out.write(bytes);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
