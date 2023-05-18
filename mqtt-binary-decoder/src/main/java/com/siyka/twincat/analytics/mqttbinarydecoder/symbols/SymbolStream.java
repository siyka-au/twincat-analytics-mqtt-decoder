package com.siyka.twincat.analytics.mqttbinarydecoder.symbols;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SymbolStream(Header header, List<SymbolEntry> symbolEntries, List<DataTypeEntry> dataTypeEntries) {
  
    public String toString() {
        return String.format("""
                Version: %d.%d
                Symbol Count: %d
                Data Type Count: %d
                Used Dynamic Symbols: %d
                CodePage: %d
                Flags: %s
                Hash: %s
                """,
                header.majorVersion(), header.minorVersion(),
                header.symbolCount(), header.dataTypeCount(),
                header.usedDynamicSymbols(),
                header.codePage(),
                header.flags(),
                header.hash()
                );
    }

    public static record Header(
            int majorVersion, int minorVersion,
            int headerSize,
            long symbolCount, long symbolSize,
            long dataTypeCount, long dataTypeSize,
            long usedDynamicSymbols,
            long codePage,
            SymbolStream.Flags flags,
            UUID hash) {

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

    public static record Flags(boolean onlineChange, boolean isTarget64Bit, boolean hasBaseTypesIncluded, boolean performQSort) {
        public String toString() {
            var flags = new ArrayList<String>();
            if (onlineChange) flags.add("ONLINE_CHANGE");
            if (isTarget64Bit) flags.add("IS_TARGET_64BIT");
            if (hasBaseTypesIncluded) flags.add("HAS_BASE_TYPES_INCLUDED");
            if (performQSort) flags.add("PERFORM_Q_SORT");
            return String.join(" | ", flags);
        }
    }

}
