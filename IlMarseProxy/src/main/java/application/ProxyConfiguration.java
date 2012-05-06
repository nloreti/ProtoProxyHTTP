package application;

import java.net.InetAddress;

public interface ProxyConfiguration {

	public int getProxyPort();

	public void setProxyPort(int port);

	public int getMaxThreads();

	public void setMaxThreads(int max);

	public int getMinThreads();

	public void setMinthreads(int min);

	public int getInicialThreads();

	public void setInicialThreads(int startPoll);

	public boolean hasProxy();

	public void setHasProxy(boolean hasProxy);

	public InetAddress getChainProxyIP();

	public void setChainProxyIP(InetAddress address);

	public int getChainProxyPort();

	public void setChainProxyPort(int port);

	public int getWebServerPort();

	public void setWebServerPort(int port);

	public int getMaxServersPerConnection();

	public void setMaxServersPerConnection(int max);

	public int getTimeOutToClient();

	public void setTimeOutToClient(int time);

	public int getTimeOutToServer();

	public void setTimeOutToServer(int time);

	public int getProxyBackLog();

	public void setProxyBackLog(int backlog);

	public int getWebServerBackLog();

	public void setWebServerBackLog(int backlog);

	public boolean isClientPersistent();

	public boolean saveConfiguration();

}
