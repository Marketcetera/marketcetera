package com.marketcetera.colin.ui.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class ValidationFailedEvent extends ComponentEvent<Component> {

	public ValidationFailedEvent(Component source) {
		super(source, false);
	}

}