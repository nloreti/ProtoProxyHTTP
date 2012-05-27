package application;

public class Start {

	public static void main(final String[] args) {
		try {
			final ProxyHTTP proxy = new ProxyHTTP();
			proxy.run();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
