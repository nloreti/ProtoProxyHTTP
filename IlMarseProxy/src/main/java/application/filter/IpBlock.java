package application.filter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

public class IpBlock extends Block {

	private InetAddress ip;

	public IpBlock(final String ip) throws UnknownHostException {
		super();
		this.ip = InetAddress.getByName(ip);
	}

	@Override
	public HttpResponseImpl doFilter(final HttpRequestImpl req,
			final HttpResponseImpl resp, final InetAddress ip) {
		if (!ip.equals(this.ip)) {
			return null;
		}
		return this.filter(req, resp);
	}

	@Override
	public boolean equals(final Object b) {
		if (b.getClass().equals(IpBlock.class)) {
			return ((IpBlock) b).ip.equals(this.ip);
		}
		return false;
	}

}
