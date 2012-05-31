package application;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

public class RequestFilter {

	private static RequestFilter instance = null;
	private boolean images;
	
	public static RequestFilter getInstance() {
		if( instance == null ){
			instance = new RequestFilter();
		}
		return instance;
	}
	
	public RequestFilter() {
		images = true;
	}
	
	public HttpResponseImpl filter(HttpRequestImpl request){
		return null;
	}

	public void setImages(boolean b) {
		this.images = b;
	}

	public boolean images() {
		return images;
	}
}
