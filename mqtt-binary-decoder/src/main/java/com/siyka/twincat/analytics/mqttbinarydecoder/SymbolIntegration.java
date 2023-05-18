package com.siyka.twincat.analytics.mqttbinarydecoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

import com.siyka.twincat.analytics.mqttbinarydecoder.symbols.SymbolStream;

@Configuration
public class SymbolIntegration {
    
    @Autowired
    private SymbolService service;

    @Bean
    IntegrationFlow symbolNotifications() {
        return IntegrationFlow.from("symbolUpdates")
            .handle(m -> {
                if (m.getHeaders().containsKey("assetName")) {
                    var assetName = m.getHeaders().get("assetName", String.class);
                    var payload = m.getPayload();
                    if (payload instanceof SymbolStream) {
                        service.registerSymbolStream(assetName, (SymbolStream) payload);
                    }
                }
            })
            .get();
    }

}
