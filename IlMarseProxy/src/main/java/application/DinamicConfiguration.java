package application;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "inicialThreads", "maxThreads", "minThreads",
		"hasProxy", "chainProxyIp", "chainProxyPort", "webServerPort",
		"proxyPort", "maxServersPerConnection", "proxyBackLog",
		"webServerBackLog", "timeOutClient", "timeOutServer" })
@XmlRootElement(name = "configuration")
@SuppressWarnings("restriction")
public class DinamicConfiguration {

	@XmlElement(name = "maxThreads")
	protected Integer maxThreads;

	@XmlElement(name = "minThreads")
	protected Integer minThreads;

	@XmlElement(name = "inicialThreads")
	protected Integer inicialThreads;

	@XmlElement(name = "hasProxy")
	protected boolean hasProxy;

	@XmlElement(name = "chainProxyIp")
	protected String chainProxyIp;

	@XmlElement(name = "chainProxyPort")
	protected Integer chainProxyPort;

	@XmlElement(name = "webServerPort")
	protected Integer webServerPort;

	@XmlElement(name = "proxyPort")
	protected Integer proxyPort;

	@XmlElement(name = "maxServersPerConnection")
	protected Integer maxServersPerConnection;

	@XmlElement(name = "timeOutClient")
	protected Integer timeOutClient;

	@XmlElement(name = "timeOutServer")
	protected Integer timeOutServer;

	@XmlElement(name = "proxyBackLog")
	protected Integer proxyBackLog;

	@XmlElement(name = "webServerBackLog")
	protected Integer webServerBackLog;

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

	public Integer getinicialThreads() {
		return this.inicialThreads;
	}

	public void setinicialThreads(final Integer inicialThreads) {
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

	public Integer getWebServerPort() {
		return this.webServerPort;
	}

	public void setWebServerPort(final Integer webServerPort) {
		this.webServerPort = webServerPort;
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

	public Integer getWebServerBackLog() {
		return this.webServerBackLog;
	}

	public void setWebServerBackLog(final Integer webServerBackLog) {
		this.webServerBackLog = webServerBackLog;
	}

}