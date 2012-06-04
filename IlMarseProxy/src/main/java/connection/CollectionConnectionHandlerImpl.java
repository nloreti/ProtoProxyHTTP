package connection;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

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
		this.handlers = new HashMap<InetSocketAddress, EndPointConnectionHandler>();
	}

	// TODO: Hay que validar que lo que se pase no sea fruta y creer
	// execeptions!

	// public EndPointConnectionHandler getEndPointConnectionHandler(
	// final String endPoint) {
	// // Transformo el String en un Socket
	// final InetSocketAddress socketAddress;
	//
	// // Split al STring
	// final String[] hostData = endPoint.split(":", 2);
	// System.out.println("VECTOR:" + hostData[0]);
	// // Divido
	// final String host = hostData[0];
	// final int port = Integer.valueOf("80");
	//
	// // Creo el InetSocket
	// socketAddress = new InetSocketAddress(host, port);
	//
	// EndPointConnectionHandler handler = this.handlers.get(socketAddress);
	// // Si no esta en el mapa, lo creo y lo pongo.
	// if (handler == null) {
	// handler = new EndPointConnectionHandlerImpl(socketAddress);
	// this.handlers.put(socketAddress, handler);
	// }
	// // Retorno un handler valido para ese EndPoint/Host
	// return handler;
	//
	// }

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
