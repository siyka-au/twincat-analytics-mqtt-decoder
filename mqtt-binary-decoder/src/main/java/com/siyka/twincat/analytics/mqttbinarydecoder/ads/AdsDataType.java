package com.siyka.twincat.analytics.mqttbinarydecoder.ads;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum AdsDataType {
    VOID(0),
    BIT(33),
    INT8(16),
    UINT8(17),
    INT16(2),
    UINT16(18),
    INT32(3),
    UINT32(19),
    INT64(20),
    UINT64(21),
    REAL32(4),
    REAL64(5),
    REAL80(32),
    STRING(30),
    W_STRING(31),
    MAX_TYPES(34),
    BIG_TYPE(65);

    private final long dataTypeId;
    private static final Map<Long, AdsDataType> lookup = new HashMap<>();

    static {
        for (AdsDataType i : AdsDataType.values()) {
            lookup.put(i.getDataTypeId(), i);
        }
    }

    private AdsDataType(final long dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public long getDataTypeId() {
        return this.dataTypeId;
    }

    public static Optional<AdsDataType> getByDataTypeId(long dataTypeId) {
        return Optional.ofNullable(lookup.get(dataTypeId));
    }

}
