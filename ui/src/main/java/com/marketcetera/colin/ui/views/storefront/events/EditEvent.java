package com.marketcetera.colin.ui.views.storefront.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class EditEvent extends ComponentEvent<Component> {

	public EditEvent(Component source) {
		super(source, false);
	}
}
