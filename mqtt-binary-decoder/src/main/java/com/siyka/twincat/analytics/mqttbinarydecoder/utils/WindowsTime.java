package com.siyka.twincat.analytics.mqttbinarydecoder.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class WindowsTime {

    public static final Instant ZERO = Instant.parse("1601-01-01T00:00:00Z");

    public static long fromInstant(Instant instant) {
        var duration = Duration.between(ZERO, instant);
        return duration.getSeconds() * 10_000_000 + duration.getNano() / 100;
    }

    public static Instant toInstant(long fileTime) {
        Duration duration = Duration.of(fileTime / 10, ChronoUnit.MICROS).plus(fileTime % 10 * 100, ChronoUnit.NANOS);
        return ZERO.plus(duration);
    }

    public static Duration toDuration(long duration) {
        return Duration.ofNanos(duration * 100);
    }

}
