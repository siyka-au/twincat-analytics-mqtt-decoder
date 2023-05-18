package com.siyka.twincat.analytics.mqttbinarydecoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MqttBinaryDecoderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqttBinaryDecoderApplication.class, args);
	}

}
