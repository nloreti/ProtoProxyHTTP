package application;

import java.net.InetAddress;

public class Start {

	public static void main(final String[] args) {
		try {
			final FilterSocketServer filterServer = new FilterSocketServer(
					InetAddress.getByName("localhost"), new FilterHandler());

			new Thread(filterServer).start();
			final ProxyHTTP proxy = new ProxyHTTP();
			proxy.run();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
