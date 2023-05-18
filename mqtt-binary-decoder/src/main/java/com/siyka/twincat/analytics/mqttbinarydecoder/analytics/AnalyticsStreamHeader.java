package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record AnalyticsStreamHeader(byte majorVersion, byte minorVersion,
        int headerSize, long sampleHeaderSize, long dataSize,
        Duration cycleTime,
        AnalyticsStreamFlag flags,
        UUID guid,
        long sampleCount, Instant streamStartTime, Instant streamStopTime) {

    public String toString() {
        return String.format("""
                Analytics Stream
                Version: %d.%d
                Header Size: %d
                Sample Header Size: %d
                Data Size: %d
                Cycle Time: %s
                Samples: %d
                Stream Start Time: %s
                Stream Stop Time:  %s
                """,
                majorVersion, minorVersion,
                headerSize, sampleHeaderSize, dataSize,
                cycleTime,
                sampleCount, streamStartTime, streamStopTime);
    }

}
