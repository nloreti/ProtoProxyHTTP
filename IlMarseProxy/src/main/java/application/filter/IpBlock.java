package application.filter;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

public class IpBlock extends Block {

	private String ip;

	public IpBlock(final String ip) {
		super();
		this.ip = ip;
	}

	@Override
	public HttpResponseImpl doFilter(final HttpRequestImpl req,
			final HttpResponseImpl resp) {
		if (!this.equalIps(req.getDestinationIp(), this.ip)) {
			return null;
		}
		return this.filter(req, resp);
	}

	private boolean equalIps(String ip1, String ip2) {
		if (ip1.contains("*")) {
			ip1 = ip1.replace('*', '\0');
			return ip2.startsWith(ip1);
		}
		if (ip2.contains("*")) {
			ip2 = ip2.replace('*', '\0');
			return ip1.startsWith(ip2);
		}
		return ip1.equals(ip2);
	}

	@Override
	public boolean equals(final Object b) {
		if (b.getClass().equals(IpBlock.class)) {
			return ((IpBlock) b).ip == this.ip;
		}
		return false;
	}

}
