package test;

import info5.sar.async.CMessageQueue;
import info5.sar.async.CQueueBroker;
import info5.sar.async.EventPump;
import info5.sar.async.MessageQueue;

public class AsyncTest {
    public static void main(String[] args) {
        EventPump pump = new EventPump() {};

        pump.start();

        CQueueBroker qbA = new CQueueBroker("aqbA", pump);
        CQueueBroker qbB = new CQueueBroker("aqbB", pump);

        qbB.bind(1234, queue -> {
            queue.setListener(new CMessageQueue.Listener() {
                public void received(byte[] msg) {
                    System.out.println("[B] reçu : " + new String(msg));
                }
                public void closed() {
                    System.out.println("[B] connexion fermée");
                }
            });
        });

        qbA.connect("aqbB", 1234, new CQueueBroker.ConnectListener() {
            @Override
            public void connected(MessageQueue queue) {
                System.out.println("[A] connecté !");
                queue.send("Hello async world!".getBytes());
            }

            public void refused() {
                System.out.println("[A] connexion refusée");
            }
        });
    }
}
