package application;

import java.net.InetAddress;

/**
 * @author  Nloreti
 */
public interface ProxyConfiguration {

	public int getProxyPort();

	public void setProxyPort(int port);

	public int getInicialThreads();

	public void setInicialThreads(int startPoll);

	public boolean hasProxy();

	public void setHasProxy(boolean hasProxy);

	/**
	 * @uml.property  name="chainProxyIP"
	 */
	public InetAddress getChainProxyIP();

	/**
	 * @param address
	 * @uml.property  name="chainProxyIP"
	 */
	public void setChainProxyIP(InetAddress address);

	public int getChainProxyPort();

	public void setChainProxyPort(int port);

	public int getFilterPort();

	public void setFilterPort(int port);

	public int getMaxServersPerConnection();

	public void setMaxServersPerConnection(int max);

	public int getTimeOutToClient();

	public void setTimeOutToClient(int time);

	public int getTimeOutToServer();

	public void setTimeOutToServer(int time);

	public int getProxyBackLog();

	public void setProxyBackLog(int backlog);

	public int getFilterBackLog();

	public void setFilterBackLog(int backlog);

	public boolean isClientPersistent();

	public boolean saveConfiguration();

}
