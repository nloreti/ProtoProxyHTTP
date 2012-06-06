package application;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import application.filter.Block;
import application.filter.BrowserBlock;
import application.filter.IpBlock;
import application.filter.OSBlock;
import application.filter.SimpleBlock;
import exceptions.CloseException;
import exceptions.ImageException;

public class RequestFilter {

	static RequestFilter instance = null;
	private List<Block> blocks;

	public static RequestFilter getInstance() {
		if (instance == null) {
			instance = new RequestFilter();
		}
		return instance;
	}

	public RequestFilter() {
		this.blocks = new ArrayList<Block>();
	}

	public HttpResponseImpl doFilter(final HttpRequestImpl request,
			final HttpResponseImpl response, final InetAddress ip) {
		boolean isRotated = false;
		boolean isLeet = false;
		for (final Block b : this.blocks) {
			final HttpResponseImpl resp = b.doFilter(request, response, ip);
			if (resp != null) {
				return resp;
			}
			isRotated |= b.images();
			isLeet |= b.leet();
		}
		this.doTransformations(request, response, isRotated, isLeet);
		return response;
	}

	private void doTransformations(final HttpRequestImpl request,
			final HttpResponseImpl response, final boolean isRotated,
			final boolean isLeet) {

		if (isRotated) {
			if (response.containsType("image/.*")) {
				Statistics.getInstance().incrementTransformations();
				this.rotateImage(response);
			}
		}
		if (isLeet) {
			if (response.containsType("text/plain.*")) {
				String body = new String(response.getBody());
				body = body.replace('a', '4').replace('e', '3')
						.replace('i', '1').replace('o', '0');
				response.setBody(body.getBytes());
			}
		}

	}

	public Block getIpBlock(final String ip) throws UnknownHostException {
		final Block b = new IpBlock(ip);
		if (this.blocks.contains(b)) {
			return this.blocks.get(this.blocks.indexOf(b));
		}
		this.blocks.add(b);
		return b;
	}

	public Block getBrowserBlock(final String browser) {
		final Block b = new BrowserBlock(Browser.valueOf(browser));
		if (this.blocks.contains(b)) {
			return this.blocks.get(this.blocks.indexOf(b));
		}
		this.blocks.add(b);
		return b;
	}

	public Block getOsBlock(final String os) {
		final Block b = new OSBlock(OperatingSystem.valueOf(os));
		if (this.blocks.contains(b)) {
			return this.blocks.get(this.blocks.indexOf(b));
		}
		this.blocks.add(b);
		return b;
	}

	public Block getSimpleBlock() {
		final Block b = new SimpleBlock();
		if (this.blocks.contains(b)) {
			return this.blocks.get(this.blocks.indexOf(b));
		}
		this.blocks.add(b);
		return b;
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
			System.out.println("Error en la imagen");
			e.printStackTrace();
		}

		try {
			resp.flush();
		} catch (final IOException e) {
			System.out.println("Error haciendo flush de la imagen");
			e.printStackTrace();
		}

		return resp.toByteArray();
	}
}
