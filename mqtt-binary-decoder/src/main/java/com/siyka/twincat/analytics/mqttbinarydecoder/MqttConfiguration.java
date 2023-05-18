package com.siyka.twincat.analytics.mqttbinarydecoder;

import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.core.Mqttv5ClientManager;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;
import org.springframework.stereotype.Component;

import com.siyka.twincat.analytics.mqttbinarydecoder.analytics.AnalyticsStreamDecoder;

import jakarta.annotation.PostConstruct;

@Configuration
public class MqttConfiguration {

    @Autowired
    private MqttStreamProperties properties;

    @Bean
    ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager() {
        MqttConnectionOptions connectionOptions = new MqttConnectionOptions();
        connectionOptions.setServerURIs(new String[] { properties.host() });
        connectionOptions.setConnectionTimeout(properties.connetionTimeout()); // 30000
        connectionOptions.setMaxReconnectDelay(properties.connectionDelay()); // 1000
        connectionOptions.setAutomaticReconnect(properties.automaticReconnect()); // true
        connectionOptions.setUserName(properties.username());
        connectionOptions.setPassword(properties.password().getBytes());
        Mqttv5ClientManager clientManager = new Mqttv5ClientManager(connectionOptions, properties.clientId());
        clientManager.setPersistence(new MqttDefaultFilePersistence());
        return clientManager;
    }

    @Component
    public static class FlowFactory {

        private final IntegrationFlowContext flowContext;
        private final ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager;
        private final SymbolService symbolService;
        private final MqttStreamProperties properties;

        FlowFactory(IntegrationFlowContext flowContext,
                ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager,
                SymbolService symbolService,
                MqttStreamProperties properties) {
            this.flowContext = flowContext;
            this.clientManager = clientManager;
            this.symbolService = symbolService;
            this.properties = properties;
        }

        @PostConstruct
        public void init() {
            for (var stream : properties.streams().entrySet()) {
                var messageProducerSymbols = new Mqttv5PahoMessageDrivenChannelAdapter(clientManager,
                        stream.getKey() + "/" + stream.getValue() + "/Bin/Tx/Symbols");

                this.flowContext.registration(
                        IntegrationFlow.from(messageProducerSymbols)
                                .transform(new SymbolStreamTransformer())
                                .enrichHeaders(h -> h.header("assetName", stream.getKey()))
                                .channel("symbolUpdates")
                                .get())
                        .register();

                var messageProducerData = new Mqttv5PahoMessageDrivenChannelAdapter(clientManager,
                        stream.getKey() + "/" + stream.getValue() + "/Bin/Tx/Data");

                // this.flowContext.registration(
                // IntegrationFlow.from(messageProducerData)
                // .transform(new SymbolStreamTransformer())
                // .enrichHeaders(h -> h.header("assetName", stream.getKey()))
                // .channel("dataUpdates")
                // .get()
                // ).register();

                this.flowContext.registration(
                        IntegrationFlow.from(messageProducerData)
                                .enrichHeaders(h -> h.header("assetName", stream.getKey()))
                                .handle(m -> {
                                    var decoder = new AnalyticsStreamDecoder(symbolService, stream.getKey());
                                    var a = decoder.transform((byte[]) m.getPayload());
                                    System.out.println(a.toString());
                                })
                                .get())
                        .register();
            }
        }
    }

}
