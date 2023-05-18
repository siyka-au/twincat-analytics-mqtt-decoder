package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import static com.siyka.twincat.analytics.mqttbinarydecoder.utils.Decoders.getUnsignedShort;

import java.nio.ByteBuffer;

public class UInt16Reader implements ValueReader<Integer> {

    @Override
    public Integer read(ByteBuffer buffer, int position) {
        return getUnsignedShort(buffer, position);
    }
    
}
