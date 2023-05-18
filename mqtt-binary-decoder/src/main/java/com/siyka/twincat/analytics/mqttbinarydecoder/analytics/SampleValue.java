package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

public class SampleValue<T> {

    private final T value;

    public SampleValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

}
