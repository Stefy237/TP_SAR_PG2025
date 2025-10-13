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
import java.util.concurrent.ConcurrentHashMap;

import info5.sar.utils.BrokerManager;
import info5.sar.utils.RDV;
import info5.sar.utils.RDVManager;
import info5.sar.utils.Type;

public class CBroker extends Broker {
	private boolean alive = true;
	private RDVManager rdvManager = new RDVManager();
	

	public CBroker(String name) {
		  super(name);
	  }

  @Override
  public synchronized Channel accept(int port){
	  System.out.println("------------------------" + name + " accepting ----------------------------");
	  try {
		RDV rdv = rdvManager.create(port);
		rdv.come(Type.ACCEPT);
		return new CChannel(this, port);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	  return null;
  }

	@Override
	public synchronized Channel connect(String name, int port) {
		System.out.println("------------------------" + name + " connecting ----------------------------");
		CBroker targetBroker = (CBroker) BrokerManager.getInstance().getBrokerByName(name);
		if(targetBroker != null) {
			try {
				targetBroker.getRdvManager().create(port).come(Type.CONNECT);
				return new CChannel(this, port);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	  public RDVManager getRdvManager() {
			return rdvManager;
	  }

}
