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

import java.util.Map;

public class CBroker extends Broker {
	static Map<String, Broker> brokerList = new ConcurrentHashMap<>();
	
	private Map<Integer, Channel> pendingConnectChannel = new ConcurrentHashMap<>();
	
  public CBroker(String name) {
	  try {
		  if (brokerList.containsKey(name)) throw new RuntimeException("Name already use");
		  brokerList.put(name, this);
		  super(name);
	  } catch (RuntimeException re) {
		  System.err.println(re.getMessage());
	      re.printStackTrace(System.err);
	  }
  }

  @Override
  public synchronized Channel accept(int port) {
    while(!pendingConnectChannel.containsKey(port)) wait();
    notify();
    return pendingConnectChannel.remove(port);
  }

  @Override
  public synchronized Channel connect(String name, int port) {
	  try {
		  CBroker remoteBroker = CBroker.brokerList.get(name);
		  CChannel ch = new CChannel(this);
		  remoteBroker.pendingConnectChannel.put(port, ch);
		  
		  while(remoteBroker.pendingConnectChannel.containsKey(port)) wait();
		  notify();
		  return ch;
	  } catch (Exception) {
		  
	  }
  }

}
