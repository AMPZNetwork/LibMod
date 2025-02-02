package com.ampznetwork.libmod.api.model.info;

import com.ampznetwork.libmod.api.LibMod;
import org.comroid.api.func.util.Command;

import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface ValueAutofillOptionsProvider {
    ValueAutofillOptionsProvider NUMERICS = ($0, $1, mod, value) -> expandDigit(value).flatMap(ValueAutofillOptionsProvider::expandRange)
            .filter(Predicate.not(String::isBlank))
            .filter(str -> str.matches("(~?-?\\d+)?(\\.{2}(~?-?\\d+)?)?"))
            .map(str -> str.replaceAll("\\.{3,}", ".."))
            .map(str -> str.replaceAll("-{2,}", "-"))
            .distinct()
            .sorted(java.util.Comparator.comparingInt(String::length));

    static Stream<String> expandDigit(String value) {
        return Stream.of(value).flatMap(num -> Stream.concat(Stream.of(num, '-' + num), IntStream.range(0, 10).mapToObj(digit -> num + digit)));
    }

    static Stream<String> expandRange(String value) {
        return Stream.concat(Stream.of(value),
                Stream.of(value).map(num -> num + "..").flatMap(num -> Stream.concat(Stream.of(num), IntStream.range(0, 10).mapToObj(digit -> num + digit))));
    }

    Stream<String> autoFillValue(Command.Usage usage, String argName, LibMod mod, String value);
}
