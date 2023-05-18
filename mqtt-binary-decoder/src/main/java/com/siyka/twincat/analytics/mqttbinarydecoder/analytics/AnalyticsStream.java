package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import java.util.List;

public class AnalyticsStream {

    private final AnalyticsStreamHeader header;
    private final List<DataPoint> data;

    public AnalyticsStream(AnalyticsStreamHeader header, List<DataPoint> data) {
        this.header = header;
        this.data = data;
    }

    public AnalyticsStreamHeader getHeader() {
        return header;
    }

    public List<DataPoint> getData() {
        return data;
    }

    public String toString() {
        var sb = new StringBuilder(header.toString());
        for (var dp : data) {
            sb.append(dp.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
