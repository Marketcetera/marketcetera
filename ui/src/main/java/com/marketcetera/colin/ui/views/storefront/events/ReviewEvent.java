package com.marketcetera.colin.ui.views.storefront.events;

import com.vaadin.flow.component.ComponentEvent;
import com.marketcetera.colin.ui.views.orderedit.OrderEditor;

public class ReviewEvent extends ComponentEvent<OrderEditor> {

	public ReviewEvent(OrderEditor component) {
		super(component, false);
	}
}