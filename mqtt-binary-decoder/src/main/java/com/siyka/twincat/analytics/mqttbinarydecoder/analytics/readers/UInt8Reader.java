package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import static com.siyka.twincat.analytics.mqttbinarydecoder.utils.Decoders.getUnsignedByte;

import java.nio.ByteBuffer;

public class UInt8Reader implements ValueReader<Short> {

    @Override
    public Short read(ByteBuffer buffer, int position) {
        return getUnsignedByte(buffer, position);
    }
    
}
