package com.siyka.twincat.analytics.mqttbinarydecoder.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.messaging.Message;

import com.siyka.twincat.analytics.mqttbinarydecoder.SymbolService;
import com.siyka.twincat.analytics.mqttbinarydecoder.analytics.SymbolStream;

@Configuration
public class TcAnalyticsIntegration {
    
    public static final String DESCRIPTION_UPDATES_CHANNEL_NAME = "descriptionUpdates";
    public static final String SYMBOL_UPDATES_CHANNEL_NAME = "symbolUpdates";
    public static final String DATA_UPDATES_CHANNEL_NAME = "dataUpdates";
    public static final String MAGIC_HEADER = "stream";

    @Autowired
    private SymbolService service;

    @Bean
    IntegrationFlow descriptionNotifications() {

        record Description(
            String source,
            String layout,
            int cycletTime,
            int dataSize,
            String systemId
        ) {};

        return IntegrationFlow.from(DESCRIPTION_UPDATES_CHANNEL_NAME)
            .filter(Message.class, m -> m.getHeaders().containsKey(MAGIC_HEADER))
            .transform(Transformers.fromJson(Description.class))
            .handle(System.out::println)
            .get();
    }

    @Bean
    IntegrationFlow symbolNotifications() {
        return IntegrationFlow.from(SYMBOL_UPDATES_CHANNEL_NAME)
            .filter(Message.class, m -> m.getHeaders().containsKey(MAGIC_HEADER))
            .handle(m -> {
                final var streamName = m.getHeaders().get(MAGIC_HEADER, String.class);
                final var payload = m.getPayload();
                if (payload instanceof SymbolStream stream) {
                    
                    service.registerSymbolStream(streamName, stream);
                }
            })
            .get();
    }

}
