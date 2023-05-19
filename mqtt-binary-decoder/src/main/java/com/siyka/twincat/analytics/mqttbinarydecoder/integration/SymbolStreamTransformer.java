package com.siyka.twincat.analytics.mqttbinarydecoder.integration;

import static com.siyka.twincat.analytics.mqttbinarydecoder.analytics.SymbolStreamDecoder.getSymbolStream;

import java.nio.ByteBuffer;

import org.springframework.integration.core.GenericTransformer;

import com.siyka.twincat.analytics.mqttbinarydecoder.analytics.SymbolStream;

public class SymbolStreamTransformer implements GenericTransformer<byte[], SymbolStream> {

    @Override
    public SymbolStream transform(byte[] source) {
        var buffer = ByteBuffer.wrap(source).asReadOnlyBuffer();
        return getSymbolStream(buffer);
    }

}
