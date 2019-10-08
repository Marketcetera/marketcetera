package com.marketcetera.colin.testbench.elements.ui;

import java.util.Optional;

import com.vaadin.flow.component.confirmdialog.testbench.ConfirmDialogElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.HasElementQuery;
import com.vaadin.testbench.TestBenchElement;

public interface HasCrudView extends HasElementQuery {

	<T extends TestBenchElement> Class<T> getFormClass();

	default GridElement getGrid() {
		return $(GridElement.class).first();
	}

	default Optional<ConfirmDialogElement> getConfirmDialog() {
		ElementQuery<ConfirmDialogElement> query = $(ConfirmDialogElement.class).onPage();
		return query.exists() ? Optional.of(query.first()) : Optional.empty();
	}

	default Optional<DialogElement> getDialog() {
		ElementQuery<DialogElement> query = $(DialogElement.class).onPage();
		return query.exists() ? Optional.of(query.first()) : Optional.empty();
	}

	default FormLayoutElement getForm() {
		return getDialog().get().$(getFormClass()).first().$(FormLayoutElement.class).waitForFirst();
	}

}
