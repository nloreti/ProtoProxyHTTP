package application;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

public class RequestFilter {

	private static RequestFilter instance = null;
	private boolean images;
	private boolean leet;
	private boolean access;
	private Set<String> ips;
	private Set<String> uris;
	private Set<String> mediaTypes;
	private int maxSize;

	public static RequestFilter getInstance() {
		if (instance == null) {
			instance = new RequestFilter();
		}
		return instance;
	}

	public RequestFilter() {
		this.images = true;
		this.leet = false;
		this.access = true;
		this.maxSize = 0;
		this.ips = new HashSet<String>();
		this.uris = new HashSet<String>();
		this.mediaTypes = new HashSet<String>();
	}

	public HttpResponseImpl filter(final HttpRequestImpl request) {
		return null;
	}

	public boolean images() {
		return this.images;
	}

	public boolean setMaxSize(final int ms) {
		this.maxSize = ms;
		return this.hasMaxSize();
	}

	public boolean hasMaxSize() {
		return this.maxSize != 0;
	}

	public boolean blockMediaType(final String mediaType) {
		return this.mediaTypes.add(mediaType);
	}

	public boolean unlockMediaType(final String mediaType) {
		return this.mediaTypes.remove(mediaType);
	}

	public boolean unlockUri(final String uri) {
		return this.uris.remove(uri);
	}

	public boolean blockUri(final String uri) {
		return this.uris.add(uri);
	}

	public boolean access() {
		return this.access;
	}

	public void accessOff() {
		this.access = false;
	}

	public void accessOn() {
		this.access = true;
	}

	public void leetOn() {
		this.leet = true;
	}

	public void leetOff() {
		this.leet = false;
	}

	public boolean leet() {
		return this.leet;
	}

	public void imagesOff() {
		this.images = false;
	}

	public void imagesOn() {
		this.images = true;
	}

	public boolean blockIP(final String ip) {
		return this.ips.add(ip);
	}

	public boolean unlockIP(final String ip) {
		return this.ips.remove(ip);
	}

	public HttpResponseImpl doFilter(final HttpRequestImpl request,
			final HttpResponseImpl response) {
		if (!this.access) {
			return this.generateBlockedResponse();
		}
		if (this.ips.contains(request.getDestinationIp())) {
			return this.generateBlockedResponseByIp(request.getDestinationIp());
		}
		if (this.images) {
			if (response.containsType("image/.*")) {
				this.rotateImage(response);
			}
		}
		if (this.leet) {
			if (response.containsType("text/plain")) {
				String body = new String(response.getBody());
				body = body.replace('a', '4').replace('e', '3')
						.replace('i', '1').replace('o', '0');
				response.setBody(body.getBytes());
			}
		}
		if (this.uris.contains(request.getRequestURI())) {
			return this.generateBlockedResponseByUri(request.getRequestURI()
					.toString());
		}
		if (response.getContentLength() > this.maxSize) {
			return this.generateBlockedResponse(this.maxSize);
		}
		if (this.mediaTypes.contains(request.getMediaType())) {
			return this.generateBlockedResponseByMediaType(request
					.getMediaType());
		}
		return response;
	}

	private void rotateImage(final HttpResponseImpl response) {
		try {
			final byte[] bodyImageBytes = response.getBody();
			final String format = response.getHeader("Content-Type").split("/")[1];
			final byte[] imageBytes = this.rotateBytes(bodyImageBytes, format);
			response.replaceHeader("Content-Length",
					String.valueOf(imageBytes.length));
			response.removeHeader("Content-Encoding");
			response.setBody(imageBytes);
		} catch (final Exception e) {
			return;
		}

	}

	private byte[] rotateBytes(final byte[] rawImageBytes, final String format) {
		int width;
		int height;
		final double radians = Math.PI;// 180 grados
		BufferedImage newImage = null;
		BufferedImage oldImage = null;
		final ByteArrayOutputStream resp;

		try {
			oldImage = ImageIO.read(new ByteArrayInputStream(rawImageBytes));

		} catch (final IOException e) {
			e.printStackTrace();
		}

		width = oldImage.getWidth();
		height = oldImage.getHeight();

		try {
			newImage = new BufferedImage(width, height, oldImage.getType());
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}
		final Graphics2D graph = newImage.createGraphics();
		graph.rotate(radians, width / 2, height / 2);
		graph.drawImage(oldImage, null, width, height);

		resp = new ByteArrayOutputStream(width * height);
		try {
			ImageIO.write(newImage, format, resp);
		} catch (final IOException e) {
			System.out.println("error al guardar la imagen");
			e.printStackTrace();
		}

		try {
			resp.flush();
		} catch (final IOException e) {
			System.out.println("flush error");
			e.printStackTrace();
		}

		return resp.toByteArray();
	}

	private HttpResponseImpl generateBlockedResponseByIp(
			final String destinationIp) {
		return this
				.generateBlockedResponse("This ip has been blocked by the proxy administrator. IP: "
						+ destinationIp);
	}

	private HttpResponseImpl generateBlockedResponseByMediaType(
			final String mediaType) {
		return this
				.generateBlockedResponse("This media type has been blocked by the proxy administrator. Media type: "
						+ mediaType);
	}

	private HttpResponseImpl generateBlockedResponse() {
		return this
				.generateBlockedResponse("The access has been completely blocked by the proxy administrator.");
	}

	private HttpResponseImpl generateBlockedResponseByUri(
			final String requestURI) {
		return this
				.generateBlockedResponse("This URI has been blocked by the proxy administrator. URI: "
						+ requestURI);
	}

	private HttpResponseImpl generateBlockedResponse(final int size) {
		return this
				.generateBlockedResponse("The resource you are trying to reach is too big. Size: "
						+ size);
	}

	private HttpResponseImpl generateBlockedResponse(final String string) {
		try {
			final HttpResponseImpl response = new HttpResponseImpl(null);
			response.addHeader("MIME TYPE", "HTML");// TODO hacer bien esto xq
													// no tengo idea
			final String body = "<body>" + string + "</body>";
			response.setBody(body.getBytes());
			return response;
		} catch (final Exception e) {
			return null;
		}
	}
}
