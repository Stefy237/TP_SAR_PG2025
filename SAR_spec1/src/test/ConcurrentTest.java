package test;

import info5.sar.channels.CBroker;
import info5.sar.channels.CChannel;
import info5.sar.channels.Task;
import info5.sar.utils.BrokerManager;

/**
 * Test concurrent : plusieurs connexions sur des ports distincts
 * entre deux brokers, avec échanges simultanés de messages.
 */
public class ConcurrentTest {

    public static void main(String[] args) {

        BrokerManager manager = BrokerManager.getInstance();
        CBroker broker1 = (CBroker) manager.create("broker1");
        CBroker broker2 = (CBroker) manager.create("broker2");

        // === TASK 1 (Serveur côté broker1) ===
        Task task1 = new Task("task1", broker1);
        task1.start(() -> {
            try {
                // deux connexions sur deux ports différents
                CChannel ch1 = (CChannel) broker1.accept(3000);
                CChannel ch2 = (CChannel) broker1.accept(3001);

                System.out.println("[task1] Connexions acceptées.");

                // écrire sur les deux canaux
                ch1.write("Hello from 3000".getBytes(), 0, "Hello from 3000".length());
                ch2.write("Hello from 3001".getBytes(), 0, "Hello from 3001".length());

                // lire les réponses
                byte[] buf1 = new byte[50];
                byte[] buf2 = new byte[50];
                int n1 = ch1.read(buf1, 0, 10);
                int n2 = ch2.read(buf2, 0, 10);

                System.out.println("[task1] Message reçu sur 3000: " + new String(buf1, 0, n1));
                System.out.println("[task1] Message reçu sur 3001: " + new String(buf2, 0, n2));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // === TASK 2 (Client côté broker2, canal 3000) ===
        Task task2 = new Task("task2", broker2);
        task2.start(() -> {
            try {
                CChannel ch = (CChannel) broker2.connect("broker1", 3000);
                System.out.println("[task2] Connecté à broker1:3000");

                byte[] buf = new byte[50];
                int n = ch.read(buf, 0, 10);
                System.out.println("[task2] Message reçu: " + new String(buf, 0, n));

                ch.write("Ack 3000".getBytes(), 0, "Ack 3000".length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // === TASK 3 (Client côté broker2, canal 3001) ===
        Task task3 = new Task("task3", broker2);
        task3.start(() -> {
            try {
                CChannel ch = (CChannel) broker2.connect("broker1", 3001);
                System.out.println("[task3] Connecté à broker1:3001");

                byte[] buf = new byte[50];
                int n = ch.read(buf, 0, buf.length);
                System.out.println("[task3] Message reçu: " + new String(buf, 0, n));

                ch.write("Ack 3001".getBytes(), 0, "Ack 3001".length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
