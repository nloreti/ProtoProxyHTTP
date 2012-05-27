package application;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

	private final static QName _maxThreads_QNAME = new QName("", "maxThreads");
	private final static QName _minThreads_QNAME = new QName("", "minThreads");
	private final static QName _inicialThreads_QNAME = new QName("",
			"inicialThreads");
	private final static QName _hasProxy_QNAME = new QName("", "hasProxy");
	private final static QName _chainProxyIp_QNAME = new QName("",
			"chainProxyIp");
	private final static QName _chainProxyPort_QNAME = new QName("",
			"chainProxyPort");
	private final static QName _webServerPort_QNAME = new QName("",
			"webServerPort");
	private final static QName _proxyPort_QNAME = new QName("", "proxyPort");
	private final static QName _maxServersPerConnection_QNAME = new QName("",
			"maxServersPerConnection");
	private final static QName _timeOutClient_QNAME = new QName("",
			"timeOutClient");
	private final static QName _timeOutServer_QNAME = new QName("",
			"timeOutServer");
	private final static QName _proxyBackLog_QNAME = new QName("",
			"proxyBackLog");
	private final static QName _webServerBackLog_QNAME = new QName("",
			"webServerBackLog");

	public ObjectFactory() {
	}

	public DinamicConfiguration createConfig() {
		return new DinamicConfiguration();
	}

	@XmlElementDecl(namespace = "", name = "maxThreads")
	public JAXBElement<Integer> createMaxThreads(final Integer value) {
		return new JAXBElement<Integer>(_maxThreads_QNAME, Integer.class, null,
				value);
	}

	@XmlElementDecl(namespace = "", name = "minThreads")
	public JAXBElement<Integer> createMinThreads(final Integer value) {
		return new JAXBElement<Integer>(_minThreads_QNAME, Integer.class, null,
				value);
	}

	@XmlElementDecl(namespace = "", name = "inicialThreads")
	public JAXBElement<Integer> createinicialThreads(final Integer value) {
		return new JAXBElement<Integer>(_inicialThreads_QNAME, Integer.class,
				null, value);
	}

	@XmlElementDecl(namespace = "", name = "hasProxy")
	public JAXBElement<Boolean> createHasProxy(final Boolean value) {
		return new JAXBElement<Boolean>(_hasProxy_QNAME, Boolean.class, null,
				value);
	}

	@XmlElementDecl(namespace = "", name = "chainProxyIp")
	public JAXBElement<String> createChainProxyIp(final String value) {
		return new JAXBElement<String>(_chainProxyIp_QNAME, String.class, null,
				value);
	}

	@XmlElementDecl(namespace = "", name = "chainProxyPort")
	public JAXBElement<Integer> createChainProxyPort(final Integer value) {
		return new JAXBElement<Integer>(_chainProxyPort_QNAME, Integer.class,
				null, value);
	}

	@XmlElementDecl(namespace = "", name = "webServerPort")
	public JAXBElement<Integer> createWebServerPort(final Integer value) {
		return new JAXBElement<Integer>(_webServerPort_QNAME, Integer.class,
				null, value);
	}

	@XmlElementDecl(namespace = "", name = "proxyPort")
	public JAXBElement<Integer> createProxyPort(final Integer value) {
		return new JAXBElement<Integer>(_proxyPort_QNAME, Integer.class, null,
				value);
	}

	@XmlElementDecl(namespace = "", name = "maxServersPerConnection")
	public JAXBElement<Integer> createMaxServersPerConnection(
			final Integer value) {
		return new JAXBElement<Integer>(_maxServersPerConnection_QNAME,
				Integer.class, null, value);
	}

	@XmlElementDecl(namespace = "", name = "timeOutClient")
	public JAXBElement<Integer> createTimeOutClient(final Integer value) {
		return new JAXBElement<Integer>(_timeOutClient_QNAME, Integer.class,
				null, value);
	}

	@XmlElementDecl(namespace = "", name = "timeOutServer")
	public JAXBElement<Integer> createTimeOutServer(final Integer value) {
		return new JAXBElement<Integer>(_timeOutServer_QNAME, Integer.class,
				null, value);
	}

	@XmlElementDecl(namespace = "", name = "proxyBackLog")
	public JAXBElement<Integer> createProxyBackLog(final Integer value) {
		return new JAXBElement<Integer>(_proxyBackLog_QNAME, Integer.class,
				null, value);
	}

	@XmlElementDecl(namespace = "", name = "webServerBackLog")
	public JAXBElement<Integer> createWebServerBackLog(final Integer value) {
		return new JAXBElement<Integer>(_webServerBackLog_QNAME, Integer.class,
				null, value);
	}
}