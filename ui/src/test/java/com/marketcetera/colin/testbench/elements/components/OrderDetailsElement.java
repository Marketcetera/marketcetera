package com.marketcetera.colin.testbench.elements.components;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("order-details")
public class OrderDetailsElement extends TestBenchElement {

	public ButtonElement getCancelButton() {
		return $(ButtonElement.class).id("cancel");
	}

	public ButtonElement getEditButton() {
		return $(ButtonElement.class).id("edit");
	}

	public ButtonElement getSaveButton() {
		return $(ButtonElement.class).id("save");
	}

}
