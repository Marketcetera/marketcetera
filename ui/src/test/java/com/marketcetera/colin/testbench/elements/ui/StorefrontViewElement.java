package com.marketcetera.colin.testbench.elements.ui;

import java.util.Optional;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.marketcetera.colin.testbench.elements.components.OrderCardElement;
import com.marketcetera.colin.testbench.elements.components.OrderDetailsElement;
import com.marketcetera.colin.testbench.elements.components.SearchBarElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("storefront-view")
public class StorefrontViewElement extends TestBenchElement implements HasApp, HasCrudView {

	@Element("order-editor")
	public static class OrderEditorElement extends TestBenchElement {
		public OrderItemEditorElement getOrderItemEditor(int index) {
			return $(OrderItemEditorElement.class).get(index);
		}

		public void review() {
			$(ButtonElement.class).id("review").click();
		}

		public TextFieldElement getCustomerNameField() {
			return $(TextFieldElement.class).id("customerName");
		}

		public TextFieldElement getCustomerDetailsField() {
			return $(TextFieldElement.class).id("customerDetails");
		}

		public void cancel() {
			$(ButtonElement.class).id("cancel").click();
		}
	}

	@Override
	public GridElement getGrid() {
		return $(GridElement.class).waitForFirst();
	}

	public OrderCardElement getOrderCard(int index) {
		// First visible grid cell has has 4th id
		return getGrid().$(OrderCardElement.class).get(index + 4);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<OrderEditorElement> getFormClass() {
		return OrderEditorElement.class;
	}

	public OrderEditorElement getOrderEditor() {
		return getDialog().get().$(OrderEditorElement.class).first();
	}

	public OrderDetailsElement getOrderDetails() {
		return getDialog().get().$(OrderDetailsElement.class).first();
	}

	public SearchBarElement getSearchBar() {
		return $(SearchBarElement.class).first();
	}

	@Override
	public Optional<DialogElement> getDialog() {
		return Optional.of($(DialogElement.class).waitForFirst());
	}
}
