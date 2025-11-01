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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import info5.sar.utils.CircularBuffer;

public class CChannel extends Channel {
	private CircularBuffer inBuffer;
	private CircularBuffer outBuffer;
	private CChannel remoteChannel;
	
	private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

	private boolean disconnected = false;
	
  protected CChannel(Broker broker, int port) {
    super(broker);
    this.inBuffer = new CircularBuffer(100);
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
	if (disconnected) throw new IllegalStateException("Channel disconnected");
	if (offset < 0 || length < 0 || offset + length > bytes.length) throw new IllegalArgumentException();

    int bytesRead = 0;
	lock.lock();
    try {
		while (bytesRead < length) {
			while (inBuffer.empty()) {
				if (disconnected) return bytesRead;
				notEmpty.await();
			}
			bytes[offset+bytesRead] = inBuffer.pull();
			bytesRead++;
			notFull.signal();
		}
	} catch (InterruptedException e) {
		e.printStackTrace();
	} finally {
		lock.unlock();
	}
    return bytesRead;
  }

  @Override
  public synchronized int write(byte[] bytes, int offset, int length) {
	if (disconnected) throw new IllegalStateException("Channel disconnected");
	if (offset < 0 || length < 0 || offset + length > bytes.length) throw new IllegalArgumentException();

    int bytesWritten = 0;
	lock.lock();
	try {
		while(bytesWritten < length) {
			while (outBuffer.full()) {
				if (disconnected) return bytesWritten;
				notFull.await();
			}
			outBuffer.push(bytes[bytesWritten + offset]);
			bytesWritten++;
			notEmpty.signal();
    	}
	} catch (InterruptedException e) {
		e.printStackTrace();
	} finally {
		lock.unlock();
	}
    
    return bytesWritten;
  }

  @Override
  public void disconnect() {
    lock.lock();
    try {
        disconnected = true;
        notEmpty.signalAll();
        notFull.signalAll();
    } finally {
        lock.unlock();
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
