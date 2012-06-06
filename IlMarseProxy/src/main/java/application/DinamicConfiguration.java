package application;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DinamicConfiguration {

	private Integer maxThreads;
	private Integer minThreads;
	private Integer inicialThreads;
	private boolean hasProxy;
	private String chainProxyIp;
	private Integer chainProxyPort;
	private Integer filterPort;
	private Integer proxyPort;
	private Integer maxServersPerConnection;
	private Integer timeOutClient;
	private Integer timeOutServer;
	private Integer proxyBackLog;
	private Integer filterLog;
	private String username;
	private String password;

	public DinamicConfiguration() {
	}

	public void build(final Document doc) {

		doc.getDocumentElement().normalize();

		final NodeList listOfmetricScoperesult = doc
				.getElementsByTagName("configuration");

		for (int s = 0; s < listOfmetricScoperesult.getLength(); s++) {

			final Node firstMetricNode = listOfmetricScoperesult.item(s);

			final NodeList listOfconfigurationResults = firstMetricNode
					.getChildNodes();

			for (int n = 0; n < listOfconfigurationResults.getLength(); n++) {

				if (!(n % 2 == 0)) {// No me preguntes porque es modulo dos,
									// escapa de mi conocimiento jaja.
					final Node configurationNode = listOfconfigurationResults
							.item(n);
					final Element elemento = (Element) configurationNode;

					if (elemento.getNodeName().equals("inicialThreads")) {
						this.inicialThreads = Integer.valueOf(elemento
								.getAttribute("value"));
					}
					if (elemento.getNodeName().equals("chainProxyIp")) {
						this.chainProxyIp = elemento.getAttribute("value");
					}
					if (elemento.getNodeName().equals("chainProxyPort")) {
						this.chainProxyPort = Integer.valueOf(elemento
								.getAttribute("value"));
					}
					if (elemento.getNodeName().equals("filterPort")) {
						this.filterPort = Integer.valueOf(elemento
								.getAttribute("value"));
					}
					if (elemento.getNodeName().equals("filterBackLog")) {
						this.filterLog = Integer.valueOf(elemento
								.getAttribute("value"));
					}
					if (elemento.getNodeName().equals("proxyPort")) {
						this.proxyPort = Integer.valueOf(elemento
								.getAttribute("value"));
					}
					if (elemento.getNodeName().equals("proxyBackLog")) {
						this.proxyBackLog = Integer.valueOf(elemento
								.getAttribute("value"));
					}
					if (elemento.getNodeName()
							.equals("maxServersPerConnection")) {
						this.maxServersPerConnection = Integer.valueOf(elemento
								.getAttribute("value"));
					}
					if (elemento.getNodeName().equals("timeOutClient")) {
						this.timeOutClient = Integer.valueOf(elemento
								.getAttribute("value"));
					}
					if (elemento.getNodeName().equals("timeOutServer")) {
						this.timeOutServer = Integer.valueOf(elemento
								.getAttribute("value"));
					}
					if (elemento.getNodeName().equals("username")) {
						this.username = elemento.getAttribute("value");
					}
					if (elemento.getNodeName().equals("password")) {
						this.password = elemento.getAttribute("value");
					}
					if (elemento.getNodeName().equals("hasProxy")) {
						final String value = elemento.getAttribute("value");
						if (value.equals("false")) {
							this.hasProxy = false;
						} else {
							this.hasProxy = true;
						}
					}
				}
			}
		}

	}

	public Integer getMaxThreads() {
		return this.maxThreads;
	}

	public void setMaxThreads(final Integer maxThreads) {
		this.maxThreads = maxThreads;
	}

	public Integer getMinThreads() {
		return this.minThreads;
	}

	public void setMinThreads(final Integer minThreads) {
		this.minThreads = minThreads;
	}

	public Integer getInicialThreads() {
		return this.inicialThreads;
	}

	public void setInicialThreads(final Integer inicialThreads) {
		this.inicialThreads = inicialThreads;
	}

	public boolean isHasProxy() {
		return this.hasProxy;
	}

	public void setHasProxy(final boolean hasProxy) {
		this.hasProxy = hasProxy;
	}

	public String getChainProxyIp() {
		return this.chainProxyIp;
	}

	public void setChainProxyIp(final String chainProxyIp) {
		this.chainProxyIp = chainProxyIp;
	}

	public Integer getChainProxyPort() {
		if (this.chainProxyPort == null) {
			return -1;
		}
		return this.chainProxyPort;
	}

	public void setChainProxyPort(final Integer chainProxyPort) {
		this.chainProxyPort = chainProxyPort;
	}

	public Integer getProxyPort() {
		return this.proxyPort;
	}

	public void setProxyPort(final Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public Integer getMaxServersPerConnection() {
		return this.maxServersPerConnection;
	}

	public void setMaxServersPerConnection(final Integer maxServersPerConnection) {
		this.maxServersPerConnection = maxServersPerConnection;
	}

	public Integer gettimeOutClient() {
		return this.timeOutClient;
	}

	public void settimeOutClient(final Integer timeOutClient) {
		this.timeOutClient = timeOutClient;
	}

	public Integer gettimeOutServer() {
		return this.timeOutServer;
	}

	public void settimeOutServer(final Integer timeOutServer) {
		this.timeOutServer = timeOutServer;
	}

	public Integer getProxyBackLog() {
		return this.proxyBackLog;
	}

	public void setProxyBackLog(final Integer proxyBackLog) {
		this.proxyBackLog = proxyBackLog;
	}

	public Integer getFilterPort() {
		return this.filterPort;
	}

	public void setFilterPort(final Integer filterPort) {
		this.filterPort = filterPort;
	}

	public Integer getTimeOutClient() {
		return this.timeOutClient;
	}

	public void setTimeOutClient(final Integer timeOutClient) {
		this.timeOutClient = timeOutClient;
	}

	public Integer getTimeOutServer() {
		return this.timeOutServer;
	}

	public void setTimeOutServer(final Integer timeOutServer) {
		this.timeOutServer = timeOutServer;
	}

	public Integer getFilterLog() {
		return this.filterLog;
	}

	public void setFilterLog(final Integer filterLog) {
		this.filterLog = filterLog;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

}