package com.siyka.twincat.analytics.mqttbinarydecoder.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class Decoders {

    public static String getString(ByteBuffer buffer, int maxSize) {
        return "";
    }

    public static short getUnsignedByte(ByteBuffer buffer) {
        return (short) (buffer.get() & ((short) 0x00ff));
    }

    public static short getUnsignedByte(ByteBuffer buffer, int position) {
        return (short) (buffer.get(position) & ((short) 0x00ff));
    }

    public static int getUnsignedShort(ByteBuffer buffer) {
        return (int) ((int) buffer.getShort() & ((int) 0x0000ffff));
    }

    public static int getUnsignedShort(ByteBuffer buffer, int position) {
        return (int) (buffer.getShort(position) & ((int) 0x0000ffff));
    }

    public static long getUnsignedInt(ByteBuffer buffer) {
        return (long) (buffer.getInt() & 0x00000000ffffffffL);
    }

    public static long getUnsignedInt(ByteBuffer buffer, int position) {
        return (long) (buffer.getInt(position) & 0x00000000ffffffffL);
    }

    public static UUID getGuid(ByteBuffer buffer) {
        if (buffer.remaining() >= 16) {
            long msb = buffer.get(buffer.position() + 3) & 0xFF;
            msb = msb << 8 | (buffer.get(buffer.position() + 2) & 0xff);
            msb = msb << 8 | (buffer.get(buffer.position() + 1) & 0xff);
            msb = msb << 8 | (buffer.get(buffer.position()) & 0xff);

            msb = msb << 8 | (buffer.get(buffer.position() + 5) & 0xff);
            msb = msb << 8 | (buffer.get(buffer.position() + 4) & 0xff);

            msb = msb << 8 | (buffer.get(buffer.position() + 7) & 0xff);
            msb = msb << 8 | (buffer.get(buffer.position() + 6) & 0xff);

            long lsb = buffer.get(buffer.position() + 8) & 0xff;
            lsb = lsb << 8 | (buffer.get(buffer.position() + 9) & 0xff);
            lsb = lsb << 8 | (buffer.get(buffer.position() + 10) & 0xff);
            lsb = lsb << 8 | (buffer.get(buffer.position() + 11) & 0xff);
            lsb = lsb << 8 | (buffer.get(buffer.position() + 12) & 0xff);
            lsb = lsb << 8 | (buffer.get(buffer.position() + 13) & 0xff);
            lsb = lsb << 8 | (buffer.get(buffer.position() + 14) & 0xff);
            lsb = lsb << 8 | (buffer.get(buffer.position() + 15) & 0xff);

            buffer.position(buffer.position() + 16);
            return new UUID(msb, lsb);
        }

        return null;
    }

    public static void skip(ByteBuffer buffer, int bytes) {
        buffer.get(new byte[bytes]);
    }
    
}
