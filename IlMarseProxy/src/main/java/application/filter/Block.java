package application.filter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import application.Statistics;
import exceptions.CloseException;
import exceptions.ImageException;

public abstract class Block {

	boolean images;
	boolean leet;
	boolean access;
	Set<InetAddress> ips;
	Set<URI> uris;
	Set<MediaType> mediaTypes;
	int maxSize;

	public Block() {
		this.images = false;
		this.leet = false;
		this.access = true;
		this.ips = new HashSet<InetAddress>();
		this.uris = new HashSet<URI>();
		this.mediaTypes = new HashSet<MediaType>();
		this.maxSize = 0;
	}

	public boolean blockMediaType(final String mediaType) {
		MediaType m;
		try {
			m = MediaType.valueOf(mediaType);
		} catch (final IllegalArgumentException e) {
			return false;
		}
		if (m == null) {
			return false;
		}
		return this.mediaTypes.add(m);
	}

	public boolean unlockMediaType(final String mediaType) {
		final MediaType m = MediaType.valueOf(mediaType);
		return this.mediaTypes.remove(m);
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
		try {
			final InetAddress address = InetAddress.getByAddress(ip.getBytes());
			this.ips.add(address);
		} catch (final UnknownHostException e) {
			return false;
		}
		return true;
	}

	public boolean unlockIP(final String ip) {
		return this.ips.remove(ip);
	}

	public abstract HttpResponseImpl doFilter(HttpRequestImpl req,
			HttpResponseImpl resp);

	HttpResponseImpl filter(final HttpRequestImpl req,
			final HttpResponseImpl resp) {
		try {
			if (!this.access) {
				return ResponseGenerator.generateBlockedResponse(resp);
			}
			if (this.destinationIPIsBlocked(req)) {
				Statistics.getInstance().incrementIpBlocks();
				return ResponseGenerator.generateBlockedResponseByIp(
						InetAddress.getByName(req.getHost()).getHostAddress(),
						resp);
			}
			if (this.images) {
				if (resp.containsType("image/.*")) {
					System.out.println("ENTRA a las fotos RequestFilter");
					Statistics.getInstance().incrementTransformations();
					this.rotateImage(resp);
				}
			}
			if (this.leet) {
				if (resp.containsType("text/plain.*")) {
					String body = new String(resp.getBody());
					body = body.replace('a', '4').replace('e', '3')
							.replace('i', '1').replace('o', '0');
					resp.setBody(body.getBytes());
				}
			}
			if (this.urisBlocked(req)) {
				Statistics.getInstance().incrementSiteBlocks();
				return ResponseGenerator.generateBlockedResponseByUri(req
						.getRequestURI().toString(), resp);
			}
			if (resp.getContentLength() != null) {
				if (this.maxSize != 0
						&& Integer.valueOf(resp.getContentLength()) > this.maxSize) {
					Statistics.getInstance().incrementSizeBlocks();
					return ResponseGenerator.generateBlockedResponse(
							this.maxSize, resp);
				}
			}

			if (this.isMediaTypeBlockable(resp)) {
				Statistics.getInstance().incrementContentBlocks();
				return ResponseGenerator
						.generateBlockedResponseByMediaType(resp);
			}
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
		return resp;
	}

	private boolean isMediaTypeBlockable(final HttpResponseImpl response) {
		for (final MediaType m : this.mediaTypes) {
			if (response.getHeader("Content-Type") != null) {
				System.out.println("Lista: " + m + "Response: "
						+ response.getHeader("Content-Type"));
				if (response.getHeader("Content-Type").matches(m.toString())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean urisBlocked(final HttpRequestImpl request) {
		for (final URI uri : this.uris) {
			if (request.getRequestURI().equals(uri)) {
				return true;
			}
		}
		return false;
	}

	private boolean destinationIPIsBlocked(final HttpRequestImpl request) {

		InetAddress requestIP;
		try {
			requestIP = InetAddress.getByName(request.getHost());

			for (final InetAddress ip : this.ips) {

				if ((ip.getHostAddress()).equals(requestIP.getHostAddress())) {
					return true;
				}
			}
		} catch (final UnknownHostException e) {
			return false;
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
		} catch (final ImageException e) {
			throw new CloseException(e.getMessage());
		}

	}

	private byte[] rotateBytes(final byte[] rawImageBytes, final String format)
			throws ImageException {
		int width;
		int height;
		final double radians = Math.PI;// 180 grados
		BufferedImage newImage = null;
		BufferedImage oldImage = null;
		final ByteArrayOutputStream resp;

		try {
			oldImage = ImageIO.read(new ByteArrayInputStream(rawImageBytes));

		} catch (final IOException e) {
			throw new ImageException("Error en la imagen");
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

	@Override
	public abstract boolean equals(Object b);
}
