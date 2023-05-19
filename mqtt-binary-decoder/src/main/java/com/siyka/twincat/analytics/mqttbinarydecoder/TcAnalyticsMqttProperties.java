package com.siyka.twincat.analytics.mqttbinarydecoder;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("twincat-analytics-mqtt-stream")
public record TcAnalyticsMqttProperties(String host, String username, String password, String clientId,
                int connetionTimeout, int connectionDelay, boolean automaticReconnect,
                Map<String, Stream> streams) {

    public static record Stream(String mainTopic, String streamName) {}
}
