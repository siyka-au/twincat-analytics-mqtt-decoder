package com.siyka.twincat.analytics.mqttbinarydecoder.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siyka.twincat.analytics.mqttbinarydecoder.SymbolService;
import com.siyka.twincat.analytics.mqttbinarydecoder.symbols.SymbolStream;

@RestController
@RequestMapping("/symbols")
public class SymbolController {

    @Autowired
    private SymbolService service;

    @GetMapping
    public Map<String, SymbolStream> getAllSymbolStreams() {
        return service.getAllSymbolStreams();
    }

    @GetMapping("/{assetName}")
    public Optional<SymbolStream> getSymbolStream(@PathVariable String assetName) {
        return service.getSymbolStreamByAssetName(assetName);
    }

}
