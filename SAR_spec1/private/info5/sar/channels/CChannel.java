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
	private CircularBuffer buffer;
	
	public CircularBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(CircularBuffer buffer) {
		this.buffer = buffer;
	}

	private boolean isDisconnected = false;
	
  protected CChannel(Broker broker, int port) {
    super(broker);
    // throw new RuntimeException("NYI");
  }

  // added for helping debugging applications.
  public String getRemoteName() {
    return super.broker.getName();
  }

  @Override
  public synchronized int read(byte[] bytes, int offset, int length) {
	System.out.println("------------------------" + getRemoteName() + " reading ----------------------------");
    int i = 0;
    while(i < length-offset && !buffer.empty()) {
    	bytes[i] = buffer.pull();
    	i++;
    	if(disconnected()) throw new RuntimeException();
    	//if(buffer.empty()) wait();
    }
    System.out.println("------------------------ end reading ----------------------------");
    return i;
  }

  @Override
  public synchronized int write(byte[] bytes, int offset, int length) {
	  System.out.println("------------------------ " + getRemoteName() + " writing ----------------------------");
    int i = 0;
    while(i < length-offset && !buffer.full()) {
    	buffer.push(bytes[i]);
    	i++;
    	if(disconnected()) throw new RuntimeException();
    }
    System.out.println("------------------------ end reading ----------------------------");
    return i;
  }

  @Override
  public void disconnect() {
    if(!isDisconnected) {
    	isDisconnected = true;
    	
    }
  }

  @Override
  public boolean disconnected() {
    return isDisconnected;
  }

}
