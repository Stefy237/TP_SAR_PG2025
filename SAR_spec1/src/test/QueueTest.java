package test;

import info5.sar.channels.Broker;
import info5.sar.channels.Task;
import info5.sar.queue.CMessageQueue;
import info5.sar.queue.CQueueBroker;
import info5.sar.utils.BrokerManager;

public class QueueTest {
    public static void main(String[] args) throws Exception {
        Broker b1 = BrokerManager.getInstance().create("qbA");
        Broker b2 = BrokerManager.getInstance().create("qbB");

        CQueueBroker qb1 = new CQueueBroker(b1);
        CQueueBroker qb2 = new CQueueBroker(b2);

        Task t1 = new Task("T1", qb1);
        t1.start(() -> {
            CMessageQueue q = (CMessageQueue) qb1.accept(3000);
            byte[] msg = q.receive();
            System.out.println("Reçu: " + new String(msg));
        });

        Task t2 = new Task("T2", qb2);
        t2.start(() -> {
            CMessageQueue q = (CMessageQueue) qb2.connect("qbA", 3000);
            q.send("Hello Broker!".getBytes(), 0, "Hello Broker!".length());
        });
    }
}
