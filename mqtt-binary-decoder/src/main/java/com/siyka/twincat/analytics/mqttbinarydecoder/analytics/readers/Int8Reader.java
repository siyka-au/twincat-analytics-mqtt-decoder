package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import java.nio.ByteBuffer;

public class Int8Reader implements ValueReader<Byte> {

    @Override
    public Byte read(ByteBuffer buffer, int position) {
        return buffer.get(position);
    }
    
}
