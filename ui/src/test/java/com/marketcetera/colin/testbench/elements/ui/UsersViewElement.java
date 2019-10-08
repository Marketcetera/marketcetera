package com.marketcetera.colin.testbench.elements.ui;

import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.textfield.testbench.EmailFieldElement;
import com.vaadin.flow.component.textfield.testbench.PasswordFieldElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;

public class UsersViewElement extends BakeryCrudViewElement {

	public EmailFieldElement getEmailField() {
		return getForm().$(EmailFieldElement.class).first();
	}

	public TextFieldElement getFirstName() {
		return getForm().$(TextFieldElement.class).first();
	}

	public TextFieldElement getLastName() {
		return getForm().$(TextFieldElement.class).last();
	}

	public PasswordFieldElement getPasswordField() {
		return getForm().$(PasswordFieldElement.class).first();
	}

	public ComboBoxElement getRole() {
		return getForm().$(ComboBoxElement.class).first();
	}

	@Override
	public void openRowForEditing(int row) {
		openRowForEditing(row, 3);
	}

}
