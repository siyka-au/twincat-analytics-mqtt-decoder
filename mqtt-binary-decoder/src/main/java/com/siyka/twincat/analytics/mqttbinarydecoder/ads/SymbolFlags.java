package com.siyka.twincat.analytics.mqttbinarydecoder.ads;

public record SymbolFlags(
    boolean isPersistent,
    boolean isBit_value,
    boolean isReference_to,
    boolean hasType_guid,
    boolean isTcComInterfacePointer,
    boolean isReadOnly,
    boolean isInterfaceMethodAccess,
    boolean isMethodDeref,
    int contextMask,
    boolean hasAttributes,
    boolean isStatic,
    boolean isInitialisedOnReset,
    boolean hasExtendedFlags) {

}
