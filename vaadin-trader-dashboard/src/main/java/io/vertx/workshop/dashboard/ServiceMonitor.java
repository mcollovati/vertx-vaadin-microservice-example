package io.vertx.workshop.dashboard;

import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.workshop.dashboard.model.Service;

public class ServiceMonitor {

    private final ServiceDiscovery discovery;

    public ServiceMonitor(ServiceDiscovery discovery) {
        this.discovery = discovery;
    }

    public Future<List<Service>> services() {
        Promise<List<Record>> promise = Promise.promise();
        discovery.getRecords(new JsonObject(), promise);
        return promise.future()
            .map(records -> records.stream().map(rec -> new Service(rec.toJson())).collect(Collectors.toList()));
    }
}
