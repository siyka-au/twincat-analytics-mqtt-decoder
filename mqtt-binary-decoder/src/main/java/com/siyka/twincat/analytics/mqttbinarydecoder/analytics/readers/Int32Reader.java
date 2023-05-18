package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import java.nio.ByteBuffer;

public class Int32Reader implements ValueReader<Integer> {

    @Override
    public Integer read(ByteBuffer buffer, int position) {
        return buffer.getInt(position);
    }
    
}
