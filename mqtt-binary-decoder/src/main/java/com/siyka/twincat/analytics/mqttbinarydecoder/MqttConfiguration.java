package com.siyka.twincat.analytics.mqttbinarydecoder;

import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.core.Mqttv5ClientManager;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;

import com.siyka.twincat.analytics.mqttbinarydecoder.analytics.AnalyticsStreamDecoder;

@Configuration
public class MqttConfiguration {

    @Autowired
    private SymbolService symbolService;

    @Bean
    ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager() {
        MqttConnectionOptions connectionOptions = new MqttConnectionOptions();
        connectionOptions.setServerURIs(new String[] { "tcp://localhost:1883" });
        connectionOptions.setConnectionTimeout(30000);
        connectionOptions.setMaxReconnectDelay(1000);
        connectionOptions.setAutomaticReconnect(true);
        connectionOptions.setUserName("username");
        connectionOptions.setPassword("password".getBytes());
        Mqttv5ClientManager clientManager = new Mqttv5ClientManager(connectionOptions, "clientId");
        clientManager.setPersistence(new MqttDefaultFilePersistence());
        return clientManager;
    }

    @Bean
    IntegrationFlow mqttInFlowEmaSymbols(ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager) {
        return createSymbolInFlow(clientManager, "someAssetName");
    }


    @Bean
    IntegrationFlow mqttInFlowEmaData(ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager) {
        return createDataInFlow(clientManager, "someAssetName");
    }

    private IntegrationFlow createSymbolInFlow(ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager, String assetName) {
        Mqttv5PahoMessageDrivenChannelAdapter messageProducer = new Mqttv5PahoMessageDrivenChannelAdapter(clientManager, assetName + "/PlcStream1/Bin/Tx/Symbols");
        
        return IntegrationFlow.from(messageProducer)
                .transform(new SymbolStreamTransformer())
                .enrichHeaders(h -> h.header("assetName", assetName))
                .channel("symbolUpdates")
                .get();
    }

    private IntegrationFlow createDataInFlow(ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager, String assetName) {
        Mqttv5PahoMessageDrivenChannelAdapter messageProducer = new Mqttv5PahoMessageDrivenChannelAdapter(clientManager, assetName + "/PlcStream1/Bin/Tx/Data");

        return IntegrationFlow.from(messageProducer)
                .enrichHeaders(h -> h.header("assetName", assetName))
                .handle(m -> {
                    var decoder = new AnalyticsStreamDecoder(symbolService, assetName);

                    var a = decoder.transform((byte[]) m.getPayload());

                    System.out.println(a.toString());
                })
                .get();
    }

}
