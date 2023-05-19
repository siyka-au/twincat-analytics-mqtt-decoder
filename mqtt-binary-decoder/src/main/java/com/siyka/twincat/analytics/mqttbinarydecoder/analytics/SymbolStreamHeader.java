package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import java.util.UUID;

import com.siyka.twincat.analytics.mqttbinarydecoder.analytics.SymbolStream.Flags;

public record SymbolStreamHeader(
        int majorVersion, int minorVersion,
        int headerSize,
        long symbolCount, long symbolSize,
        long dataTypeCount, long dataTypeSize,
        long usedDynamicSymbols,
        long codePage,
        SymbolStream.Flags flags,
        UUID hash
    ) {

    public String toString() {
        return String.format("""
                Symbol Stream v%d.%d
                Header Size: %d bytes
                Symbols: %d [%d bytes]
                Data Types: %d [%d bytes]
                Flags: %s
                Hash: %s
                """,
                majorVersion, minorVersion,
                headerSize,
                symbolCount, symbolSize,
                dataTypeCount, dataTypeSize,
                flags,
                hash);
    }
    
}
