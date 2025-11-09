package test;

import info5.sar.channels.CBroker;
import info5.sar.channels.CChannel;
import info5.sar.channels.Task;
import info5.sar.utils.BrokerManager;

/**
 * Test de robustesse de la couche Broker/Channel.
 *
 * Objectif : vérifier le comportement d'une lecture bloquante
 * lorsqu'un channel est fermé par l'autre extrémité.
 *
 * Ce test illustre :
 *   - le blocage sur read() lorsque le buffer est vide ;
 *   - le déblocage propre lorsque disconnect() est appelé ;
 *   - la levée ou la gestion correcte d'une IllegalStateException.
 */

public class WRWhileDisconnedTest {

     public static void main(String[] args) {

        BrokerManager manager = BrokerManager.getInstance();
        CBroker broker = (CBroker) manager.create("broker");

        Task serverTask = new Task("server", broker);
        serverTask.start(() -> {
            CChannel ch = (CChannel) broker.accept(4040);
            assert ch != null : "Le serveur n'a pas pu accepter de connexion.";

            System.out.println("[Server] Connexion acceptée, début de lecture bloquante...");

            byte[] buffer = new byte[10];
            try {
                // Tentative de lecture bloquante — aucune donnée n'est écrite.
                int n = ch.read(buffer, 0, 5);
                System.out.println("[Server] Lecture terminée : " + n + " octets lus");
            } catch (IllegalStateException e) {
                System.out.println("[Server] Lecture interrompue proprement : canal fermé.");
            }

            System.out.println("[Server] Fin du thread serveur.");
        });

        Task clientTask = new Task("client", broker);
        clientTask.start(() -> {
            CChannel ch = (CChannel) broker.connect("broker", 4040);
            assert ch != null : "Le client n'a pas pu se connecter.";

            System.out.println("[Client] Connecté au serveur");
            try {
                Thread.sleep(1000); // on laisse le serveur bloqué sur read()
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("[Client] Fermeture du canal.");
            ch.disconnect();

            System.out.println("[Client] Fin du thread client.");
        });
    }
}
