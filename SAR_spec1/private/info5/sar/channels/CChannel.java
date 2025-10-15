/*
 * Copyright (C) 2023 Pr. Olivier Gruber                                    
 *                                                                       
 * This program is free software: you can redistribute it and/or modify  
 * it under the terms of the GNU General Public License as published by  
 * the Free Software Foundation, either version 3 of the License, or     
 * (at your option) any later version.                                   
 *                                                                       
 * This program is distributed in the hope that it will be useful,       
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         
 * GNU General Public License for more details.                          
 *                                                                       
 * You should have received a copy of the GNU General Public License     
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package info5.sar.channels;

import info5.sar.utils.CircularBuffer;

public class CChannel extends Channel {
	private CircularBuffer inBuffer;
	private CircularBuffer outBuffer;
	private CChannel remoteChannel;
	// private CBroker broker;

	private boolean disconnected = false;
	
  protected CChannel(Broker broker, int port) {
    super(broker);
    this.inBuffer = new CircularBuffer(1000);
  }
  
  protected CChannel(Broker broker, CircularBuffer inBuffer, CircularBuffer outBuffer) {
	  super(broker);
      this.inBuffer = inBuffer;
      this.outBuffer = outBuffer;
  }

  // added for helping debugging applications.
  public String getRemoteName() {
    return remoteChannel.getBroker().getName();
  }

  @Override
  public synchronized int read(byte[] bytes, int offset, int length) {
	System.out.println("------------------------" + broker.getName() + " is reading from " + getRemoteName() + " ----------------------------");
    int i = 0;
    while(i < length-offset && !inBuffer.empty()) {
    	bytes[i] = inBuffer.pull();
    	i++;
    	if(disconnected()) throw new RuntimeException();
    }
    System.out.println("------------------------ end reading ----------------------------");
    return i;
  }

  @Override
  public synchronized int write(byte[] bytes, int offset, int length) {
	  System.out.println("------------------------ " + broker.getName() + " is writing to " + getRemoteName() + "----------------------------");
    int i = 0;
    while(i < length-offset && !outBuffer.full()) {
    	outBuffer.push(bytes[i]);
    	i++;
    	if(disconnected()) throw new RuntimeException();
    }
    System.out.println("------------------------ end reading ----------------------------");
    return i;
  }

  @Override
  public void disconnect() {
    if(!disconnected) {
    	disconnected = true;
    	
    }
  }

  @Override
  public boolean disconnected() {
    return disconnected;
  }
	
	public CircularBuffer getInBuffer() {
		return inBuffer;
	}

	public void setInBuffer(CircularBuffer inBuffer) {
		this.inBuffer = inBuffer;
	}

	public CircularBuffer getOutBuffer() {
		return outBuffer;
	}

	public void setOutBuffer(CircularBuffer outBuffer) {
		this.outBuffer = outBuffer;
	}
	
	public CChannel getRemoteChannel() {
		return remoteChannel;
	}

	public void setRemoteChannel(CChannel remoteChannel) {
		this.remoteChannel = remoteChannel;
	}
}
