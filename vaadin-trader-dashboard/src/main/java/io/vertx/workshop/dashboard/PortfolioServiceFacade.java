package io.vertx.workshop.dashboard;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.workshop.portfolio.Portfolio;
import io.vertx.workshop.portfolio.PortfolioService;

public class PortfolioServiceFacade {
    private final PortfolioService service;

    public PortfolioServiceFacade(PortfolioService service) {
        this.service = service;
    }

    public Future<JsonObject> getPortfolio() {
        Promise<Portfolio> portfolioPromise = Promise.promise();
        Promise<Double> evaluatePromise = Promise.promise();
        service.getPortfolio(portfolioPromise);

        return portfolioPromise.future().map(Portfolio::toJson)
            .compose(json -> {
                service.evaluate(evaluatePromise);
                return evaluatePromise.future().map(value -> json.put("value", value)
                    .put("totalValue", json.getDouble("cash", 0.0) + value));
            });
    }
}
