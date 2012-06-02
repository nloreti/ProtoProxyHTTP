package application;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

public class RequestFilter {

	private static RequestFilter instance = null;
	private boolean images;
	private boolean leet;
	private boolean access;
	private List<String> ips;
	private List<URI> uris;
	private Set<String> mediaTypes;
	private int maxSize;

	public static RequestFilter getInstance() {
		if (instance == null) {
			instance = new RequestFilter();
		}
		return instance;
	}

	public RequestFilter() {
		this.images = false;
		this.leet = false;
		this.access = true;
		this.maxSize = Integer.MAX_VALUE;
		this.ips = new ArrayList<String>();
		this.uris = new ArrayList<URI>();
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

	public boolean unlockUri(final URI uri) {
		return this.uris.remove(uri);
	}

	public boolean blockUri(final URI uri) {
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

		try {
			if (!this.access) {
				return this.generateBlockedResponse(response);
			}
			if (this.destinationIPIsBlocked(request)) {
				return this.generateBlockedResponseByIp(
						InetAddress.getByName(request.getHost())
								.getHostAddress(), response);
			}
			if (this.images) {
				if (response.containsType("image/.*")) {
					System.out.println("ENTRA");
					this.rotateImage(response);
				}
			}
			if (this.leet) {
				if (response.containsType("text/plain.*")) {
					String body = new String(response.getBody());
					body = body.replace('a', '4').replace('e', '3')
							.replace('i', '1').replace('o', '0');
					response.setBody(body.getBytes());
				}
			}
			if (this.urisBlocked(request)) {
				return this.generateBlockedResponseByUri(request
						.getRequestURI().toString(), response);
			}
			if (response.getContentLength() != null) {
				if (Integer.valueOf(response.getContentLength()) > this.maxSize) {
					return this.generateBlockedResponse(this.maxSize, response);
				}
			}
			if (this.mediaTypes.contains(request.getMediaType())) {
				return this.generateBlockedResponseByMediaType(
						request.getMediaType(), response);
			}
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
		return response;
	}

	private boolean urisBlocked(final HttpRequestImpl request) {
		int i;
		for (i = 0; i < this.uris.size(); i++) {
			if (request.getRequestURI().equals(this.uris.get(i))) {
				return true;
			}
		}
		return false;
	}

	private boolean destinationIPIsBlocked(final HttpRequestImpl request) {

		int i = 0;
		InetAddress requestIP;
		try {
			requestIP = InetAddress.getByName(request.getHost());

			for (i = 0; i < this.ips.size(); i++) {
				final InetAddress listIP = InetAddress.getByName(this.ips
						.get(i));
				if ((listIP.getHostAddress())
						.equals(requestIP.getHostAddress())) {
					return true;
				}
			}
		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
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
			e.printStackTrace();
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
		graph.drawImage(oldImage, null, 0, 0);

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
			final String destinationIp, final HttpResponseImpl response) {
		return this.generateBlockedResponse(
				"This ip has been blocked by the proxy administrator. IP: "
						+ destinationIp, response);
	}

	private HttpResponseImpl generateBlockedResponseByMediaType(
			final String mediaType, final HttpResponseImpl response) {
		return this.generateBlockedResponse(
				"This media type has been blocked by the proxy administrator. Media type: "
						+ mediaType, response);
	}

	private HttpResponseImpl generateBlockedResponse(
			final HttpResponseImpl response) {
		return this
				.generateBlockedResponse(
						"The access has been completely blocked by the proxy administrator.",
						response);
	}

	private HttpResponseImpl generateBlockedResponseByUri(
			final String requestURI, final HttpResponseImpl response) {
		return this.generateBlockedResponse(
				"This URI has been blocked by the proxy administrator. URI: "
						+ requestURI, response);
	}

	private HttpResponseImpl generateBlockedResponse(final int size,
			final HttpResponseImpl response) {
		return this.generateBlockedResponse(
				"The resource you are trying to reach is too big. Size: "
						+ size, response);
	}

	private HttpResponseImpl generateBlockedResponse(final String string,
			final HttpResponseImpl response) {
		try {
			final String body = "<title>Feedback Page</title><html><body><h1>"
					+ string + "<h1></body></html>";

			response.appendHeader("Content-Length",
					String.valueOf(body.length()));
			response.removeHeader("Content-Encoding");
			response.setBody(body.getBytes());

			// if (response.getHeader("Content-Type") != null) {
			// response.removeHeader("Content-Type");
			// }
			// response.addHeader("Content-Type", "text/html");
			// System.out.println(new String(response.getBody(), "UTF-8"));
			System.out.println("ENTRE");
			// response.setBody(body.getBytes());
		} catch (final Exception e) {
			e.printStackTrace();

		}
		return response;
	}
}
