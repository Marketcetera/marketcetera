package com.marketcetera.colin.testbench.elements.ui;

import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;

public class ProductsViewElement extends BakeryCrudViewElement {

	public TextFieldElement getProductName() {
		return getEditor().$(FormLayoutElement.class).first().$(TextFieldElement.class).first();
	}

	public TextFieldElement getPrice() {
		return getEditor().$(FormLayoutElement.class).first().$(TextFieldElement.class).all().get(1);
	}

	@Override
	public void openRowForEditing(int row) {
		openRowForEditing(row, 2);
	}
}
