package com.ampznetwork.libmod.api.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.comroid.api.Polyfill;
import org.comroid.api.text.Capitalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum NameGenerator implements IntFunction<String>, Function<Capitalization, String>, Supplier<String> {
    /** generates random nouns */
    NOUNS(1, "https://raw.githubusercontent.com/AMPZNetwork/WorldMod/main/src/api/main/resources/nouns.txt"),

    /** generates names for points of interest */
    POI(2, "https://raw.githubusercontent.com/AMPZNetwork/WorldMod/main/src/api/main/resources/adverbs.txt",
            "https://raw.githubusercontent.com/AMPZNetwork/WorldMod/main/src/api/main/resources/nouns.txt");

    /** default length */
    int               defaultLength;
    List<Set<String>> nameLists;
    Random            rng = new Random();

    NameGenerator(int defaultLength, String... nameListResourceUrls) {
        this.defaultLength = defaultLength;
        this.nameLists     = Arrays.stream(nameListResourceUrls)
                .map(Polyfill::url)
                .map(url -> {
                    try (
                            var is = url.openStream();
                            var isr = new InputStreamReader(is);
                            var br = new BufferedReader(isr)
                    ) {
                        return br.lines().collect(Collectors.toSet());
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to load words", e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * @param length number of separate words
     *
     * @return a random name generated by this instances resources
     */
    @Override
    public String apply(int length) {
        if (length < 1)
            length = defaultLength;
        return nameLists.stream()
                .flatMap(strings -> strings.stream().limit(1))
                .limit(length)
                .map(String::toLowerCase)
                .collect(Collectors.joining("_"));
    }

    @Override
    public String apply(Capitalization capitalization) {
        return capitalization.convert(apply(-1));
    }

    @Override
    public String get() {
        return apply(Capitalization.Title_Case);
    }
}
