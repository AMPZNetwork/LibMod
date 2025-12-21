package com.ampznetwork.libmod.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.comroid.api.text.Markdown;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.*;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.*;

public interface Util {
    class Kyori {
        public static final ComponentFlattener COMPONENT_TO_MARKDOWN = ComponentFlattener.basic()
                .toBuilder()
                .mapper(TextComponent.class, text -> {
                    var feats = text.decorations()
                            .entrySet()
                            .stream()
                            .filter(e -> e.getValue() == TextDecoration.State.TRUE)
                            .map(Map.Entry::getKey)
                            .map(decor -> switch (decor) {
                                case OBFUSCATED -> Markdown.Code;
                                case BOLD -> Markdown.Bold;
                                case STRIKETHROUGH -> Markdown.Strikethrough;
                                case UNDERLINED -> Markdown.Underline;
                                case ITALIC -> Markdown.Italic;
                            })
                            .toList();
                    var str = text.content();
                    for (var feat : feats)
                        str = feat.apply(str);
                    return str;
                })
                .build();

        public static String sanitizePlain(String string) {
            return sanitize(string, plainText());
        }

        public static String sanitize(String string) {
            return sanitize(string, legacyAmpersand());
        }

        public static <R> R sanitize(String string, ComponentSerializer<Component, TextComponent, R> output) {
            ComponentSerializer<Component, TextComponent, String> first = null, last;
            if (legacyAmpersand().equals(output)) last = legacySection();
            else if (legacySection().equals(output)) last = legacyAmpersand();
            else {
                first = legacySection();
                last  = legacyAmpersand();
            }
            if (first != null) string = last.serialize(first.deserialize(string));
            var out = last.deserialize(string);
            return output.serialize(out);
        }

        public static Collector<ComponentLike, List<ComponentLike>, Component> collector() {
            return collector(null);
        }

        public static Collector<ComponentLike, List<ComponentLike>, Component> collector(
                @Nullable Component delimiter
        ) {
            return Collector.of(ArrayList::new, Collection::add, (l, r) -> {
                l.addAll(r);
                return l;
            }, ls -> {
                var txt = Component.text();
                for (var iterator = ls.iterator(); iterator.hasNext(); ) {
                    var l = iterator.next();
                    txt.append(l);
                    if (iterator.hasNext() && delimiter != null) txt.append(delimiter);
                }
                return txt.build();
            });
        }
    }
}
