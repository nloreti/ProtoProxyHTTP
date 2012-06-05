package connection;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionConnectionHandlerImpl implements
		CollectionConnectionHandler {

	static CollectionConnectionHandlerImpl instance;
	private final Map<InetSocketAddress, EndPointConnectionHandler> handlers;

	public synchronized static CollectionConnectionHandler getInstance() {
		if (instance == null) {
			instance = new CollectionConnectionHandlerImpl();
		}
		return instance;
	}

	private CollectionConnectionHandlerImpl() {
		this.handlers = new ConcurrentHashMap<InetSocketAddress, EndPointConnectionHandler>();

	}

	public synchronized EndPointConnectionHandler getEndPointConnectionHandler(
			final String host) {
		final InetSocketAddress address = this.getInetSocketAddress(host);
		EndPointConnectionHandler handler = this.handlers.get(address);
		if (handler == null) {
			handler = new EndPointConnectionHandlerImpl(address);
			this.handlers.put(address, handler);
		}
		return handler;
	}

	public InetSocketAddress getInetSocketAddress(final String endPoint) {
		final String[] hostInfo = endPoint.split(":", 2);
		if (hostInfo.length > 2) {
			System.out.println("Mas de dos lenght ENDPOINT");
		}
		final String host = hostInfo[0];
		int port = 80;
		if (hostInfo.length == 2) {
			try {
				port = Integer.valueOf(hostInfo[1]);
			} catch (final NumberFormatException e) {
				System.out.println("Error de formato");
			}
		}
		final InetSocketAddress address = new InetSocketAddress(host, port);
		if (address.isUnresolved()) {
			System.out.println("Unresolved");
		}
		return address;

	}
}
