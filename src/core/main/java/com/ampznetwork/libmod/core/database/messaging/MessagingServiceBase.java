package com.ampznetwork.libmod.core.database.messaging;

import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.messaging.MessagingService;
import com.ampznetwork.libmod.api.messaging.NotifyEvent;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.comroid.api.func.util.AlmostComplete;
import org.comroid.api.func.util.Debug;
import org.comroid.api.func.util.Event;
import org.comroid.api.tree.Component;

import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Value
@Slf4j
@NonFinal
public abstract class MessagingServiceBase<Entities extends IEntityService> extends Component.Base implements MessagingService {
    protected Entities entities;
    Event.Bus<DbObject<?>> syncEventBus = new Event.Bus<>();
    protected @NonFinal BigInteger ident;

    public MessagingServiceBase(Entities entities, Duration interval) {
        this.entities = entities;

        entities.getScheduler()
                .scheduleWithFixedDelay(() -> {
                            try {
                                dispatch(pollNotifier());
                            } catch (Throwable t) {
                                Debug.log(entities.getBanMod().log(), "An error occurred during event dispatch", t);
                            }
                        },
                        interval.toMillis(),
                        interval.toMillis(),
                        TimeUnit.MILLISECONDS);
    }

    protected abstract void push(NotifyEvent event);

    protected abstract NotifyEvent[] pollNotifier();

    @Override
    public final AlmostComplete<NotifyEvent.Builder> push() {
        return new AlmostComplete<>(NotifyEvent::builder, builder -> {
            builder.id(ident);
            var event = builder.build();
            var relatedType = event.getRelatedType();
            var eventType   = event.getType();
            if (relatedType != null && !eventType.test(relatedType))
                throw new IllegalArgumentException("%s event does not allow %s payloads".formatted(eventType, relatedType));
            var relatedId = event.getRelatedId();
            syncEventBus.accept(entities.getAccessor(relatedType).get(relatedId).orElse(null), SYNC_INBOUND);
            push(event);
        });
    }

    private void dispatch(NotifyEvent... events) {
        if (events.length == 0) return;
        if (events.length > 1)
            for (var event : events)
                dispatch(event);
        var event = events[0];

        // nothing to do for HELLO
        var eventType = event.getType();
        if (eventType == NotifyEvent.Type.HELLO)
            return;

        // validate event
        var relatedType = event.getRelatedType();
        var relatedId = event.getRelatedId();
        if (relatedId == null || relatedType == null) {
            log.error("Invalid event received; data was null\n" + event);
            return;
        }
        if (!eventType.test(relatedType)) {
            log.error("Invalid packet received; %s event type does not allow %s payloads; ignoring it"
                    .formatted(eventType, relatedType));
            return;
        }

        // handle SYNC
        entities.refresh(relatedType, relatedId);
        syncEventBus.accept(entities.getAccessor(relatedType).get(relatedId).orElse(null), SYNC_INBOUND);
    }
}
