package info5.sar.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import info5.sar.channels.Broker;
import info5.sar.channels.CBroker;

public final class BrokerManager {

	// private static volatile BrokerManager instance = null;
	private static volatile BrokerManager instance = new BrokerManager();
	private static Map<String,Broker> brokers = new ConcurrentHashMap<>();
	
	private BrokerManager() {
		super();
	}
	
	public static BrokerManager getInstance() {
		/*
		 * if(instance == null) { synchronized(BrokerManager.class) { if(instance ==
		 * null) { instance = new BrokerManager(); } } }
		 */
		return instance;
	}
	
	public Broker create(String name) {
		return brokers.computeIfAbsent(name, n -> new CBroker(n));
	}
	
	public Broker getBrokerByName(String name) {
		return brokers.get(name);
	}
}
