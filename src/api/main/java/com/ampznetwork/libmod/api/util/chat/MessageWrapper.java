package com.ampznetwork.libmod.api.util.chat;

import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.entity.Player;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.annotations.Doc;

import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.*;

@Value
@NonFinal
public abstract class MessageWrapper {
    protected abstract Stream<Player> getTargets();

    @Builder(builderClassName = "MessageBuilder", builderMethodName = "createMessage", buildMethodName = "send")
    public void sendMessage(@Doc("{} styled format string") String format, Object... args) {
        sendMessage(BroadcastType.INFO, format, args);
    }

    @Builder(builderClassName = "MessageBuilder", builderMethodName = "createMessage", buildMethodName = "send")
    public void sendMessage(Colorizer colorizer, @Doc("{} styled format string") String format, Object... args) {
        var prefix = text("")
                .append(text("[", colorizer.getDecorationColor()))
                .append(text(wrapper().getName(), colorizer.getPrimaryColor()))
                .append(text("] ", colorizer.getDecorationColor()));
        var text  = text();
        var split = format.split("\\{}");
        for (int i = 0; i < split.length; i++) {
            text.append(text(split[i], colorizer.getSecondaryColor()));
            if (args.length > i)
                text.append(text(String.valueOf(args[i]), colorizer.getAccentColor()));
        }
        final var msg = prefix.append(text);
        wrapper().getTargets()
                .map(DbObject::getId)
                .forEach(p -> wrapper().getLib()
                        .getPlayerAdapter()
                        .send(p, msg));
    }

    protected abstract BroadcastWrapper wrapper();
}
