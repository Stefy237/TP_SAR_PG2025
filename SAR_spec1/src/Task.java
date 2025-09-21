public abstract class Task extends Thread {
	Broker brocker;
	Runnable runnable;
	
    Task(Broker b, Runnable r){
    	this.brocker = b;
    	this.runnable = r;
    };
    abstract public static Broker getBroker() {};
    
    public void run() {
    	runnable.run();
    };
}
