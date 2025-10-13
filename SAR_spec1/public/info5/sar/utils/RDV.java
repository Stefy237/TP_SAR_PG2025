package info5.sar.utils;

public class RDV {
	private int port;
	
	public RDV(int port) {
		this.port = port;
	}
	
	boolean pendingAccept = false;
	boolean pendingConnect = false;
	
	public synchronized void come(Type type) throws InterruptedException {
		switch(type) {
		case CONNECT :
			pendingConnect = true;
			if(pendingAccept) {
				notifyAll();
			} else {
				wait(1000);
				throw new InterruptedException();
			}
			break;
		case ACCEPT :
			pendingAccept = true;
			if(pendingConnect) {
				notifyAll();
			} else {
				wait(1000);
				throw new InterruptedException();
			}
			break;
		}
		
	}
}
