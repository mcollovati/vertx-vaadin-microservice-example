package io.vertx.workshop.dashboard.model;

import java.util.Collections;
import java.util.Set;

import io.vertx.core.json.JsonObject;

public class Portfolio extends JsonSupport {


    public Portfolio(JsonObject json) {
        super(json);
    }

    public double getCash() {
        return json.getDouble("cash", 0.0);
    }

    public double getValue() {
        return json.getDouble("value", 0.0);
    }

    public double getTotalValue() {
        return json.getDouble("totalValue", 0.0);
    }

    public Set<String> getCompanies() {
        if (json.containsKey("shares")) {
            return json.getJsonObject("shares").fieldNames();
        }
        return Collections.emptySet();
    }

    public int sharesFor(String company) {
        if (json.containsKey("shares")) {
            return json.getJsonObject("shares").getInteger(company, 0);
        }
        return 0;
    }

}
