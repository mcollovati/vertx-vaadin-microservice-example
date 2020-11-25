package io.vertx.workshop.dashboard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.github.mcollovati.vertx.vaadin.VaadinVerticle;
import com.github.mcollovati.vertx.vaadin.VertxVaadinService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.workshop.portfolio.PortfolioService;

/**
 * The entry point of the Spring Boot application.
 */
public class VaadinDashboardVerticle extends VaadinVerticle {


    private final Map<String, WebClient> webClients = new ConcurrentHashMap<>();
    protected ServiceDiscovery discovery;

    @Override
    public void start() {
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));

    }

    @Override
    public void stop() throws Exception {
        webClients.values().forEach(WebClient::close);
        discovery.close();
    }

    @Override
    protected void serviceInitialized(VertxVaadinService service, Router router) {
        // Services
        service.getContext().setAttribute(ServiceMonitor.class, new ServiceMonitor(discovery));

        // Portfolio service
        Promise<PortfolioService> servicePromise = Promise.promise();
        EventBusService.getProxy(discovery, PortfolioService.class, servicePromise);
        servicePromise.future().onSuccess(svc -> service.getContext().setAttribute(PortfolioServiceFacade.class, new PortfolioServiceFacade(svc)));

        // Audit service
        Supplier<Future<WebClient>> webClientSupplier = () -> getOrRegisterHttpClientService("audit");
        service.getContext().setAttribute(OperationService.class, new OperationService(webClientSupplier));

        service.getContext().setAttribute(VertxHelper.class, new VertxHelper(vertx, discovery));
    }

    private Future<WebClient> getOrRegisterHttpClientService(String name) {
        Promise<WebClient> promise = Promise.promise();
        if (webClients.containsKey(name)) {
            promise.complete(webClients.get(name));
        } else {
            HttpEndpoint.getWebClient(discovery, new JsonObject().put("name", name), ar -> {
                if (ar.succeeded()) {
                    webClients.put(name, ar.result());
                    promise.complete(ar.result());
                } else {
                    promise.fail(ar.cause());
                }
            });
        }
        return promise.future();
    }
}
