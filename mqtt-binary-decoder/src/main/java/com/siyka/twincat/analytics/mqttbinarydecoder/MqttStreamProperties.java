package com.siyka.twincat.analytics.mqttbinarydecoder;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("twincat-analytics-mqtt-stream")
public record MqttStreamProperties(String host, String username, String password, String clientId,
                int connetionTimeout, int connectionDelay, boolean automaticReconnect,
                Map<String, String> streams) {
}
