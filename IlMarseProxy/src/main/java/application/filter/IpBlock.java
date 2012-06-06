package application.filter;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

public class IpBlock extends Block {

	private String ip;
	
	public IpBlock(String ip) {
		this.ip = ip;
	}
	
	@Override
	public HttpResponseImpl doFilter(HttpRequestImpl req, HttpResponseImpl resp) {
		if( !equalIps( req.getDestinationIp(), ip ) )
			return null;
		return filter( req, resp );
	}

	private boolean equalIps(String ip1, String ip2) {
		if( ip1.contains("*") ){
			ip1 = ip1.replace('*', '\0');
			return ip2.startsWith(ip1);
		}
		if( ip2.contains("*") ){
			ip2 = ip2.replace('*', '\0');
			return ip1.startsWith(ip2);
		}
		return ip1.equals(ip2);
	}

	@Override
	public boolean equals(Block b) {
		if( b.getClass().equals(IpBlock.class) ){
			return ((IpBlock)b).ip == ip;
		}
		return false;
	}

}
