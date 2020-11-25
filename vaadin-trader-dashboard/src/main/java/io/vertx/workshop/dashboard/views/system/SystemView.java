package io.vertx.workshop.dashboard.views.system;

import java.util.Map;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import io.vertx.workshop.dashboard.ServiceMonitor;
import io.vertx.workshop.dashboard.VertxHelper;
import io.vertx.workshop.dashboard.model.Service;
import io.vertx.workshop.dashboard.views.components.Box;
import io.vertx.workshop.dashboard.views.main.MainView;

@Route(value = "system", layout = MainView.class)
@PageTitle("System")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport(value = "./styles/views/system/system-view.css", include = "lumo-badge")
public class SystemView extends Composite<HorizontalLayout> {

    private final Grid<Service> servicesGrid;

    public SystemView() {
        setId("system-view");

        servicesGrid = new Grid<>();
        servicesGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        servicesGrid.setHeightFull();
        servicesGrid.addColumn(Service::getName).setHeader("Name");
        servicesGrid.addColumn(new ComponentRenderer<>(SystemView::statusBadge)).setHeader("Status").setFlexGrow(0);
        servicesGrid.addColumn(Service::getType).setHeader("Type");
        servicesGrid.addColumn(Service::getType).setHeader("Registration Id");
        servicesGrid.addColumn(new ComponentRenderer<>(s -> fromMap(s.getLocation())))
            .setHeader("Location").setFlexGrow(2);
        servicesGrid.addColumn(new ComponentRenderer<>(s -> fromMap(s.getMetadata())))
            .setHeader("Metadata").setFlexGrow(2);
        Box availableServices = new Box("Available services", servicesGrid);

        getContent().setSizeFull();
        getContent().setPadding(true);

        getContent().addAndExpand(availableServices);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        updateServices(ui);

        VertxHelper vertxHelper = VertxHelper.get(ui);
        long timerId = vertxHelper.vertx().setPeriodic(5000, ev -> {
            updateServices(ui);
        });
        addDetachListener(ev -> {
            VertxHelper.get(ev.getUI()).cancelTimer(timerId);
            ev.unregisterListener();
        });

    }

    private void updateServices(UI ui) {
        VertxHelper.service(ui, ServiceMonitor.class).ifPresent(svc ->
            svc.services()
                .onFailure(err -> ui.access(() -> Notification.show("Error while retrieving services: " + err.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR)))
                .onSuccess(list -> ui.access(() -> servicesGrid.setItems(list))));
    }

    private static UnorderedList fromMap(Map<String, Object> map) {
        UnorderedList ul = new UnorderedList();
        map.entrySet().stream()
            .map(e -> new ListItem(String.format("%s = %s", e.getKey(), e.getValue())))
            .forEach(ul::add);
        return ul;
    }


    private static Span statusBadge(Service s) {
        Service.Status status = s.getStatus();
        String theme = "badge primary ";
        if (status == Service.Status.UP) {
            theme += "success";
        } else if (status == Service.Status.DOWN) {
            theme += "error";
        } else {
            theme += "contrast";
        }
        Span span = new Span(status.name());
        span.getElement().setAttribute("theme", theme);
        return span;
    }
}
