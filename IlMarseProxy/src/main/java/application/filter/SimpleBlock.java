package application.filter;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

public class SimpleBlock extends Block {

	public SimpleBlock() {
		super();
	}

	@Override
	public HttpResponseImpl doFilter(HttpRequestImpl req, HttpResponseImpl resp) {
		return filter(req, resp);
	}
	
	@Override
	public boolean equals(Object b) {
		System.out.println("asd");
		return b.getClass().equals(SimpleBlock.class);
	}
	
	@Override
	public int hashCode(){
		return 0;
	}
}
