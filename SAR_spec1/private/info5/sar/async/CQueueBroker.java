package info5.sar.async;

import java.util.concurrent.ConcurrentHashMap;

import info5.sar.channels.Broker;
import info5.sar.channels.Channel;
import info5.sar.queue.MessageQueue;

public class CQueueBroker extends QueueBroker {
    private EventPump pump;
    private final ConcurrentHashMap<Integer, AcceptListener> bindings = new ConcurrentHashMap<>();

    public CQueueBroker(String name) {
        super(name);
    }

    public CQueueBroker(String name, EventPump pump) {
        super(name);
        this.pump = pump;
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        new Thread(() -> {
            try {
                Channel channel = broker.accept(port);
                if (channel == null) return;
                
                CMessageQueue queue = new CMessageQueue(channel, pump);
                pump.post(() -> listener.accepted(queue));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return bindings.putIfAbsent(port, listener) == null;
    }

    @Override
    public boolean unbind(int port) {
        return bindings.remove(port) != null;
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        new Thread(() -> {
            try {
                Channel ch = getBroker().connect(name, port);
                if (ch == null) {
                    pump.post(listener::refused);
                    return;
                }
                CMessageQueue q =  new CMessageQueue(ch, pump);
                pump.post(() -> listener.connected(q));
            } catch (Exception e) {
                pump.post(listener::refused);
            }
        }).start();
        return true;
    }
    
}
