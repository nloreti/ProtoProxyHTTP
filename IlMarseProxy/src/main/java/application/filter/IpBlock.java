package application.filter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

public class IpBlock extends Block {

	private InetAddress ip;

	public IpBlock(final String ip) throws UnknownHostException {
		super();
		this.ip = InetAddress.getByAddress(ip.getBytes());
	}

	@Override
	public HttpResponseImpl doFilter(final HttpRequestImpl req,
			final HttpResponseImpl resp, InetAddress ip) {
		if (!ip.equals(this.ip)) {
			return null;
		}
		return this.filter(req, resp);
	}


	@Override
	public boolean equals(final Object b) {
		if (b.getClass().equals(IpBlock.class)) {
			return ((IpBlock) b).ip == this.ip;
		}
		return false;
	}

}
