package com.marketcetera.colin.testbench.elements.components;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("search-bar")
public class SearchBarElement extends TestBenchElement {

    public ButtonElement getCreateNewButton() {
        return $(ButtonElement.class).id("action");
    }
}
