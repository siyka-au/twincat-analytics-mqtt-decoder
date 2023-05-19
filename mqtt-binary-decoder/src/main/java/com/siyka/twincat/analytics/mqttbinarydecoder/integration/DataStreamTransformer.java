package com.siyka.twincat.analytics.mqttbinarydecoder.integration;

import java.nio.ByteBuffer;

import org.springframework.integration.core.GenericTransformer;

import com.siyka.twincat.analytics.mqttbinarydecoder.analytics.DataStream;

import static com.siyka.twincat.analytics.mqttbinarydecoder.analytics.DataStreamDecoder.getDataStream;

public class DataStreamTransformer implements GenericTransformer<byte[], DataStream> {

    @Override
    public DataStream transform(byte[] source) {
        var buffer = ByteBuffer.wrap(source).asReadOnlyBuffer();
        return getDataStream(buffer);
    }
    
}
