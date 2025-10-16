package info5.sar.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import info5.sar.channels.RDV;

public final class RDVManager {

	private static Map<Integer, List<RDV>> rdvs = new ConcurrentHashMap<>();
	private List<Integer> pendingAcceptPorts = new ArrayList<>();
	
	public synchronized RDV create(int port, Type type) {
		if(type == Type.ACCEPT) {
			if(pendingAcceptPorts.contains(port)) {
				throw new IllegalArgumentException();
			} else {
				pendingAcceptPorts.add(port);
			}
		}
		List<RDV> list = rdvs.computeIfAbsent(port, p -> new ArrayList<RDV>());
		for (RDV rdv : list) {
			if(rdv.isValid()) {
				return rdv;
			} 
		}
		RDV rdv = new RDV(port);
		list.add(rdv);
		return rdv;
	}
	
	public void acceptCompleted(int port) {
		pendingAcceptPorts.remove(Integer.valueOf(port));
	}
}
