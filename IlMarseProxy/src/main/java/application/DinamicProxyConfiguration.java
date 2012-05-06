package application;

import java.io.File;
import java.net.InetAddress;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class DinamicProxyConfiguration implements ProxyConfiguration {

	static DinamicProxyConfiguration proxyInstance;
	DinamicConfiguration configuration;
	private static String path = "conf/configuration.xml";

	public DinamicProxyConfiguration() {
		this.configuration = this.parseXML(path);
	}

	public static DinamicProxyConfiguration getInstance() {

		if (proxyInstance == null) {
			proxyInstance = new DinamicProxyConfiguration();
		}

		return proxyInstance;
	}

	@SuppressWarnings("restriction")
	private DinamicConfiguration parseXML(final String path) {
		JAXBContext context;
		DinamicConfiguration configuration;

		try {

			context = JAXBContext
					.newInstance("application/DinamicConfiguration");
			final Unmarshaller unmarshaller = context.createUnmarshaller();
			configuration = (DinamicConfiguration) unmarshaller
					.unmarshal(new File(path));
		} catch (final JAXBException e) {
			configuration = null;
		}

		return configuration;
	}

	public int getProxyPort() {
		return this.configuration.getProxyPort();
	}

	public void setProxyPort(final int port) {
		this.configuration.setProxyPort(port);

	}

	public int getMaxThreads() {
		return this.configuration.getMaxThreads();
	}

	public void setMaxThreads(final int maxThreads) {
		this.configuration.setMaxThreads(maxThreads);
	}

	public int getMinThreads() {
		return this.configuration.getMinThreads();
	}

	public void setMinthreads(final int minThreads) {
		this.configuration.setMinThreads(minThreads);
	}

	public int getInicialThreads() {
		return this.configuration.getinicialThreads();
	}

	public void setInicialThreads(final int inicialThreads) {
		this.configuration.setinicialThreads(inicialThreads);
	}

	public boolean hasProxy() {
		return this.configuration.isHasProxy();
	}

	public void setHasProxy(final boolean hasProxy) {
		this.configuration.setHasProxy(hasProxy);
	}

	public InetAddress getChainProxyIP() {
		InetAddress inet = null;
		try {
			if (this.configuration.getChainProxyIp() != null) {
				inet = InetAddress.getByName(this.configuration
						.getChainProxyIp());
			}
		} catch (final Exception e) {
			System.out.println("inet pinchada");
			e.printStackTrace();
		}
		return inet;
	}

	public void setChainProxyIP(final InetAddress address) {
		if (address != null) {
			this.configuration.setChainProxyIp(address.getHostAddress());
		} else {
			this.configuration.setChainProxyIp(null);
		}
	}

	public int getChainProxyPort() {
		return this.configuration.getChainProxyPort();
	}

	public void setChainProxyPort(final int chainProxyPort) {
		this.configuration.setChainProxyPort(chainProxyPort);
	}

	public int getWebServerPort() {
		return this.configuration.getWebServerPort();
	}

	public void setWebServerPort(final int port) {
		this.configuration.setWebServerPort(port);
	}

	public int getMaxServersPerConnection() {
		return this.getMaxServersPerConnection();
	}

	public void setMaxServersPerConnection(final int maxServersPerConnection) {
		this.configuration.setMaxServersPerConnection(maxServersPerConnection);
	}

	public int getTimeOutToClient() {
		return this.configuration.gettimeOutClient();
	}

	public void setTimeOutToClient(final int timeOutClient) {
		// TODO Auto-generated method stub
		this.configuration.settimeOutClient(timeOutClient);
	}

	public int getTimeOutToServer() {
		return this.getTimeOutToServer();

	}

	public void setTimeOutToServer(final int timeOutServer) {
		// TODO Auto-generated method stub
		this.configuration.settimeOutServer(timeOutServer);
	}

	public int getProxyBackLog() {
		return this.configuration.getProxyBackLog();
	}

	public void setProxyBackLog(final int backlog) {
		// TODO Auto-generated method stub
		this.configuration.setProxyBackLog(backlog);
	}

	public int getWebServerBackLog() {
		return this.configuration.getWebServerBackLog();
	}

	public void setWebServerBackLog(final int webServerBackLog) {
		this.configuration.setWebServerBackLog(webServerBackLog);
	}

	public boolean isClientPersistent() {
		// TODO;return configuration.
		return true;
	}

	public boolean saveConfiguration() {
		// TODO Auto-generated method stub
		return false;
	}

}
