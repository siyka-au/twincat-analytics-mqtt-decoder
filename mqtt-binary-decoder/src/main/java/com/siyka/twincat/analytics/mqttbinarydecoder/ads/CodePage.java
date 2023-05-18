package com.siyka.twincat.analytics.mqttbinarydecoder.ads;

public enum CodePage {
    UTF8(65001);

    private final long codePageValue;

    private CodePage(long codePageValue) { this.codePageValue = codePageValue; }

    public long getCodePageValue() {
        return codePageValue;
    }
}
