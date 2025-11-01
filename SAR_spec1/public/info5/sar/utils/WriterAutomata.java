package info5.sar.utils;

import java.nio.ByteBuffer;

import info5.sar.channels.CChannel;

public class WriterAutomata {
    private CChannel channel;

    public WriterAutomata(CChannel channel) {
        this.channel =channel;
    }

    public void write(byte[] bytes, int offset, int length) {
        if (channel.disconnected()) throw new IllegalStateException();

        byte[] header = ByteBuffer.allocate(4).putInt(length).array();
        channel.write(header, 0, 4);
        channel.write(bytes, offset, length);
    }
}
