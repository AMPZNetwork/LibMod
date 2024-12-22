package com.ampznetwork.libmod.api.util.chat;

import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.entity.Player;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import net.kyori.adventure.text.format.TextColor;
import org.comroid.annotations.Doc;
import org.intellij.lang.annotations.MagicConstant;

import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.*;

@Value
@NonFinal
public abstract class MessageWrapper {
    TextColor themeColor;

    protected abstract Stream<Player> getTargets();

    @Builder(builderClassName = "MessageBuilder", builderMethodName = "createMessage", buildMethodName = "send")
    public void sendMessage(@Doc("{} styled format string") String format, Object... args) {
        sendMessage(BroadcastType.INFO, format, args);
    }

    @Builder(builderClassName = "MessageBuilder", builderMethodName = "createMessage", buildMethodName = "send")
    public void sendMessage(@MagicConstant(valuesFromClass = BroadcastType.class) Colorizer colorizer, @Doc("{} styled format string") String format, Object... args) {
        var prefix = text("")
                .append(text("[", colorizer.getDecorationColor())).append(text(wrapper().getName(), themeColor))
                .append(text("] ", colorizer.getDecorationColor()));
        var       text = colorizer.colorize(format, args);
        final var msg  = prefix.append(text);
        wrapper().getTargets()
                .map(DbObject::getId)
                .forEach(p -> wrapper().getLib()
                        .getPlayerAdapter()
                        .send(p, msg));
    }

    protected abstract BroadcastWrapper wrapper();
}
