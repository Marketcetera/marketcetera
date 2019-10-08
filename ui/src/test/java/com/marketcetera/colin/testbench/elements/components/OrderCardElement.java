package com.marketcetera.colin.testbench.elements.components;

import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("order-card")
public class OrderCardElement extends DivElement {

	public String getGoodsCount(int index) {
		SpanElement count = $(DivElement.class).attributeContains("class", "goods-item").get(index)
				.$(SpanElement.class).first();
		return count.getText();
	}

	@Override
	public void click() {
		$(DivElement.class).attributeContains("class", "wrapper").first().click();
	}
}
