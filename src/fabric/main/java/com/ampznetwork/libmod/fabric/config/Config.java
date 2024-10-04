package com.ampznetwork.libmod.fabric.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.Name;
import lombok.extern.slf4j.Slf4j;
import org.comroid.annotations.internal.Annotations;
import org.comroid.api.io.FileHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

@Slf4j
public class Config {
    public static <T extends Config> T createAndLoad(Class<T> type) {
        var name = Annotations.findAnnotations(Name.class, type)
                .findAny()
                .map(Annotations.Result::getAnnotation)
                .map(Name::value)
                .orElseGet(type::getSimpleName);
        var file = new FileHandle(new File("config/" + name + ".json5"));

        // copy default from resources if not present
        if (!file.exists())
            try {
                try (
                        var resource = type.getResourceAsStream(name + ".json");
                        var in = new InputStreamReader(Objects.requireNonNull(resource));
                        var out = file.openWriter()
                ) {
                    in.transferTo(out);
                }
            } catch (Throwable t) {
                log.warn("Could not initialize default configuration file", t);
            }

        // load from file
        T config;
        try {
            config = new ObjectMapper(JsonFactory.builder()
                    .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
                    .build()
                    .enable(JsonParser.Feature.ALLOW_COMMENTS)
                    .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION))
                    .readValue(file, type);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize Configuration", e);
        }

        // todo: save once to migrate new defaults

        return config;
    }
}
