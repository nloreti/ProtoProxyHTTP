package application;

import java.util.HashSet;
import java.util.Set;

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
		images = true;
		leet = false;
		access = true;
		maxSize = 0;
		ips = new HashSet<String>();
		uris = new HashSet<String>();
		mediaTypes = new HashSet<String>();
	}

	public HttpResponseImpl filter(HttpRequestImpl request) {
		return null;
	}

	public boolean images() {
		return images;
	}

	public boolean setMaxSize(int ms) {
		this.maxSize = ms;
		return hasMaxSize();
	}

	public boolean hasMaxSize() {
		return maxSize != 0;
	}

	public boolean blockMediaType(String mediaType) {
		return mediaTypes.add(mediaType);
	}

	public boolean unlockMediaType(String mediaType) {
		return mediaTypes.remove(mediaType);
	}

	public boolean unlockUri(String uri) {
		return uris.remove(uri);
	}

	public boolean blockUri(String uri) {
		return uris.add(uri);
	}

	public boolean access() {
		return access;
	}

	public void accessOff() {
		access = false;
	}

	public void accessOn() {
		access = true;
	}

	public void leetOn() {
		leet = true;
	}

	public void leetOff() {
		leet = false;
	}

	public boolean leet() {
		return leet;
	}

	public void imagesOff() {
		images = false;
	}

	public void imagesOn() {
		images = true;
	}

	public boolean blockIP(String ip) {
		return ips.add(ip);
	}

	public boolean unlockIP(String ip) {
		return ips.remove(ip);
	}

	public HttpResponseImpl doFilter(HttpRequestImpl request,
			HttpResponseImpl response) {
		if (!access)
			return generateBlockedResponse();
		if (ips.contains(request.getDestinationIp()))
			return generateBlockedResponseByIp(request.getDestinationIp());
		if (images) {
			if (response.containsType("image")) {// TODO hacer bien esto xq no tengo idea!
				//TODO dar vuelta la imagen
				//BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(response.getInputStream()));
			}
		}
		if (leet) {
			if (response.containsType("plain text")) {// TODO hacer bien esto xq no tengo idea!
				String body = new String(response.getBody());
				body = body.replace('a', '4').replace('e', '3')
						.replace('i', '1').replace('o', '0');
				response.setBody(body.getBytes());
			}
		}
		if (uris.contains(request.getRequestURI())) {
			return generateBlockedResponseByUri(request.getRequestURI()
					.toString());
		}
		if (response.getContentLength() > maxSize) {
			return generateBlockedResponse(maxSize);
		}
		if (mediaTypes.contains(request.getMediaType())) {
			return generateBlockedResponseByMediaType(request.getMediaType());
		}
		return response;
	}

	private HttpResponseImpl generateBlockedResponseByIp(String destinationIp) {
		return generateBlockedResponse("This ip has been blocked by the proxy administrator. IP: "
				+ destinationIp);
	}

	private HttpResponseImpl generateBlockedResponseByMediaType(String mediaType) {
		return generateBlockedResponse("This media type has been blocked by the proxy administrator. Media type: "
				+ mediaType);
	}

	private HttpResponseImpl generateBlockedResponse() {
		return generateBlockedResponse("The access has been completely blocked by the proxy administrator.");
	}

	private HttpResponseImpl generateBlockedResponseByUri(String requestURI) {
		return generateBlockedResponse("This URI has been blocked by the proxy administrator. URI: "
				+ requestURI);
	}

	private HttpResponseImpl generateBlockedResponse(int size) {
		return generateBlockedResponse("The resource you are trying to reach is too big. Size: "
				+ size);
	}

	private HttpResponseImpl generateBlockedResponse(String string) {
		try {
			HttpResponseImpl response = new HttpResponseImpl(null);
			response.addHeader("MIME TYPE", "HTML");// TODO hacer bien esto xq
													// no tengo idea
			String body = "<body>" + string + "</body>";
			response.setBody(body.getBytes());
			return response;
		} catch (Exception e) {
			return null;
		}
	}
}
