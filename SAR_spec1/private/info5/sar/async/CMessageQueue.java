package info5.sar.async;

import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import info5.sar.channels.Channel;

public class CMessageQueue extends MessageQueue {
    private final Channel ch;
    private final EventPump pump;
    private Listener listener;
    private final Thread readerThread;
    // private final Thread writerThread;
    // private final BlockingQueue<byte[]> outbox = new LinkedBlockingQueue<>();

    boolean started = false;

    public CMessageQueue(Channel ch, EventPump pump) {
        this.ch = ch;
        this.pump = pump;
        this.readerThread = new Thread(this::listenLoop);
        // this.writerThread = new Thread(this::writeLoop);
    }

    @Override
    public void setListener(Listener l) {
        this.listener = l;
        if (!started) {
            readerThread.start();
            // writerThread.start();
            // started = true;
        }
    }

    @Override
    public boolean send(byte[] bytes) {
        return send(bytes, 0, bytes.length);
    }

    @Override
    public boolean send(byte[] bytes, int offset, int length) {
        if (ch.disconnected()) return false;
        byte[] msg = java.util.Arrays.copyOfRange(bytes, offset, offset + length);
        // return outbox.offer(copy);
        try {
            byte[] header = ByteBuffer.allocate(4).putInt(msg.length).array();
            ch.write(header, 0, 4);
            ch.write(msg, 0, msg.length);
        
        } catch (Exception e) {
            if (listener != null)
                pump.post(listener::closed);
        }
        return true;
    }

    @Override
    public void close() {
        ch.disconnect();
    }

    @Override
    public boolean closed() {
        try {
            readerThread.join(1000);
            // writerThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ch.disconnected();
    }

    // === Internals loops ===
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

    // private void writeLoop() {
    //     try {
    //         while (!ch.disconnected()) {
    //             byte[] msg = outbox.take(); 
    //             byte[] header = ByteBuffer.allocate(4).putInt(msg.length).array();
    //             ch.write(header, 0, 4);
    //             ch.write(msg, 0, msg.length);
    //         }
    //     } catch (Exception e) {
    //         if (listener != null)
    //             pump.post(listener::closed);
    //     }
    // }
    
}
