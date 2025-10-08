package test;

import info5.sar.channels.CBroker;
import info5.sar.channels.CChannel;
import info5.sar.channels.Task;
import info5.sar.utils.BrokerManager;
import info5.sar.utils.CircularBuffer;

public class test1 {
	public static void main(String[] args) {
		BrokerManager brokerManager = BrokerManager.getInstance();
		CBroker user1 = (CBroker) brokerManager.create("user1");
		CBroker user2 = (CBroker) brokerManager.create("user2");
		CBroker user3 = (CBroker) brokerManager.create("user3");
		
		CircularBuffer buffer1 = new CircularBuffer(1000);
		CircularBuffer buffer2 = new CircularBuffer(1000);
		CircularBuffer buffer3 = new CircularBuffer(1000);
		
		Task task1 = new Task("task1", user1);
		Task task2 = new Task("task1", user2);
		Task task3 = new Task("task1", user3);
		
		task1.start(new Runnable() {

			@Override
			public void run() {
				CChannel ch11 = (CChannel) task1.getBroker().accept(5000);
				byte[] chOut11 = new byte[] {1,1,1};
				byte[] chIn11 = new byte[3];
				ch11.setBuffer(buffer1);
				
				CChannel ch12 = (CChannel) task1.getBroker().connect("user2",5000);
				byte[] chOut12 = new byte[] {1,1,1};
				byte[] chIn12 = new byte[3];
				ch12.setBuffer(buffer3);
				
				ch11.write(chOut11, 0, 3);
				ch12.read(chIn12, 0, 3);
			}
			
		});
		
		task2.start(new Runnable() {

			@Override
			public void run() {
				CChannel ch22 = (CChannel) task1.getBroker().connect("use3",5000);
				byte[] chOut22 = new byte[] {1,1,1};
				byte[] chIn22 = new byte[3];
				ch22.setBuffer(buffer2);
				
				CChannel ch21 = (CChannel) task2.getBroker().accept(5000);
				byte[] chOut21 = new byte[] {1,1,1};
				byte[] chIn1 = new byte[3];
				ch21.setBuffer(buffer3);
				
				ch21.write(chOut21, 0, 3);
				ch22.read(chIn22, 0, 3);
			}
			
		});
		
		task3.start(new Runnable() {

			@Override
			public void run() {
				CChannel ch31 = (CChannel) task3.getBroker().connect("user1",5000);
				byte[] chOut31 = new byte[] {1,1,1};
				byte[] chIn1 = new byte[3];
				ch31.setBuffer(buffer1);
				
				CChannel ch32 = (CChannel) task2.getBroker().accept(5000);
				/*CChannel ch2 = (CChannel) task1.getBroker().connect("user2",5000);
				byte[] chOut2 = new byte[] {1,1,1};
				byte[] chIn2 = new byte[3];
				ch2.setBuffer(buffer2);*/
				
				ch31.write(chOut31, 0, 3);
				//ch2.read(chIn2, 0, 3);
			}
			
		});
	}
}
