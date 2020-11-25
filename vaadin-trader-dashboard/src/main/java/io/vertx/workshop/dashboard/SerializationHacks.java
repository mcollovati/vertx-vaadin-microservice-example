package io.vertx.workshop.dashboard;

import java.lang.reflect.Field;

import com.vaadin.flow.component.charts.model.DataProviderSeries;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;

public final class SerializationHacks {

    private SerializationHacks() {

    }


    /**
     * Replaces DataProviderListener lambda with a concrete class
     * to avoid ClassCastExceptions on deserialization.
     */
    public static void install(DataProviderSeries<?> provider) {
        try {
            Field field = DataProviderSeries.class.getDeclaredField("listener");
            field.setAccessible(true);
            field.set(provider, new DataSeriesProviderSerializationWorkaround<>(provider));
            // Hack to attach the new listener to the data provider
            provider.setAutomaticChartUpdateEnabled(false);
            provider.setAutomaticChartUpdateEnabled(true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    // Workaround to avoid serialization issue on DataProviderSeries
    // java.lang.ClassCastException: cannot assign instance of java.lang.invoke.SerializedLambda
    // to field com.vaadin.flow.component.charts.model.DataProviderSeries.listener
    // of type com.vaadin.flow.data.provider.DataProviderListener in instance
    // of com.vaadin.flow.component.charts.model.DataProviderSeries
    private static class DataSeriesProviderSerializationWorkaround<T> implements DataProviderListener<T> {

        private final DataProviderSeries<T> delegate;

        public DataSeriesProviderSerializationWorkaround(DataProviderSeries<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onDataChange(DataChangeEvent<T> event) {
            delegate.updateSeries();
        }

    }

}
