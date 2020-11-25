package io.vertx.workshop.dashboard.views.dashboard;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import io.vertx.workshop.dashboard.OperationService;
import io.vertx.workshop.dashboard.PortfolioServiceFacade;
import io.vertx.workshop.dashboard.VertxHelper;
import io.vertx.workshop.dashboard.model.Operation;
import io.vertx.workshop.dashboard.model.Portfolio;
import io.vertx.workshop.dashboard.views.components.Box;
import io.vertx.workshop.dashboard.views.main.MainView;

@Route(value = "dashboard", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport("./styles/views/dashboard/dashboard-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class DashboardView extends Div {

    private final Grid<Operation> lastOperations = new Grid<>();
    private final PortfolioComponent portfolio = new PortfolioComponent();
    private final ChartComponent chartComponent = new ChartComponent();

    public DashboardView() {
        setId("dashboard-view");
        setSizeFull();
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);
        mainLayout.setSizeFull();
        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setPadding(false);
        leftLayout.setSizeFull();
        leftLayout.addAndExpand(new Box("Portfolio", portfolio), chartComponent);

        mainLayout.addAndExpand(leftLayout, lastOperations());
        add(mainLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();

        updateLastOperations(ui);
        updatePortfolio(ui);

        VertxHelper vertxHelper = VertxHelper.get(ui);
        long timerId = vertxHelper.vertx().setPeriodic(5000, ev -> {
            updatePortfolio(ui);
            updateLastOperations(ui);
            updateChart(ui);
        });
        addDetachListener(ev -> {
            VertxHelper.get(ev.getUI()).cancelTimer(timerId);
            ev.unregisterListener();
        });
    }

    private void updateChart(UI ui) {
        ui.access(chartComponent::updateChart);
    }

    private void updateLastOperations(UI ui) {
        VertxHelper.service(ui, OperationService.class)
            .ifPresent(svc ->
                svc.lastOperations()
                    .onFailure(err -> ui.access(() -> Notification.show("Error while retrieving last operations: " + err.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR)))
                    .onSuccess(ops -> ui.access(() -> lastOperations.setItems(ops))));
    }

    private void updatePortfolio(UI ui) {
        VertxHelper.service(ui, PortfolioServiceFacade.class)
            .ifPresent(svc ->
                svc.getPortfolio()
                    .onFailure(err -> ui.access(() -> Notification.show("Error while retrieving the portfolio: " + err.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR)))
                    .onSuccess(json -> ui.access(() -> portfolio.update(new Portfolio(json)))));
    }

    private Component lastOperations() {
        lastOperations.setPageSize(10);
        lastOperations.setSizeFull();
        lastOperations.addColumn(Operation::getAmount).setHeader("Amount");
        lastOperations.addColumn(Operation::getAction).setHeader("Action");
        lastOperations.addColumn(Operation::getCompany).setHeader("Company");

        return new Box("Last Operations", lastOperations);
    }


}
