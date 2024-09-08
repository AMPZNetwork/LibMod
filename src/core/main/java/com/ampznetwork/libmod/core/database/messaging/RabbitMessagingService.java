package com.ampznetwork.libmod.core.database.messaging;

import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.messaging.MessagingService;
import com.ampznetwork.libmod.api.messaging.NotifyEvent;
import lombok.Value;
import org.comroid.api.func.util.Event;
import org.comroid.api.net.Rabbit;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;

@Value
public class RabbitMessagingService extends MessagingServiceBase<IEntityService> implements MessagingService.RabbitMQ {
    @Event.Subscriber(type = NotifyEvent.class)
    Queue<NotifyEvent> incomingQueue = new LinkedList<>();
    Rabbit.Exchange.Route<NotifyEvent> route;

    public RabbitMessagingService(String uri, IEntityService service) {
        super(service, Duration.ofMillis(100));

        var rabbit   = Rabbit.of(uri).orElseThrow();
        var exchange = rabbit.exchange("banmod");
        (this.route = exchange.route("", NotifyEvent.CONVERTER)).register(this);
    }

    @Override
    protected void push(NotifyEvent event) {
        route.send(event);
    }

    @Override
    protected NotifyEvent[] pollNotifier() {
        NotifyEvent[] queue;
        synchronized (incomingQueue) {
            if (incomingQueue.isEmpty())
                return new NotifyEvent[0];
            queue = incomingQueue.toArray(NotifyEvent[]::new);
            incomingQueue.clear();
        }
        return queue;
    }
}
