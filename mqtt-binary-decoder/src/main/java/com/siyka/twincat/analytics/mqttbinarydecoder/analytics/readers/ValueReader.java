package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface ValueReader<T> {
    
    T read(ByteBuffer buffer, int position);

}
