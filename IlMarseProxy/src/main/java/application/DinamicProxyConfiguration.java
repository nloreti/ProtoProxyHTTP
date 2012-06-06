package application;

import java.io.File;
import java.net.InetAddress;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DinamicProxyConfiguration implements ProxyConfiguration {

	static DinamicProxyConfiguration proxyInstance;
	/**
	 * @uml.property name="configuration"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	DinamicConfiguration configuration;
	private static String path = "configuration/configuration.xml";

	public DinamicProxyConfiguration() {
		this.configuration = this.parseXML(path);
	}

	public static DinamicProxyConfiguration getInstance() {

		if (proxyInstance == null) {
			proxyInstance = new DinamicProxyConfiguration();
		}

		return proxyInstance;
	}

	private DinamicConfiguration parseXML(final String path) {

		final DinamicConfiguration dinamicConfiguration = new DinamicConfiguration();

		try {
			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory

			.newInstance();
			final DocumentBuilder docBuilder = docBuilderFactory
					.newDocumentBuilder();
			final Document doc = docBuilder.parse(new File(path));
			dinamicConfiguration.build(doc);
		} catch (final SAXParseException err) {
			System.out.println("** Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

		} catch (final SAXException e) {
			final Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (final Throwable t) {
			t.printStackTrace();
		}

		return dinamicConfiguration;
	}

	@Override
	public int getProxyPort() {
		return this.configuration.getProxyPort();
	}

	@Override
	public void setProxyPort(final int port) {
		this.configuration.setProxyPort(port);

	}

	@Override
	public int getInicialThreads() {
		return this.configuration.getInicialThreads();
	}

	@Override
	public void setInicialThreads(final int inicialThreads) {
		this.configuration.setInicialThreads(inicialThreads);
	}

	@Override
	public boolean hasProxy() {
		return this.configuration.isHasProxy();
	}

	@Override
	public void setHasProxy(final boolean hasProxy) {
		this.configuration.setHasProxy(hasProxy);
	}

	@Override
	public InetAddress getChainProxyIP() {
		InetAddress inet = null;
		try {
			if (this.configuration.getChainProxyIp() != null) {
				inet = InetAddress.getByName(this.configuration
						.getChainProxyIp());
			}
		} catch (final Exception e) {
			System.out.println("Error en el chain Proxy");
			e.printStackTrace();
		}
		return inet;
	}

	@Override
	public void setChainProxyIP(final InetAddress address) {
		if (address != null) {
			this.configuration.setChainProxyIp(address.getHostAddress());
		} else {
			this.configuration.setChainProxyIp(null);
		}
	}

	@Override
	public int getChainProxyPort() {
		return this.configuration.getChainProxyPort();
	}

	@Override
	public void setChainProxyPort(final int chainProxyPort) {
		this.configuration.setChainProxyPort(chainProxyPort);
	}

	@Override
	public int getMaxServersPerConnection() {
		return this.configuration.getMaxServersPerConnection();
	}

	@Override
	public void setMaxServersPerConnection(final int maxServersPerConnection) {
		this.configuration.setMaxServersPerConnection(maxServersPerConnection);
	}

	@Override
	public int getTimeOutToClient() {
		return this.configuration.gettimeOutClient();
	}

	@Override
	public void setTimeOutToClient(final int timeOutClient) {
		this.configuration.settimeOutClient(timeOutClient);
	}

	@Override
	public int getTimeOutToServer() {
		return this.configuration.gettimeOutServer();

	}

	@Override
	public void setTimeOutToServer(final int timeOutServer) {
		this.configuration.settimeOutServer(timeOutServer);
	}

	@Override
	public int getProxyBackLog() {
		return this.configuration.getProxyBackLog();
	}

	@Override
	public void setProxyBackLog(final int backlog) {
		this.configuration.setProxyBackLog(backlog);
	}

	@Override
	public boolean isClientPersistent() {
		// TODO;return configuration.
		return true;
	}

	@Override
	public boolean saveConfiguration() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getFilterPort() {
		return this.configuration.getFilterPort();
	}

	@Override
	public void setFilterPort(final int port) {
		this.configuration.setFilterPort(port);
	}

	@Override
	public int getFilterBackLog() {
		return this.configuration.getFilterLog();
	}

	@Override
	public void setFilterBackLog(final int backlog) {
		this.configuration.setFilterLog(backlog);
	}

	public String getUsername() {
		return this.configuration.getUsername();
	}

	public String getPassword() {
		return this.configuration.getPassword();
	}
}
