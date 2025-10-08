package info5.sar.utils;

import java.util.concurrent.Semaphore;

public class RDV {
	private int port;
	
	public RDV(int port) {
		this.port = port;
	}
	
	private final Semaphore connectSem = new Semaphore(0);
	private final Semaphore acceptSem = new Semaphore(0);
	private final Semaphore mutex = new Semaphore(1);
	
	private int nConnect = 0;
	private boolean pendingAccept = false;
	private boolean acceptDone = false;
	
	public void come(Type type) throws InterruptedException {
		mutex.acquire();
		switch(type) {
		case CONNECT :
			if(pendingAccept && !acceptDone) {
				acceptSem.release();
				acceptDone = true;
				mutex.release();
			} else if(pendingAccept && acceptDone){
				mutex.release();
			} else {
				nConnect++;
				mutex.release();
				connectSem.acquire();
			}
			break;
		case ACCEPT :
			pendingAccept = true;
			
			if(nConnect > 0) {
				connectSem.release(nConnect);
				nConnect = 0;
				mutex.release();
			} else {
				mutex.release();
				acceptSem.acquire();
			}
			break;
		}
		
	}
}
