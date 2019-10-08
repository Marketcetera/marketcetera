package com.marketcetera.colin.ui.components;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("amount-field")
@JsModule("./src/components/amount-field.js")
public class AmountField extends AbstractSinglePropertyField<AmountField, Integer> {

	public AmountField() {
        super("value", null, true);
	}

	public void setEnabled(boolean enabled) {
		getElement().setProperty("disabled", !enabled);
	}

	public void setMin(int value) {
		getElement().setProperty("min", value);
	}

	public void setMax(int value) {
		getElement().setProperty("max", value);
	}

	public void setEditable(boolean editable) {
		getElement().setProperty("editable", editable);
	}

	public void setPattern(String pattern) {
		getElement().setProperty("pattern", pattern);
	}

}
