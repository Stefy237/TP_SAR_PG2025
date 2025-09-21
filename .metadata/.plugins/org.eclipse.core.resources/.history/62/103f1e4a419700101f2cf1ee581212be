public class Serveur {
    Broker bk = new Broker("Serveur");
    Task t = new Task(bk, new Runnable(
        void run() {
            Channel channel = bk.accept(0);
        }
    ));
    t.start();
}