package info5.sar.utils;

import java.nio.ByteBuffer;

import info5.sar.channels.CChannel;

public class ReaderAutomata {
    private CChannel channel;

    public ReaderAutomata(CChannel channel) {
        this.channel = channel;
    }

    public byte[] read() {
        if (channel.disconnected()) throw new IllegalStateException("Channel disconnected");

        byte[] header = new byte[4];
        channel.read(header, 0, 4);
        int length = ByteBuffer.wrap(header).getInt();
        byte[] payload = new byte[length];
        channel.read(payload, 0, length);
        return payload;
    }

}
