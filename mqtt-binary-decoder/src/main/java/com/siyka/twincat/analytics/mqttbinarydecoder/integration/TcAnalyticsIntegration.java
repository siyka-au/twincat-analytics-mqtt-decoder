package com.siyka.twincat.analytics.mqttbinarydecoder.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

import com.siyka.twincat.analytics.mqttbinarydecoder.SymbolService;
import com.siyka.twincat.analytics.mqttbinarydecoder.analytics.SymbolStream;

@Configuration
public class TcAnalyticsIntegration {
    
    public static final String SYMBOL_UPDATES_CHANNEL_NAME = "symbolUpdates";
    public static final String DATA_UPDATES_CHANNEL_NAME = "dataUpdates";
    public static final String MAGIC_HEADER = "stream";

    @Autowired
    private SymbolService service;

    @Bean
    IntegrationFlow symbolNotifications() {
        return IntegrationFlow.from(SYMBOL_UPDATES_CHANNEL_NAME)
            .handle(m -> {
                if (m.getHeaders().containsKey(MAGIC_HEADER)) {
                    var streamName = m.getHeaders().get(MAGIC_HEADER, String.class);
                    var payload = m.getPayload();
                    if (payload instanceof SymbolStream) {
                        
                        service.registerSymbolStream(streamName, (SymbolStream) payload);
                    }
                }
            })
            .get();
    }

}
