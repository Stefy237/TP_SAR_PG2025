public abstract class Task extends Thread {
    Task(Broker b, Runnable r);
    abstract public static Broker getBroker();
    abstract public void run();
}
