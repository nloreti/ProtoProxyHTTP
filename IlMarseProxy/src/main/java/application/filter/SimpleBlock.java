package application.filter;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

public class SimpleBlock extends Block {

	public SimpleBlock() {
	}

	@Override
	public HttpResponseImpl doFilter(HttpRequestImpl req, HttpResponseImpl resp) {
		return filter(req, resp);
	}
}
