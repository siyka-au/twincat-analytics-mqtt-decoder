package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import java.time.Instant;
import java.util.Objects;

public record DataPoint(String name, Instant timestamp, Object value) {

    public DataPoint {
        Objects.requireNonNull(name);
        Objects.requireNonNull(timestamp);
        Objects.requireNonNull(value);
    }

    public String toString() {
        return String.format("%s - %s = %s", timestamp, name, value);
    }

}
