package io.vertx.workshop.portfolio.groovy;
public class PortfolioService_GroovyExtension {
  public static void getPortfolio(io.vertx.workshop.portfolio.PortfolioService j_receiver, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    j_receiver.getPortfolio(resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.workshop.portfolio.Portfolio>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.workshop.portfolio.Portfolio> ar) {
        resultHandler.handle(ar.map(event -> event != null ? io.vertx.core.impl.ConversionHelper.fromJsonObject(event.toJson()) : null));
      }
    } : null);
  }
  public static void buy(io.vertx.workshop.portfolio.PortfolioService j_receiver, int amount, java.util.Map<String, Object> quote, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    j_receiver.buy(amount,
      quote != null ? io.vertx.core.impl.ConversionHelper.toJsonObject(quote) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.workshop.portfolio.Portfolio>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.workshop.portfolio.Portfolio> ar) {
        resultHandler.handle(ar.map(event -> event != null ? io.vertx.core.impl.ConversionHelper.fromJsonObject(event.toJson()) : null));
      }
    } : null);
  }
  public static void sell(io.vertx.workshop.portfolio.PortfolioService j_receiver, int amount, java.util.Map<String, Object> quote, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> resultHandler) {
    j_receiver.sell(amount,
      quote != null ? io.vertx.core.impl.ConversionHelper.toJsonObject(quote) : null,
      resultHandler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.workshop.portfolio.Portfolio>>() {
      public void handle(io.vertx.core.AsyncResult<io.vertx.workshop.portfolio.Portfolio> ar) {
        resultHandler.handle(ar.map(event -> event != null ? io.vertx.core.impl.ConversionHelper.fromJsonObject(event.toJson()) : null));
      }
    } : null);
  }
}
