package test;

import info5.sar.channels.CBroker;
import info5.sar.channels.CChannel;
import info5.sar.channels.Task;
import info5.sar.utils.BrokerManager;

public class ConcurentTest {
    public static void main(String[] args) {
		
		BrokerManager brokerManager = BrokerManager.getInstance();
		CBroker broker1 = (CBroker) brokerManager.create("broker1");
		CBroker broker2 = (CBroker) brokerManager.create("broker2");
		// CBroker broker3 = (CBroker) brokerManager.create("broker3");
		
		Task task1 = new Task("task1", broker1);
		task1.start(() -> {
			CChannel channel1 = (CChannel) task1.getBroker().accept(3000);
			CChannel channel2 = (CChannel) task1.getBroker().accept(3000);
			assert channel1 != null : "Accept should return a channel";
			assert channel2 != null : "Accept should return a channel";
			byte[] msg1Sent = "Hello \n".getBytes();
			byte[] msg2Sent = "Welcome from task1 \n".getBytes();
			channel1.write(msg1Sent, 0, msg1Sent.length);
			channel2.write(msg1Sent, 0, msg1Sent.length);
			channel1.write(msg2Sent, msg1Sent.length, msg2Sent.length);
			channel2.write(msg2Sent, msg1Sent.length, msg1Sent.length);
			byte[] buffer1 = new byte[100];
			byte[] buffer2 = new byte[100];
			int n = channel1.read(buffer1, 0, 5);
			int m = channel2.read(buffer2, 0, 5);
			String msg1Received = new String(buffer1, 0, n);
			String msg2Received = new String(buffer2, 0, m);
			// assert msgReceived.equals("Hi") : "Wrong message Received";
            System.out.println("Message received by task1 " + msg1Received);
            System.out.println("Message received by task1 " + msg2Received);
		});
		
        Task task2 = new Task("task2", broker2);
		task2.start(() -> {
			CChannel channel1 = (CChannel) task2.getBroker().connect("broker1",3000);
			CChannel channel2 = (CChannel) task2.getBroker().connect("broker2",3000);
			// assert channel != null : "Connect should return a channel";
			byte[] msgSent = "Hi from task2 \n".getBytes();
			channel1.write(msgSent, 0, msgSent.length);
			channel2.write(msgSent, 0, msgSent.length);
			byte[] buffer1 = new byte[100];
			byte[] buffer2 = new byte[100];
			int n = channel1.read(buffer1, 0, 5);
			int m = channel2.read(buffer2, 0, 5);
			String msg1Received = new String(buffer1, 0, n);
			String msg2Received = new String(buffer2, 0, m);
			// assert msg2Received.equals("Hello") : "Wrong message Received";
            System.out.println("Message received by task2 " + msg1Received);
            System.out.println("Message received by task2 " + msg2Received);
		});

        Task task3 = new Task("task3", broker2);
		task3.start(() -> {
			CChannel channel1 = (CChannel) task3.getBroker().connect("broker1",3000);
			CChannel channel2 = (CChannel) task3.getBroker().accept(3000);
			// assert channel != null : "Connect should return a channel";
			byte[] msg1Sent = "Hi from task3 \n".getBytes();
			byte[] msg2Sent = "Hello from task3 \n".getBytes();
			channel1.write(msg1Sent, 0, msg1Sent.length);
			channel2.write(msg2Sent, 0, msg2Sent.length);
			byte[] buffer1 = new byte[100];
			byte[] buffer2 = new byte[100];
			int n = channel1.read(buffer1, 0, 5);
			int m = channel2.read(buffer2, 0, 5);
			String msg1Received = new String(buffer1, 0, n);
			String msg2Received = new String(buffer2, 0, m);
			//assert msgReceived.equals("Hello") : "Wrong message Received";
            System.out.println("Message received by task3 " + msg1Received);
            System.out.println("Message received by task3 " + msg2Received);
		});
		
	}
}
