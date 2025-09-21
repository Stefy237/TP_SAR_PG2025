public class Client {
    Broker bk = new Broker("client");
    Channel channel;
    Task t = new Task(bk, new Runnable(
        public void run(){
            channel= bk.connect("72.0.2.1",80);
        }
    ));
    t.start();
}
