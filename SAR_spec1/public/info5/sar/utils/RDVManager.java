package info5.sar.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import info5.sar.channels.RDV;

public final class RDVManager {

	private static Map<Integer,RDV> rdvs = new ConcurrentHashMap<>();
	
	public RDV create(int port) {
		return rdvs.computeIfAbsent(port, p -> new RDV(p));
	}
}
