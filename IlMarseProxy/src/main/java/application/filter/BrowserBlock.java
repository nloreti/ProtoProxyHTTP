package application.filter;

import java.net.InetAddress;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.UserAgent;

public class BrowserBlock extends Block {

	Browser browser;
	
	public BrowserBlock(Browser browser) {
		super();
		this.browser = browser;
	}
	
	@Override
	public HttpResponseImpl doFilter(HttpRequestImpl req, HttpResponseImpl resp, InetAddress ip) {
		UserAgent ua = new UserAgent(req.getHeader("User-Agent"));
		if( !browser.equals(ua.getBrowser()) )
			return null;
		return filter( req, resp );
	}


	@Override
	public boolean equals(Object b) {
		if( b.getClass().equals(BrowserBlock.class) ){
			return ((BrowserBlock)b).browser.equals(browser);
		}
		return false;
	}
}
