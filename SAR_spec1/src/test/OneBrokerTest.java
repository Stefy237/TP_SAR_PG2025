package test;

import info5.sar.channels.CBroker;
import info5.sar.channels.CChannel;
import info5.sar.channels.Task;
import info5.sar.utils.BrokerManager;

public class OneBrokerTest {
	public static void main(String[] args) {
		
		BrokerManager brokerManager = BrokerManager.getInstance();
		CBroker broker = (CBroker) brokerManager.create("broker");
		
		Task task1 = new Task("task1", broker);
		task1.start(() -> {
			CChannel channel = (CChannel) task1.getBroker().accept(3000);
			assert channel != null : "Accept should return a channel";
			byte[] msgSent = "Hello".getBytes();
			channel.write(msgSent, 0, msgSent.length);
			byte[] buffer = new byte[10];
			int n = channel.read(buffer, 0, buffer.length);
			String msgReceived = new String(buffer, 0, n);
			assert msgReceived.equals("Hi") : "Wrong message Received";
            System.out.println("Message received by task1 " + msgReceived);
		});
		
        Task task2 = new Task("task2", broker);
		task2.start(() -> {
			CChannel channel = (CChannel) task2.getBroker().connect("broker",3000);
			assert channel != null : "Connect should return a channel";
			byte[] msgSent = "Hi".getBytes();
			channel.write(msgSent, 0, msgSent.length);
			byte[] buffer = new byte[10];
			int n = channel.read(buffer, 0, buffer.length);
			String msgReceived = new String(buffer, 0, n);
			assert msgReceived.equals("Hello") : "Wrong message Received";
            System.out.println("Message received by task2 " + msgReceived);

		});
		
	}
}
