package com.ampznetwork.libmod.api.messaging;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.attr.Named;
import org.comroid.api.func.util.AlmostComplete;
import org.comroid.api.func.util.Event;
import org.comroid.api.text.Capitalization;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface MessagingService {
    String SYNC_INBOUND  = "inbound";
    String SYNC_OUTBOUND = "outbound";

    AlmostComplete<NotifyEvent.Builder> push();

    Event.Bus<DbObject> getSyncEventBus();

    interface Config {
        boolean inheritDatasource();
    }

    interface PollingDatabase extends MessagingService {
        record Config(@Nullable DatabaseInfo dbInfo, Duration interval) implements MessagingService.Config {
            @Override
            public boolean inheritDatasource() {
                return dbInfo == null || dbInfo.type() == null;
            }
        }
    }

    interface RabbitMQ extends MessagingService {
        record Config(String uri) implements MessagingService.Config {
            @Override
            public boolean inheritDatasource() {
                return false;
            }
        }
    }

    @Value
    @NonFinal
    abstract class Type<Config extends MessagingService.Config, Implementation extends MessagingService> implements Named {
        public static final Set<Type<?, ?>> REGISTRY = new HashSet<>();
        String name;

        public abstract @Nullable Implementation createService(LibMod mod, IEntityService entities, Config config);

        public interface Provider {
            String getMessagingServiceTypeName();

            @Nullable MessagingService.Config getMessagingServiceConfig();

            default Optional<Type<?, ?>> getMessagingServiceType() {
                var messagingServiceTypeName = getMessagingServiceTypeName();
                if ("none".equals(messagingServiceTypeName))
                    return Optional.empty();
                return Type.REGISTRY.stream()
                        .filter(type -> Capitalization.equalsIgnoreCase(type.name, messagingServiceTypeName))
                        .findAny();
            }
        }
    }
}
