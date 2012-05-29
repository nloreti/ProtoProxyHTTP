package connection;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class CollectionConnectionHandlerImpl implements
		CollectionConnectionHandler {

	static CollectionConnectionHandlerImpl instance;
	private Map<InetSocketAddress, EndPointConnectionHandler> handlers;

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

	public EndPointConnectionHandler getEndPointConnectionHandler(
			final String endPoint) {
		// Transformo el String en un Socket
		final InetSocketAddress socketAddress;

		// Split al STring
		final String[] hostData = endPoint.split(":", 2);
		System.out.println("VECTOR:" + hostData[0]);
		// Divido
		final String host = hostData[0];
		final int port = Integer.valueOf("80");

		// Creo el InetSocket
		socketAddress = new InetSocketAddress(host, port);

		EndPointConnectionHandler handler = this.handlers.get(socketAddress);
		// Si no esta en el mapa, lo creo y lo pongo.
		if (handler == null) {
			handler = new EndPointConnectionHandlerImpl(socketAddress);
			this.handlers.put(socketAddress, handler);
		}
		// Retorno un handler valido para ese EndPoint/Host
		return handler;
	}

}
