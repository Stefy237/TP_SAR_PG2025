package info5.sar.queue;

import info5.sar.channels.Broker;

public abstract class QueueBroker {
	String name;
	Broker broker;
	
	QueueBroker(Broker broker) {
		this.broker = broker;
	}
	
	abstract MessageQueue accept(int port);
	abstract MessageQueue connect(String name, int port);
	
	public String getName() { return name; }
}
