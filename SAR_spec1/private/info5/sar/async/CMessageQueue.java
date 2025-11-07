package info5.sar.async;

import info5.sar.channels.Channel;

public class CMessageQueue extends MessageQueue {
    private final Channel ch;
    private final EventPump pump;
    private Listener listener;
    private final Thread readerThread;

    public CMessageQueue(Channel ch, EventPump pump) {
        this.ch = ch;
        this.pump = pump;
        this.readerThread = new Thread(this::listenLoop);
        this.readerThread.start();
    }

    @Override
    public void setListener(Listener l) {
        this.listener = l;
    }

    @Override
    public boolean send(byte[] bytes) {
        return send(bytes, 0, bytes.length);
    }

    @Override
    public boolean send(byte[] bytes, int offset, int length) {
        try {
            byte[] header = java.nio.ByteBuffer.allocate(4).putInt(length).array();
            ch.write(header, 0, 4);
            ch.write(bytes, offset, length);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void close() {
        ch.disconnect();
    }

    @Override
    public boolean closed() {
        return ch.disconnected();
    }

    // === Internal loop ===
    private void listenLoop() {
        try {
            while (!ch.disconnected()) {
                byte[] header = new byte[4];
                ch.read(header, 0, 4);
                int length = java.nio.ByteBuffer.wrap(header).getInt();

                byte[] payload = new byte[length];
                ch.read(payload, 0, length);

                if (listener != null) {
                    pump.post(() -> listener.received(payload));
                }
            }
        } catch (Exception e) {
            if (listener != null)
                pump.post(listener::closed);
        }
    }
    
}
