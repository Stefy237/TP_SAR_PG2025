package info5.sar.queue;

import info5.sar.channels.Broker;
import info5.sar.channels.CChannel;
import info5.sar.utils.ReaderAutomata;
import info5.sar.utils.WriterAutomata;

public class CQueueBroker extends QueueBroker {

    CQueueBroker(Broker broker) {
        super(broker);
    }

    @Override
    MessageQueue accept(int port) {
        CChannel channel = (CChannel) broker.accept(port);
        return new CMessageQueue(this, channel);
    }

    @Override
    MessageQueue connect(String name, int port) {
        CChannel channel = (CChannel) broker.connect(name, port);
        if (channel == null) return null;
        return new CMessageQueue(this, channel);
    }
    
}
