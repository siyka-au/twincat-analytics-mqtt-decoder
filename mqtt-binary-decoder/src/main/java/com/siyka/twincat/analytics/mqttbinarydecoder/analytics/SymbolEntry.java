package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import java.util.Optional;
import java.util.UUID;

import javax.swing.text.html.Option;

import com.siyka.twincat.analytics.mqttbinarydecoder.ads.AdsDataType;

public record SymbolEntry(String name, String typeName, String comment,
        long indexGroup, long indexOffset, long size,
        AdsDataType dataType, Optional<UUID> typeGuid, Flags flags) {

    public boolean isBitType() {
        return (indexOffset & 0x40000000) > 0;
    }

    public String toString() {
        return String.format("""
                Name: %s
                Type Name: %s
                Comment: %s
                Index Group/Offset: 0x%08x / 0x%08x
                Type: %s%s
                Flags: %s
                """,
                name,
                typeName,
                comment,
                indexGroup, indexOffset,
                dataType,
                typeGuid
                    .map(guid -> String.format(" {%s}", guid))
                    .orElseGet(() -> ""),
                flags);
    }

    public static record Flags(boolean isPersistent, boolean isBitValue, boolean isReferenceTo, boolean hasTypeGuid,
            boolean isTwincatComInterfacePointer, boolean isReadOnly, boolean isInterfaceMethodAccess,
            boolean isMethodDeref, byte contextMask, boolean hasAttributes, boolean isStatic,
            boolean isInitialisedOnReset, boolean hasExtendedFlags) {
    }

}
