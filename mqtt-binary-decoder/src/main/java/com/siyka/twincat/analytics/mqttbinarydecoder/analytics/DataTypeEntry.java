package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.siyka.twincat.analytics.mqttbinarydecoder.ads.AdsDataType;

public record DataTypeEntry(
                long version,
                int hashValue, int typeHashValue,
                long size, long offset,
                AdsDataType baseDataType,
                DataTypeEntry.Flags flags,
                String name, String typeName, String comment,
                Optional<UUID> guid,
                Optional<byte[]> copyMask,
                int arrayDimensions,
                Optional<ArrayInformation> arrayInformation,
                List<MethodInformation> methodInformation,
                Map<String, byte[]> enumInformation,
                Map<String, String> attributes,
                List<DataTypeEntry> subItems) {

    public static record Flags(
            boolean isDataType,
            boolean isDataItem,
            boolean isReferenceTo,
            boolean isMethodDeref,
            boolean isOversamplingArray,
            boolean isBitValue,
            boolean isPropertyItem,
            boolean hasTypeGuid,
            boolean isPersistent,
            boolean hasCopyMask,
            boolean isTwincatComInterfacePointer,
            boolean hasMethodInfos,
            boolean hasAttributes,
            boolean hasEnumInfos,
            boolean isByteAligned,
            boolean isStatic,
            boolean spLevels,
            boolean ignorePersist,
            boolean isAnySizeArray,
            boolean isPersistantDatatype,
            boolean isInitialisedOnResult) {
    }

    public static record ArrayInformation(long lowerBounds, long length) {}

    public static record MethodInformation() {}

    public static record EnumInformation() {}

}
