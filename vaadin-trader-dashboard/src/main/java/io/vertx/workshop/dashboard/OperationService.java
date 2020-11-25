package io.vertx.workshop.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.workshop.dashboard.model.Operation;

public class OperationService {

    private final Supplier<Future<WebClient>> clientSupplier;

    public OperationService(Supplier<Future<WebClient>> clientSupplier) {
        this.clientSupplier = clientSupplier;
    }

    public Future<List<Operation>> lastOperations() {
        return clientSupplier.get().compose(client -> {
            Promise<List<Operation>> promise = Promise.promise();
            client.get("/").send(ar -> {
                if (ar.succeeded()) {
                    JsonArray objects = ar.result().bodyAsJsonArray();
                    List<Operation> ops = new ArrayList<>(objects.size());
                    for (int i = 0; i < objects.size(); i++) {
                        ops.add(new Operation(objects.getJsonObject(i)));
                    }
                    promise.complete(ops);
                } else {
                    promise.fail(ar.cause());
                }
            });
            return promise.future();
        });
    }

}
