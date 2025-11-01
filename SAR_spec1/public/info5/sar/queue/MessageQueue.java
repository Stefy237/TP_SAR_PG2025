package info5.sar.queue;

public abstract class MessageQueue {
	
	QueueBroker qbroker;

	  protected MessageQueue (QueueBroker qbroker) {
	    this.qbroker = qbroker;
	  }

	  // added for helping debugging applications.
	  public abstract String getRemoteName();
	  
	  public QueueBroker getBroker() {
	    return qbroker;
	  }
	  
	abstract void send(byte[] bytes, int offset, int length);
	abstract byte[] receive();
	abstract void close();
	abstract boolean closed();
}
