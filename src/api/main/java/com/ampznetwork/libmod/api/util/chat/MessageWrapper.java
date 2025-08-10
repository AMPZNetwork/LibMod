package com.ampznetwork.libmod.api.util.chat;

import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.entity.Player;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.comroid.annotations.Doc;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.*;

@Value
@NonFinal
public abstract class MessageWrapper {
    TextColor themeColor;

    protected abstract Stream<Player> getTargets();

    public void sendMessage(@Doc("{} styled format string") String format, Object... args) {
        sendMessage(BroadcastType.INFO, format, args);
    }

    public void sendMessage(
            @Nullable @MagicConstant(valuesFromClass = BroadcastType.class) Colorizer colorizer, @Doc("{} styled format string") String format,
            @Nullable Object... args
    ) {
        if (args == null) args = new Object[0];
        if (colorizer == null) colorizer = BroadcastType.INFO;
        var msg = createMessage(colorizer, format, args);
        getTargets().map(DbObject::getId).forEach(p -> wrapper().getLib().getPlayerAdapter().send(p, msg));
    }

    public Component createMessage(
            @Doc("{} styled format string") String format,
            @Nullable Object... args
    ) {
        return createMessage(null, format, args);
    }

    @Builder(builderClassName = "MessageDraft", builderMethodName = "draft")
    public Component createMessage(
            @Nullable @MagicConstant(valuesFromClass = BroadcastType.class) Colorizer colorizer, @Doc("{} styled format string") String format,
            @Nullable Object... args
    ) {
        if (args == null) args = new Object[0];
        if (colorizer == null) colorizer = BroadcastType.INFO;
        var prefix = text("").append(text("[", colorizer.getDecorationColor()))
                .append(text(wrapper().getName(), themeColor))
                .append(text("] ", colorizer.getDecorationColor()));
        var text = colorizer.colorize(format, args);
        return prefix.append(text);
    }

    protected abstract BroadcastWrapper wrapper();
}
