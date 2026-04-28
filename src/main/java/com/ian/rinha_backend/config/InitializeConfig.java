package com.ian.rinha_backend.config;

import com.ian.rinha_backend.model.ReferencesModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

@Configuration
public class InitializeConfig {

    @Bean(name = "mccRiskMap")
    public Map<String, Double> mccRiskMap() {
        InputStream resourceAsStream = getClass().getResourceAsStream("/mcc_risk.json");
        return new ObjectMapper().readValue(resourceAsStream, new TypeReference<Map<String, Double>>() {
        });
    }

    @Bean(name = "references")
    public ReferencesModel[] referencesArray() {
        InputStream resourceAsStream = getClass().getResourceAsStream("/references.json");
        return new ObjectMapper().readValue(resourceAsStream, ReferencesModel[].class);
    }
}
