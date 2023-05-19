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

import com.siyka.twincat.analytics.mqttbinarydecoder.analytics.DataStreamDecoder;
import com.siyka.twincat.analytics.mqttbinarydecoder.integration.DataStreamTransformer;
import com.siyka.twincat.analytics.mqttbinarydecoder.integration.SymbolStreamTransformer;
import com.siyka.twincat.analytics.mqttbinarydecoder.integration.TcAnalyticsIntegration;

import jakarta.annotation.PostConstruct;

@Configuration
public class MqttConfiguration {

    private final static String TC_ANALYTICS_SYMBOL_TOPIC = "/Bin/Tx/Symbols";
    private final static String TC_ANALYTICS_DATA_TOPIC = "/Bin/Tx/Data";

    @Autowired
    private TcAnalyticsMqttProperties properties;

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
        private final TcAnalyticsMqttProperties properties;

        private final SymbolStreamTransformer symbolStreamTransformer = new SymbolStreamTransformer();
        private final DataStreamTransformer dataStreamTransformer = new DataStreamTransformer();

        FlowFactory(IntegrationFlowContext flowContext,
                ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager,
                SymbolService symbolService,
                TcAnalyticsMqttProperties properties) {
            this.flowContext = flowContext;
            this.clientManager = clientManager;
            this.symbolService = symbolService;
            this.properties = properties;
        }

        @PostConstruct
        public void init() {
            for (var streamConfig : properties.streams().entrySet()) {

                var name = streamConfig.getKey();
                var analyticsStream = streamConfig.getValue();

                var symbolsMessageProducer = new Mqttv5PahoMessageDrivenChannelAdapter(clientManager,
                        analyticsStream.mainTopic() + "/" + analyticsStream.streamName() + TC_ANALYTICS_SYMBOL_TOPIC);

                this.flowContext.registration(
                        IntegrationFlow.from(symbolsMessageProducer)
                                .transform(symbolStreamTransformer)
                                .enrichHeaders(h -> h.header(TcAnalyticsIntegration.MAGIC_HEADER, name))
                                .channel(TcAnalyticsIntegration.SYMBOL_UPDATES_CHANNEL_NAME)
                                .get())
                        .register();

                var dataMessageProducer = new Mqttv5PahoMessageDrivenChannelAdapter(clientManager,
                        analyticsStream.mainTopic() + "/" + analyticsStream.streamName() + TC_ANALYTICS_DATA_TOPIC);

                // this.flowContext.registration(
                // IntegrationFlow.from(messageProducerData)
                // .transform(new SymbolStreamTransformer())
                // .enrichHeaders(h -> h.header(TcAnalyticsIntegration.MAGIC_HEADER, name))
                // .channel(TcAnalyticsIntegration.DATA_UPDATES_CHANNEL_NAME)
                // .get()
                // ).register();

                this.flowContext.registration(
                        IntegrationFlow.from(dataMessageProducer)
                            .transform(dataStreamTransformer)
                                .enrichHeaders(h -> h.header(TcAnalyticsIntegration.MAGIC_HEADER, name))
                                .handle(m -> {
                                    System.out.println("Receiving data from " + m.getHeaders().get(TcAnalyticsIntegration.MAGIC_HEADER));
                                    System.out.println(m);
                                    System.out.println();
                                    // var decoder = new AnalyticsStreamDecoder(symbolService, name);
                                    // var a = decoder.transform((byte[]) m.getPayload());
                                    // System.out.println(a.toString());
                                })
                                .get())
                        .register();
            }
        }
    }

}
