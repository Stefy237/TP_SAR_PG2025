package info5.sar.channels;

import info5.sar.channels.CBroker;
import info5.sar.channels.CChannel;
import info5.sar.utils.Type;

public class RDV {
	private int port;
	private boolean pendingAccept = false;
	private boolean pendingConnect = false;
	private CChannel acceptChannel;
	private CChannel connectChannel;
	private boolean valid = true;
	
	public RDV(int port) {
		this.port = port;
	}
		
	public synchronized CChannel come(CBroker broker, Type type) throws InterruptedException {
		switch(type) {
			case CONNECT -> {
				connectChannel = new CChannel(broker, port);
				pendingConnect = true;
				if(pendingAccept && acceptChannel != null) {
					linkChannels();
					notifyAll();
				} else {
					// wait(3000);
					// throw new InterruptedException();
					while (!pendingAccept || acceptChannel == null) {
						wait();
					}
				}
				// valid = false;
				return connectChannel;
			}
			case ACCEPT -> {
				acceptChannel = new CChannel(broker, port);
				pendingAccept = true;
				if(pendingConnect && connectChannel != null) {
					linkChannels();
					notifyAll();
				} else {
					// wait(3000);
					// throw new InterruptedException();
					while (!pendingConnect || connectChannel == null) {
						wait();
					}
				}
				// valid = false;
				return acceptChannel;
			}
			default -> {
				return null;
			}
		}
	}

	public void linkChannels() {
	    // Chaque canal écrit dans le inBuffer de l’autre
	    connectChannel.setOutBuffer(acceptChannel.getInBuffer());
	    acceptChannel.setOutBuffer(connectChannel.getInBuffer());

	    // Lier les références croisées
	    connectChannel.setRemoteChannel(acceptChannel);
	    acceptChannel.setRemoteChannel(connectChannel);
	    
	    valid = false;
	    CBroker broker = (CBroker) acceptChannel.getBroker();
	    broker.getRdvManager().acceptCompleted(port);

	}
	
	public boolean isValid() {
		return valid;
	}

}
