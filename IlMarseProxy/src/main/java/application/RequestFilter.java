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
}
