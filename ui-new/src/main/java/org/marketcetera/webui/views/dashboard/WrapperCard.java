package org.marketcetera.webui.views.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public class WrapperCard extends Div {

    public WrapperCard(String className, Component[] components, String... classes) {
        addClassName(className);

        Div card = new Div();
        card.addClassNames(classes);
        card.add(components);

        add(card);
    }

}
