package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import java.nio.ByteBuffer;

public class Float64Reader implements ValueReader<Double> {

    @Override
    public Double read(ByteBuffer buffer, int position) {
        return buffer.getDouble(position);
    }
    
}
