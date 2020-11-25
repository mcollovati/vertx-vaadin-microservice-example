package io.vertx.workshop.dashboard.model;

import java.util.Map;

import io.vertx.core.json.JsonObject;

public class Service extends JsonSupport {

    public Service(JsonObject json) {
        super(json);
    }

    public String getName() {
        return json.getString("name");
    }

    public Status getStatus() {
        return Status.valueOf(json.getString("status", Status.OUT_OF_SERVICE.name()));
    }

    public String getType() {
        return json.getString("type");
    }

    public String getRegistration() {
        return json.getString("registration");
    }

    public Map<String, Object> getLocation() {
        return json.getJsonObject("location").getMap();
    }

    public Map<String, Object> getMetadata() {
        return json.getJsonObject("metadata").getMap();
    }

    public enum Status {
        UP, DOWN, OUT_OF_SERVICE
    }
}
