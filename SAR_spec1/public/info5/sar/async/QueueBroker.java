package info5.sar.async;

import info5.sar.channels.Broker;
import info5.sar.utils.BrokerManager;

public abstract class QueueBroker {
    private String name;
    protected Broker broker;
    
    QueueBroker(String name){
        this.name = name;
        this.broker = BrokerManager.getInstance().create(name);
    }

    public interface AcceptListener {
        public void accepted(MessageQueue queue);
    }

    public abstract boolean bind(int port, AcceptListener listener);

    public abstract boolean unbind(int port);

    public interface ConnectListener {
        public void connected(MessageQueue queue);
        public void refused();
    }

    public abstract boolean connect(String name, int port, ConnectListener listener);

    public Broker getBroker() { return broker; }

}
