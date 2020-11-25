package io.vertx.workshop.dashboard.views.dashboard;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import io.vertx.workshop.dashboard.model.Portfolio;

public class PortfolioComponent extends Composite<FormLayout> {
    private final TextField cash = new TextField();
    private final TextField value = new TextField();
    private final TextField totalValue = new TextField();
    private final Map<String, FormLayout.FormItem> shares = new TreeMap<>();
    private final Binder<Portfolio> binder = new Binder<>();

    public PortfolioComponent() {
        getContent().setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1)
        );
        getContent().addFormItem(cash, "Cash");
        getContent().addFormItem(value, "Value");
        getContent().addFormItem(totalValue, "Total Value");

        binder.forField(cash)
            .withConverter(new StringToDoubleConverter("Invalid cash amount"))
            .bind(Portfolio::getCash, null);
        binder.forField(value)
            .withConverter(new StringToDoubleConverter("Invalid value amount"))
            .bind(Portfolio::getValue, null);
        binder.forField(totalValue)
            .withConverter(new StringToDoubleConverter("Invalid total value amount"))
            .bind(Portfolio::getTotalValue, null);
    }

    public void update(Portfolio portfolio) {
        updateShares(portfolio);
        binder.setBean(portfolio);
    }

    private void updateShares(Portfolio portfolio) {
        Set<String> companies = portfolio.getCompanies();
        companies.forEach(company -> shares.computeIfAbsent(company, k -> {
            TextField field = new TextField();
            binder.forField(field)
                .withConverter(new StringToIntegerConverter("Invalid share value for " + k))
                .bind(p -> p.sharesFor(k), null);
            return getContent().addFormItem(field, company + " Shares");
        }));
    }
}