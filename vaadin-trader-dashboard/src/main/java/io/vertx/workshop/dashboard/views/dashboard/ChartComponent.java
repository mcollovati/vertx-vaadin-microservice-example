package io.vertx.workshop.dashboard.views.dashboard;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.AxisType;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.Hover;
import com.vaadin.flow.component.charts.model.Labels;
import com.vaadin.flow.component.charts.model.Marker;
import com.vaadin.flow.component.charts.model.MarkerSymbolEnum;
import com.vaadin.flow.component.charts.model.PlotOptionsArea;
import com.vaadin.flow.component.charts.model.Series;
import com.vaadin.flow.component.charts.model.States;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.workshop.dashboard.VertxHelper;

public class ChartComponent extends Composite<Chart> {

    private final AtomicInteger xAxisCounter = new AtomicInteger();
    private final Map<String, Quote> quotes = new ConcurrentHashMap<>();

    public ChartComponent() {
        setupChart(getContent());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        Vertx vertx = VertxHelper.get(ui).vertx();

        VertxHelper.consumeFromEventBus(ui, "market", this::onQuoteReceived)
            .onSuccess(f -> addDetachListener(ev -> {
                f.apply(ev.getUI()).remove();
                ev.unregisterListener();
            }));
    }

    void updateChart() {
        Configuration configuration = getContent().getConfiguration();
        List<Series> series = configuration.getSeries();
        Map<String, DataSeries> seriesByName = series.stream()
            .map(DataSeries.class::cast)
            .collect(Collectors.toMap(Series::getName, Function.identity()));

        int currentCounter = xAxisCounter.incrementAndGet();
        quotes.forEach((company, quote) -> {
            DataSeries currentDs = seriesByName.computeIfAbsent(company, n -> {
                DataSeries ds = new DataSeries(n);
                configuration.addSeries(ds);
                return ds;
            });
            currentDs.add(new DataSeriesItem(currentCounter, quote.getValue()), true, currentDs.size() > 10);
        });
    }

    public void onQuoteReceived(JsonObject json) {
        boolean wasEmpty = quotes.isEmpty();
        int price = json.getInteger("bid");
        String name = json.getString("name");
        quotes.computeIfAbsent(name, Quote::new).setValue(price);
        if (wasEmpty) {
            updateChart();
        }
    }

    private void setupChart(Chart chart) {
        chart.setSizeFull();

        final Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.AREASPLINE);
        configuration.getTitle().setText("");
        configuration.getLegend().setEnabled(false);

        PlotOptionsArea plotOptions = new PlotOptionsArea();
        plotOptions.setPointStart(1);
        Marker marker = new Marker();
        marker.setEnabled(false);
        marker.setSymbol(MarkerSymbolEnum.CIRCLE);
        marker.setRadius(2);
        States states = new States();
        states.setHover(new Hover(true));
        marker.setStates(states);
        plotOptions.setMarker(marker);
        configuration.setPlotOptions(plotOptions);

        XAxis xAxis = new XAxis();
        Labels labels = new Labels();
        labels.setFormatter("this.value");
        xAxis.setLabels(labels);
        xAxis.setAllowDecimals(false);
        xAxis.setType(AxisType.CATEGORY);
        configuration.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle("");
        labels = new Labels();
        labels.setFormatter("this.value");
        yAxis.setLabels(labels);
        configuration.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        configuration.setTooltip(tooltip);

        /*
        dataProvider = new ListDataProvider<>(quotes.values());
        DataProviderSeries<Quote> series = new DataProviderSeries<>(dataProvider, Quote::getValue);
        SerializationHacks.install(series);
        configuration.addSeries(series);
         */

        /*
        List<DataSeriesItem> initialData = IntStream.rangeClosed(1, 10)
            .mapToObj(i -> new DataSeriesItem(i, 0))
            .collect(Collectors.toList());
        xAxisCounter = new AtomicInteger(10);


        List<Series> series = quotes.keySet().stream()
            .map(Company::toString)
            .map(DataSeries::new).collect(Collectors.toList());
        series.stream().map(DataSeries.class::cast)
            .forEach(s -> s.setData(new ArrayList<>(initialData)));

         */
        //configuration.setSeries(series);
        //chart.drawChart();
    }


    private static final class Quote implements Serializable {
        private final String name;
        private AtomicInteger value = new AtomicInteger();

        public Quote(String name) {
            this.name = name;
        }

        public void setValue(int newValue) {
            value.set(newValue);
        }

        int getValue() {
            return value.get();
        }
    }
}
