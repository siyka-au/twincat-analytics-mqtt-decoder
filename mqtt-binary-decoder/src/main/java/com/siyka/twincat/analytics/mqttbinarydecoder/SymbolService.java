package com.siyka.twincat.analytics.mqttbinarydecoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.siyka.twincat.analytics.mqttbinarydecoder.symbols.SymbolStream;

@Service
public class SymbolService {
    
    private Map<String, SymbolStream> streams = new HashMap<>();

    public Map<String, SymbolStream> getAllSymbolStreams() {
        return streams;
    }

    public Optional<SymbolStream> getSymbolStreamByAssetName(final String assetName) {
        return Optional.ofNullable(streams.get(assetName));
    }

    public void registerSymbolStream(final String assetName, SymbolStream stream) {
        this.streams.put(assetName, stream);
    }

}
