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

		// JAXBContext context;
		// DinamicConfiguration configuration;
		//
		// try {
		//
		// context = JAXBContext.newInstance("application");
		// final Unmarshaller unmarshaller = context.createUnmarshaller();
		// configuration = (DinamicConfiguration) unmarshaller
		// .unmarshal(new File(path));
		//
		// } catch (final JAXBException e) {
		// e.printStackTrace();
		// configuration = null;
		// }
		// return configuration;
	}

	public int getProxyPort() {
		return this.configuration.getProxyPort();
	}

	public void setProxyPort(final int port) {
		this.configuration.setProxyPort(port);

	}

	public int getInicialThreads() {
		return this.configuration.getInicialThreads();
	}

	public void setInicialThreads(final int inicialThreads) {
		this.configuration.setInicialThreads(inicialThreads);
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

	public int getMaxServersPerConnection() {
		return this.configuration.getMaxServersPerConnection();
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
		return this.configuration.gettimeOutServer();

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

	public boolean isClientPersistent() {
		// TODO;return configuration.
		return true;
	}

	public boolean saveConfiguration() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getFilterPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setFilterPort(final int port) {
		// TODO Auto-generated method stub

	}

	public int getFilterBackLog() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setFilterBackLog(final int backlog) {
		// TODO Auto-generated method stub

	}

}
