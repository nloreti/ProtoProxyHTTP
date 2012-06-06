package application;

import java.util.ArrayList;
import java.util.List;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import application.filter.Block;

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
		blocks = new ArrayList<Block>();
	}

	public HttpResponseImpl filter(final HttpRequestImpl request) {
		return null;
	}

	public HttpResponseImpl doFilter(final HttpRequestImpl request,
			final HttpResponseImpl response) {
		for (Block b : blocks) {
			HttpResponseImpl resp = b.doFilter(request, response);
			if (resp != null)
				return resp;
		}
		return response;
	}

}
