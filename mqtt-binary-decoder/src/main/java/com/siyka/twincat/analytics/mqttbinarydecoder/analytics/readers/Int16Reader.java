package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import java.nio.ByteBuffer;

public class Int16Reader implements ValueReader<Short> {

    @Override
    public Short read(ByteBuffer buffer, int position) {
        return buffer.getShort(position);
    }
    
}
