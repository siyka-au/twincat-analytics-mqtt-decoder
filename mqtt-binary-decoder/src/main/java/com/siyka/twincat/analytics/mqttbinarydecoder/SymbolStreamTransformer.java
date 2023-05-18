package com.siyka.twincat.analytics.mqttbinarydecoder;

import java.nio.ByteBuffer;

import org.springframework.integration.core.GenericTransformer;

import com.siyka.twincat.analytics.mqttbinarydecoder.symbols.SymbolStream;
import static com.siyka.twincat.analytics.mqttbinarydecoder.symbols.SymbolStreamDecoder.getSymbolStream;

public class SymbolStreamTransformer implements GenericTransformer<byte[], SymbolStream> {

    @Override
    public SymbolStream transform(byte[] source) {
        var buffer = ByteBuffer.wrap(source).asReadOnlyBuffer();
        return getSymbolStream(buffer);
    }

}
