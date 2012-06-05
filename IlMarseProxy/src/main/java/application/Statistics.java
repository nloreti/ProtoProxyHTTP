package application;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {

	private static volatile Statistics inst = null;
	private final AtomicInteger proxyClientBytes;
	private final AtomicInteger proxyServerBytes;
	private final AtomicInteger siteBlocks;
	private final AtomicInteger ipBlocks;
	private final AtomicInteger contentBlocks;
	private final AtomicInteger sizeBlocks;
	private final AtomicInteger transformations;
	private final AtomicInteger openConnections;

	public static Statistics getInstance() {
		return inst = inst == null ? new Statistics() : inst;
	}

	private Statistics() {
		this.proxyClientBytes = new AtomicInteger();
		this.proxyServerBytes = new AtomicInteger();
		this.siteBlocks = new AtomicInteger();
		this.ipBlocks = new AtomicInteger();
		this.contentBlocks = new AtomicInteger();
		this.sizeBlocks = new AtomicInteger();
		this.transformations = new AtomicInteger();
		this.openConnections = new AtomicInteger();
	}

	public void incrementProxyClientBytes(final int b) {
		this.proxyClientBytes.addAndGet(b);
	}

	public void incrementProxyServerBytes(final int b) {
		this.proxyServerBytes.addAndGet(b);
	}

	public void incrementSiteBlocks() {
		this.siteBlocks.incrementAndGet();
	}

	public void incrementIpBlocks() {
		this.ipBlocks.incrementAndGet();
	}

	public void incrementSizeBlocks() {
		this.sizeBlocks.incrementAndGet();
	}

	public void incrementContentBlocks() {
		this.contentBlocks.incrementAndGet();
	}

	public void incrementTransformations() {
		this.transformations.incrementAndGet();
	}

	public void connectionOpened() {
		this.openConnections.incrementAndGet();
	}

	public void connectionClosed() {
		this.openConnections.decrementAndGet();
	}

	public int getProxyClientBytes() {
		return this.proxyClientBytes.intValue();
	}

	public int getProxyServerBytes() {
		return this.proxyServerBytes.intValue();
	}

	public int getSiteBlocks() {
		return this.siteBlocks.intValue();
	}

	public int getIpBlocks() {
		return this.ipBlocks.intValue();
	}

	public int getContentBlocks() {
		return this.contentBlocks.intValue();
	}

	public int getSizeBlocks() {
		return this.sizeBlocks.intValue();
	}

	public int getTransformations() {
		return this.transformations.intValue();
	}

	public int getOpenConnections() {
		return this.openConnections.intValue();
	}

}
