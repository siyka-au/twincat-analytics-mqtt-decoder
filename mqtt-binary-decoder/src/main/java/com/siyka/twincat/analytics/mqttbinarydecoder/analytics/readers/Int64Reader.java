package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import java.nio.ByteBuffer;

public class Int64Reader implements ValueReader<Long> {

    @Override
    public Long read(ByteBuffer buffer, int position) {
        return buffer.getLong(position);
    }
    
}
