package com.siyka.twincat.analytics.mqttbinarydecoder.symbols;

import com.siyka.twincat.analytics.mqttbinarydecoder.ads.AdsDataTypeFlags;

class AdsDataTypeFlagsDecoder {

    static AdsDataTypeFlags decode(int data) {
        var isDataType = (data & 0x00000001) > 0;
        var isDataItem = (data & 0x00000002) > 0;
        var isReferenceTo = (data & 0x00000004) > 0;
        var isMethodDeref = (data & 0x00000008) > 0;
        var isOversamplingArray = (data & 0x00000010) > 0;
        var isBitValue = (data & 0x00000020) > 0;
        var isPropertyItem = (data & 0x00000040) > 0;
        var hasTypeGuid = (data & 0x00000080) > 0;
        var isPersistent = (data & 0x00000100) > 0;
        var hasCopyMask = (data & 0x00000200) > 0;
        var isTcComInterfacePointer = (data & 0x00000400) > 0;
        var hasMethodInformation = (data & 0x00000800) > 0;
        var hasAttributes = (data & 0x00001000) > 0;
        var hasEnumInformation = (data & 0x00002000) > 0;
        var isByteAligned = (data & 0x00010000) > 0;
        var isStatic = (data & 0x00020000) > 0;
        var spLevels = (data & 0x00040000) > 0;
        var ignorePersist = (data & 0x00080000) > 0;
        var isAnySizeArray = (data & 0x00100000) > 0;
        var isPersistentDataType = (data & 0x00200000) > 0;
        var isInitialisedOnResult = (data & 0x00400000) > 0;

        return new AdsDataTypeFlags(isDataType, isDataItem, isReferenceTo, isMethodDeref, isOversamplingArray,
                isBitValue, isPropertyItem, hasTypeGuid, isPersistent, hasCopyMask, isTcComInterfacePointer,
                hasMethodInformation, hasAttributes, hasEnumInformation, isByteAligned, isStatic, spLevels,
                ignorePersist, isAnySizeArray, isPersistentDataType, isInitialisedOnResult);
    }

}
