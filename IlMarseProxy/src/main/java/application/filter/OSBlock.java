package application.filter;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;

public class OSBlock extends Block {

	private OperatingSystem os;
	
	public OSBlock(OperatingSystem os) {
		this.os = os;
	}
	
	@Override
	public HttpResponseImpl doFilter(HttpRequestImpl req, HttpResponseImpl resp) {
		UserAgent ua = new UserAgent(req.getHeader("User-Agent"));
		if( !os.equals(ua.getOperatingSystem()) )
			return null;
		return filter( req, resp );
	}

}
