package com.marketcetera.colin.testbench.elements.ui;

import com.vaadin.flow.component.confirmdialog.testbench.ConfirmDialogElement;
import com.vaadin.flow.component.crud.testbench.CrudElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.marketcetera.colin.testbench.elements.components.SearchBarElement;
import com.vaadin.testbench.TestBenchElement;

public class BakeryCrudViewElement extends CrudElement implements HasApp {

	public SearchBarElement getSearchBar() {
		return $(SearchBarElement.class).first();
	}

	public FormLayoutElement getForm() {
		return getEditor().$(FormLayoutElement.class).first();
	}

	public ConfirmDialogElement getDiscardConfirmDialog() {
		return $(ConfirmDialogElement.class).first();
	}

	public ConfirmDialogElement getDeleteConfirmDialog() {
		return $(ConfirmDialogElement.class).last();
	}

	public void openRowForEditing(int row, int editCol) {
		getGrid().getCell(row, editCol).$(TestBenchElement.class).first().click();
	}
}
