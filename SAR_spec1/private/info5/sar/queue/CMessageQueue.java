package info5.sar.queue;

import info5.sar.channels.CChannel;
import info5.sar.utils.ReaderAutomata;
import info5.sar.utils.WriterAutomata;

public class CMessageQueue extends MessageQueue {
    private CChannel channel;
    private WriterAutomata writer;
    private ReaderAutomata reader;

    public CMessageQueue(QueueBroker qbroker, CChannel channel) {
        super(qbroker);
        this.writer = new WriterAutomata(channel);
        this.reader = new ReaderAutomata(channel);
    }

    @Override
    public String getRemoteName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRemoteName'");
    }

    @Override
    public void send(byte[] bytes, int offset, int length) {
        writer.write(bytes, offset, length);
    }

    @Override
    public byte[] receive() {
        return reader.read();
    }

    @Override
    public void close() {
        channel.disconnect();
    }

    @Override
    public boolean closed() {
        return channel.disconnected();
    }
    
}
