package io.vertx.workshop.dashboard.model;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public class Operation extends JsonSupport {

    public Operation(JsonObject json) {
        super(json);
    }

    public String getAction() {
        return json.getString("action");
    }

    public int getAmount() {
        return json.getInteger("amount");
    }

    public String getCompany() {
        return json.getJsonObject("quote").getString("name");
    }
}
