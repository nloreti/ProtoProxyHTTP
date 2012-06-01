package application;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {

	private static volatile Statistics inst = null;
	private final AtomicInteger proxyClientBytes;
	private final AtomicInteger proxyServerBytes;
	private final AtomicInteger blocks;
	private final AtomicInteger transformations;
	private final AtomicInteger openConnections;
	
	
	
	public static Statistics getInstance(){
		return inst = inst==null?new Statistics():inst;
	}
	
	private Statistics() {
		proxyClientBytes = new AtomicInteger();
		proxyServerBytes = new AtomicInteger();
		blocks = new AtomicInteger();
		transformations = new AtomicInteger();
		openConnections = new AtomicInteger();
	}
	
	public void incrementProxyClientBytes(int b){
		proxyClientBytes.addAndGet(b);
	} 
	
	public void incrementProxyServerBytes(int b){
		proxyServerBytes.addAndGet(b);
	} 
	
	public void incrementBlocks(){
		blocks.incrementAndGet();
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

	public int getBlocks() {
		return blocks.intValue();
	}

	public int getTransformations() {
		return transformations.intValue();
	}

	public int getOpenConnections() {
		return openConnections.intValue();
	}
	
	
}
