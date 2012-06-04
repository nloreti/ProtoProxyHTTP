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
	
	
	
	public static Statistics getInstance(){
		return inst = inst==null?new Statistics():inst;
	}
	
	private Statistics() {
		proxyClientBytes = new AtomicInteger();
		proxyServerBytes = new AtomicInteger();
		siteBlocks = new AtomicInteger();
		ipBlocks = new AtomicInteger();
		contentBlocks = new AtomicInteger();
		sizeBlocks = new AtomicInteger();
		transformations = new AtomicInteger();
		openConnections = new AtomicInteger();
	}
	
	public void incrementProxyClientBytes(int b){
		proxyClientBytes.addAndGet(b);
	} 
	
	public void incrementProxyServerBytes(int b){
		proxyServerBytes.addAndGet(b);
	} 
	
	public void incrementSiteBlocks(){
		siteBlocks.incrementAndGet();
	}
	
	public void incrementIpBlocks(){
		ipBlocks.incrementAndGet();
	}
	
	public void incrementSizeBlocks(){
		sizeBlocks.incrementAndGet();
	}
	
	public void incrementContentBlocks(){
		contentBlocks.incrementAndGet();
	}
	
	public void incrementTransformations(){
		transformations.incrementAndGet();
	}
	
	public void connectionOpened(){
		openConnections.incrementAndGet();
	}
	
	public void connectionClosed(){
		openConnections.decrementAndGet();
	}

	public int getProxyClientBytes() {
		return proxyClientBytes.intValue();
	}

	public int getProxyServerBytes() {
		return proxyServerBytes.intValue();
	}

	public int getSiteBlocks() {
		return siteBlocks.intValue();
	}
	
	public int getIpBlocks() {
		return ipBlocks.intValue();
	}
	
	public int getContentBlocks() {
		return contentBlocks.intValue();
	}
	
	public int getSizeBlocks() {
		return sizeBlocks.intValue();
	}

	public int getTransformations() {
		return transformations.intValue();
	}

	public int getOpenConnections() {
		return openConnections.intValue();
	}
	
	
}
