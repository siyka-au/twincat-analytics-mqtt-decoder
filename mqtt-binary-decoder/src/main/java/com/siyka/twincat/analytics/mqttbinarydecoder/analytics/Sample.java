package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import java.time.Instant;

public class Sample {

    private final Instant timestamp;
    private final byte[] data;

    public Sample(Instant timestamp, byte[] data) {
        this.timestamp = timestamp;
        this.data = data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public byte[] getData() {
        return data;
    }

}
