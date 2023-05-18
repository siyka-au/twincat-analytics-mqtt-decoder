package com.siyka.twincat.analytics.mqttbinarydecoder.ads;

public record AdsDataTypeFlags(
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
    boolean isTcComInterfacePointer,
    boolean hasMethodInformation,
    boolean hasAttributes,
    boolean hasEnumInformation,
    boolean isByteAligned,
    boolean isStatic,
    boolean spLevels,
    boolean ignorePersist,
    boolean isAnySizeArray,
    boolean isPersistentDataType,
    boolean isInitialisedOnResult) {

}
