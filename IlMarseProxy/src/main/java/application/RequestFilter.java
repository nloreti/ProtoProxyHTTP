package application;

import java.util.ArrayList;
import java.util.List;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import application.filter.Block;
import application.filter.BrowserBlock;
import application.filter.IpBlock;
import application.filter.OSBlock;
import application.filter.SimpleBlock;

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

	public HttpResponseImpl doFilter(final HttpRequestImpl request,
			final HttpResponseImpl response) {
		for (Block b : blocks) {
			HttpResponseImpl resp = b.doFilter(request, response);
			if (resp != null)
				return resp;
		}
		return response;
	}

	public Block getIpBlock( String ip ){
		Block b = new IpBlock(ip);
		if( blocks.contains(b) )
			return blocks.get(blocks.indexOf(b));
		blocks.add(b);
		return b;
	}
	
	public Block getBrowserBlock( String browser ){
		Block b = new BrowserBlock(Browser.valueOf(browser));
		if( blocks.contains(b) )
			return blocks.get(blocks.indexOf(b));
		blocks.add(b);
		return b;
	}
	
	public Block getOsBlock( String os ){
		Block b = new OSBlock(OperatingSystem.valueOf(os));
		if( blocks.contains(b) )
			return blocks.get(blocks.indexOf(b));
		blocks.add(b);
		return b;
	}
	
	public Block getSimpleBlock(){
		Block b = new SimpleBlock();
		if( blocks.contains(b) )
			return blocks.get(blocks.indexOf(b));
		blocks.add(b);
		return b;
	}
}
