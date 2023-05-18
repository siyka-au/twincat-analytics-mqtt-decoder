package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import java.nio.ByteBuffer;

public class Float32Reader implements ValueReader<Float> {

    @Override
    public Float read(ByteBuffer buffer, int position) {
        return buffer.getFloat(position);
    }
    
}
