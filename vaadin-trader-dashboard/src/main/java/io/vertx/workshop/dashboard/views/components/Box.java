package io.vertx.workshop.dashboard.views.components;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Box extends Composite<VerticalLayout> {
    public Box(String caption, Component content) {

        Span captionSpan = new Span(caption);
        captionSpan.addClassName("box-caption");

        getContent().addClassName("box");

        getContent().add(caption);
        getContent().addAndExpand(content);
    }
}
