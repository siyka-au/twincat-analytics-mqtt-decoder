package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DataStream(Header header, List<DataPoint> data) {

    public String toString() {
        var sb = new StringBuilder(header.toString());
        // for (var dp : data) {
        // sb.append(dp.toString());
        // sb.append("\n");
        // }
        return sb.toString();
    }

    public static record Header(byte majorVersion, byte minorVersion,
            int headerSize, long sampleHeaderSize, long dataSize,
            Duration cycleTime,
            Flags flags,
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

    public static record Flags(boolean hasHeadTimestamp, boolean hasSampleTimestamp, boolean dcTime,
            CompressionMethod compressionMethod) {
    }

    public static enum CompressionMethod {
        NONE, RUN_LENGTH, RESERVED; 
    }

}
