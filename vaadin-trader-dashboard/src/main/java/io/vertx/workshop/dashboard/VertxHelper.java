package io.vertx.workshop.dashboard;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxHelper {

    private static final Logger logger = LoggerFactory.getLogger(VertxHelper .class);

    private final Vertx vertx;
    private final ServiceDiscovery serviceDiscovery;
    private final AtomicLong counter = new AtomicLong();
    private final Map<Long, MessageConsumer<?>> consumers = new ConcurrentHashMap<>();

    VertxHelper(Vertx vertx, ServiceDiscovery serviceDiscovery) {
        this.vertx = vertx;
        this.serviceDiscovery = serviceDiscovery;
    }

    public Vertx vertx() {
        return vertx;
    }

    /**
     * Consumes messages from the given address executing handler code
     * with exclusive access to the UI.
     * <p>
     * It returns a factory that produces a {@link Registration}, given an UI,
     * that, when removed, will stop handler from consuming messages.
     *
     * @param ui      current UI
     * @param address messages source address
     * @param handler function to be invoked for incoming messages.
     * @return a future with a factory to build a {@link Registration}
     * @see UI#access(Command)
     */
    public static Future<SerializableFunction<UI, Registration>> consumeFromMessageSource(UI ui, String address, Consumer<JsonObject> handler) {
        Promise<SerializableFunction<UI, Registration>> promise = Promise.promise();
        VertxHelper helper = get(ui);
        MessageSource.<JsonObject>getConsumer(helper.serviceDiscovery, new JsonObject().put("name", address), ar -> {
            if (ar.succeeded()) {
                MessageConsumer<JsonObject> consumer = ar.result();
                SerializableFunction<UI, Registration> registration = helper.registerConsumer(consumer);
                handlerWithUIAccess(ui, consumer, handler);
                promise.complete(registration);
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    public static Future<SerializableFunction<UI, Registration>> consumeFromEventBus(UI ui, String address, Consumer<JsonObject> handler) {
        VertxHelper helper = get(ui);
        MessageConsumer<JsonObject> consumer = helper.vertx.eventBus().consumer(address);
        handlerWithUIAccess(ui, consumer, handler);
        return Future.succeededFuture(helper.registerConsumer(consumer));
    }

    private static void handlerWithUIAccess(UI ui, MessageConsumer<JsonObject> consumer, Consumer<JsonObject> handler) {
        consumer.handler(ev -> {
            JsonObject json = ev.body();
            ui.access(() -> handler.accept(json));
        });
    }

    public static <T> Optional<T> service(UI ui, Class<T> serviceType) {
        return Optional.ofNullable(ui.getSession().getService().getContext().getAttribute(serviceType));
    }

    private SerializableFunction<UI, Registration> registerConsumer(MessageConsumer<JsonObject> consumer) {
        long consumerId = counter.incrementAndGet();
        consumers.put(consumerId, consumer);
        logger.debug("Registered consumer {}", consumerId);
        return theUi -> () -> VertxHelper.unregister(theUi, consumerId);
    }

    private static void unregister(UI ui, long consumerId) {
        MessageConsumer<?> consumer = VertxHelper.get(ui).consumers.remove(consumerId);
        if (consumer != null) {
            logger.debug("Unregistered consumer {}", consumerId);
            consumer.unregister();
        }
    }

    public void cancelTimer(long... timerId) {
        for (long tid : timerId) {
            vertx.cancelTimer(tid);
            logger.debug("Canceled timer {}", tid);
        }
    }


    public static VertxHelper get(UI ui) {
        return ui.getSession().getService().getContext().getAttribute(VertxHelper.class);
    }
}
