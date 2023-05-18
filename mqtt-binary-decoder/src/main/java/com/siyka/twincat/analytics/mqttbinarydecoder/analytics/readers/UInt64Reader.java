package com.siyka.twincat.analytics.mqttbinarydecoder.analytics.readers;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class UInt64Reader implements ValueReader<BigInteger> {

    @Override
    public BigInteger read(ByteBuffer buffer, int position) {
        return BigInteger.valueOf(69);
    }
    
}
