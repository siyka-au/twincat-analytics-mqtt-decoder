package com.siyka.twincat.analytics.mqttbinarydecoder.symbols;

import static com.siyka.twincat.analytics.mqttbinarydecoder.utils.Decoders.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.siyka.twincat.analytics.mqttbinarydecoder.ads.AdsDataType;

public class SymbolStreamDecoder {

    public static SymbolStream getSymbolStream(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        var header = getSymbolStreamHeader(buffer);
        var symbolEntries = getSymbolEntries(buffer, (int) header.symbolCount());
        var dataTypeEntries = getDataTypeEntries(buffer, (int) header.dataTypeCount());

        return new SymbolStream(header, symbolEntries, dataTypeEntries);
    }

    private static SymbolStream.Header getSymbolStreamHeader(ByteBuffer buffer) {
        var majorVersion = buffer.get();
        var minorVersion = buffer.get();

        var headerSize = getUnsignedShort(buffer);
        var symbolCount = getUnsignedInt(buffer);
        var symbolSize = getUnsignedInt(buffer);
        var dataTypeCount = getUnsignedInt(buffer);
        var dataTypeSize = getUnsignedInt(buffer);
        var usedDynamicSymbols = getUnsignedInt(buffer);
        var codePage = getUnsignedInt(buffer);
        var flags = SymbolStreamDecoder.decodeSymbolStreamFlags(buffer.getInt());

        skip(buffer, 16);

        var hash = getGuid(buffer);

        return new SymbolStream.Header(
                majorVersion, minorVersion,
                headerSize,
                symbolCount, symbolSize,
                dataTypeCount, dataTypeSize,
                usedDynamicSymbols,
                codePage,
                flags,
                hash);
    }

    private static List<SymbolEntry> getSymbolEntries(ByteBuffer buffer, int count) {
        var symbolEntries = new ArrayList<SymbolEntry>(count);
        for (var i = 0; i < count; i++) {
            symbolEntries.add(getSymbolEntry(buffer));
        }
        return symbolEntries;
    }

    private static SymbolEntry getSymbolEntry(ByteBuffer buffer) {
        var startPosition = buffer.position();

        var entryLength = getUnsignedInt(buffer);

        var indexGroup = getUnsignedInt(buffer);
        var indexOffset = getUnsignedInt(buffer);

        var size = getUnsignedInt(buffer);

        var dataType = AdsDataType.getByDataTypeId(buffer.getInt()).get();
        var flags = decodeSymbolFlags(buffer.getInt());

        var nameLength = getUnsignedShort(buffer);
        var typeNameLength = getUnsignedShort(buffer);
        var commentLength = getUnsignedShort(buffer);

        var nameBytes = new byte[nameLength + 1];
        buffer.get(nameBytes);
        var name = new String(nameBytes).trim();

        var typeNameBytes = new byte[typeNameLength + 1];
        buffer.get(typeNameBytes);
        var typeName = new String(typeNameBytes).trim();

        var commentBytes = new byte[commentLength + 1];
        buffer.get(commentBytes);
        var comment = new String(commentBytes).trim();

        Optional<UUID> typeGuid = Optional.empty();
        if (flags.hasTypeGuid()) {
            typeGuid = Optional.of(getGuid(buffer));
        }

        buffer.position(startPosition + (int) entryLength);

        return new SymbolEntry(name, typeName, comment,
                indexGroup, indexOffset, size,
                dataType, typeGuid, flags);
    }

    private static SymbolStream.Flags decodeSymbolStreamFlags(int data) {
        var onlineChange = (data & 0x0001) > 0;
        var isTarget64Bit = (data & 0x0002) > 0;
        var hasBaseTypesIncluded = (data & 0x0004) > 0;
        var performQSortMask = (data & 0x0008) > 0;

        return new SymbolStream.Flags(onlineChange, isTarget64Bit, hasBaseTypesIncluded, performQSortMask);
    }

    private static SymbolEntry.Flags decodeSymbolFlags(int data) {
        var isPersistent = (data & 0x00000001) > 0;
        var isBitValue = (data & 0x00000002) > 0;
        var isReferenceTo = (data & 0x00000004) > 0;
        var hasTypeGuid = (data & 0x00000008) > 0;
        var isTwincatComInterfacePointer = (data & 0x00000010) > 0;
        var isReadOnly = (data & 0x00000020) > 0;
        var isInterfaceMethodAccess = (data & 0x00000040) > 0;
        var isMethodDeref = (data & 0x00000080) > 0;
        var contextMask = (byte) (data & 0x00000f00);
        var hasAttributes = (data & 0x00001000) > 0;
        var isStatic = (data & 0x00002000) > 0;
        var isInitialisedOnReset = (data & 0x00004000) > 0;
        var hasExtendedFlags = (data & 0x00008000) > 0;

        return new SymbolEntry.Flags(isPersistent, isBitValue, isReferenceTo, hasTypeGuid, isTwincatComInterfacePointer,
                isReadOnly, isInterfaceMethodAccess, isMethodDeref, contextMask, hasAttributes, isStatic,
                isInitialisedOnReset, hasExtendedFlags);
    }

    private static List<DataTypeEntry> getDataTypeEntries(ByteBuffer buffer, int count) {
        var dataTypeEntries = new ArrayList<DataTypeEntry>(count);
        for (var i = 0; i < count; i++) {
            dataTypeEntries.add(getDataTypeEntry(buffer));
        }

        return dataTypeEntries;
    }
    
    private static DataTypeEntry getDataTypeEntry(ByteBuffer buffer) {
        var startPosition = buffer.position();

        var entryLength = getUnsignedInt(buffer);

        var version = getUnsignedInt(buffer);
        var hashValue = buffer.getInt();
        var typeHashValue = buffer.getInt();
        var size = getUnsignedInt(buffer);
        var offset = getUnsignedInt(buffer);

        var dataType = AdsDataType.getByDataTypeId(getUnsignedInt(buffer));

        var flags = decodeDataTypeEntryFlags(buffer.getInt());

        var nameLength = getUnsignedShort(buffer);
        var typeNameLength = getUnsignedShort(buffer);
        var commentLength = getUnsignedShort(buffer);

        var arrayDimensions = getUnsignedShort(buffer);
        var subItemCount = getUnsignedShort(buffer);

        var nameBytes = new byte[nameLength + 1];
        buffer.get(nameBytes);
        var name = new String(nameBytes).trim();

        var typeNameBytes = new byte[typeNameLength + 1];
        buffer.get(typeNameBytes);
        var typeName = new String(typeNameBytes).trim();

        var commentBytes = new byte[commentLength + 1];
        buffer.get(commentBytes);
        var comment = new String(commentBytes).trim();

        Optional<DataTypeEntry.ArrayInformation> arrayInformation;
        if (arrayDimensions > 0) {
            var arrayLowerBounds = getUnsignedInt(buffer);
            var arrayLength = getUnsignedInt(buffer);
            arrayInformation = Optional.of(new DataTypeEntry.ArrayInformation(arrayLowerBounds, arrayLength));
        } else {
            arrayInformation = Optional.empty();
        }

        List<DataTypeEntry> subItems;
        if (subItemCount > 0) {
            subItems = getDataTypeEntries(buffer, subItemCount);
        } else {
            subItems = Collections.emptyList();
        }

        Optional<UUID> typeGuid;
        if (flags.hasTypeGuid()) {
            typeGuid = Optional.of(getGuid(buffer));
        } else {
            typeGuid = Optional.empty();
        }

        Optional<byte[]> copyMask = Optional.empty();
        if (flags.hasCopyMask()) {
            var data = new byte[(int) size];
            buffer.get(data);
            copyMask = Optional.of(data);
        } else {
            copyMask = Optional.empty();
        }

        List<DataTypeEntry.MethodInformation> methodInformation;
        if (flags.hasMethodInfos()) {
            methodInformation = getMethodInformation(buffer);
        } else {
            methodInformation = Collections.emptyList();
        }

        Map<String, String> attributes;
        if (flags.hasAttributes()) {
            attributes = getAttributes(buffer);
        } else {
            attributes = Collections.emptyMap();
        }

        Map<String, byte[]> enumInformation;
        if (flags.hasEnumInfos()) {
            enumInformation = getEnumInformation(buffer, (int) size);
        } else {
            enumInformation = Collections.emptyMap();
        }

        buffer.position(startPosition + (int) entryLength);

        return new DataTypeEntry(version, hashValue, typeHashValue, size, offset, dataType.get(), flags,
                name, typeName, comment, typeGuid, copyMask, arrayDimensions, arrayInformation, methodInformation,
                enumInformation, attributes, subItems);
    }

    private static DataTypeEntry.Flags decodeDataTypeEntryFlags(int data) {
        boolean isDataType =                   (data & 0x00000001) > 0;
        boolean isDataItem =                   (data & 0x00000002) > 0;
        boolean isReferenceTo =                (data & 0x00000004) > 0;
        boolean isMethodDeref =                (data & 0x00000008) > 0;
        boolean isOversamplingArray =          (data & 0x00000010) > 0;
        boolean isBitValue =                   (data & 0x00000020) > 0;
        boolean isPropertyItem =               (data & 0x00000040) > 0;
        boolean hasTypeGuid =                  (data & 0x00000080) > 0;
        boolean isPersistent =                 (data & 0x00000100) > 0;
        boolean hasCopyMask =                  (data & 0x00000200) > 0;
        boolean isTwincatComInterfacePointer = (data & 0x00000400) > 0;
        boolean hasMethodInfos =               (data & 0x00000800) > 0;
        boolean hasAttributes =                (data & 0x00001000) > 0;
        boolean hasEnumInfos =                 (data & 0x00002000) > 0;
        // skip                                (data & 0x00004000) > 0;
        // skip                                (data & 0x00008000) > 0;
        boolean isByteAligned =                (data & 0x00010000) > 0;
        boolean isStatic =                     (data & 0x00020000) > 0;
        boolean spLevels =                     (data & 0x00040000) > 0;
        boolean ignorePersist =                (data & 0x00080000) > 0;
        boolean isAnySizeArray =               (data & 0x00100000) > 0;
        boolean isPersistantDatatype =         (data & 0x00200000) > 0;
        boolean isInitialisedOnResult =        (data & 0x00400000) > 0;

        return new DataTypeEntry.Flags(isDataType, isDataItem, isReferenceTo, isMethodDeref, isOversamplingArray,
                isBitValue, isPropertyItem, hasTypeGuid, isPersistent, hasCopyMask, isTwincatComInterfacePointer,
                hasMethodInfos, hasAttributes, hasEnumInfos, isByteAligned, isStatic, spLevels, ignorePersist,
                isAnySizeArray, isPersistantDatatype, isInitialisedOnResult);
    }

    private static List<DataTypeEntry.MethodInformation> getMethodInformation(ByteBuffer buffer) {
        return Collections.emptyList();
    }

    private static Map<String, byte[]> getEnumInformation(ByteBuffer buffer, int size) {
        var enumItemCount = getUnsignedShort(buffer);
        var enumInformation = new HashMap<String, byte[]>(enumItemCount);
        
        for (var i = 0; i < enumItemCount; i++) {
            var nameLength = getUnsignedByte(buffer);

            var nameBytes = new byte[nameLength + 1];
            buffer.get(nameBytes);
            var name = new String(nameBytes).trim();

            var data = new byte[size];
            buffer.get(data);
            enumInformation.put(name, data);
        }

        return enumInformation;
    }

    private static Map<String, String> getAttributes(ByteBuffer buffer) {
        var attributeCount = getUnsignedShort(buffer);
        var attributes = new HashMap<String, String>(attributeCount);

        for (var i = 0; i < attributeCount; i++) {
            var keyLength = getUnsignedByte(buffer);
            var valueLength = getUnsignedByte(buffer);

            var keyBytes = new byte[keyLength + 1];
            buffer.get(keyBytes);
            var key = new String(keyBytes).trim();
    
            var valueBytes = new byte[valueLength + 1];
            buffer.get(valueBytes);
            var value = new String(valueBytes).trim();

            attributes.put(key, value);
        }

        return attributes;
    }

}
