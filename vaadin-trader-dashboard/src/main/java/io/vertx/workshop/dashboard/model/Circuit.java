package io.vertx.workshop.dashboard.model;

import io.vertx.core.json.JsonObject;

public class Circuit extends JsonSupport {

    public Circuit(JsonObject json) {
        super(json);
    }

    public String getName() {
        return json.getString("");
    }

    public Status getStatus() {
        return Status.valueOf(json.getString("status", Status.OPEN.name()));
    }

    public String getFailures() {
        return json.getString("failures");
    }

    public enum Status {
        CLOSED, OPEN, HALF_OPEN
    }
}
