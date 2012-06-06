package application.filter;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.UserAgent;

public class BrowserBlock extends Block {

	Browser browser;
	
	public BrowserBlock(Browser browser) {
		this.browser = browser;
	}
	
	@Override
	public HttpResponseImpl doFilter(HttpRequestImpl req, HttpResponseImpl resp) {
		UserAgent ua = new UserAgent(req.getHeader("User-Agent"));
		if( !browser.equals(ua.getBrowser()) )
			return null;
		return filter( req, resp );
	}

}
