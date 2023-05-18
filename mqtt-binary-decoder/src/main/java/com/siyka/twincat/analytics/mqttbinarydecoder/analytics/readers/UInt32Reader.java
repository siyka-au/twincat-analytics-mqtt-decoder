package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import static com.siyka.twincat.analytics.mqttbinarydecoder.utils.Decoders.getUnsignedInt;
import java.nio.ByteBuffer;

public class UInt32Reader implements ValueReader<Long> {

    @Override
    public Long read(ByteBuffer buffer, int position) {
        return getUnsignedInt(buffer, position);
    }
    
}
