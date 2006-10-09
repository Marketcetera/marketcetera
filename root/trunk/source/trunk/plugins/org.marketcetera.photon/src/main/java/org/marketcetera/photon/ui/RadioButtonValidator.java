package org.marketcetera.photon.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class RadioButtonValidator implements IControlValidator {

	private List<Button> buttons = new LinkedList<Button>();
	private final boolean required;
	private final String fieldName;

	/**
	 * Create a new {@link RadioButtonValidator}, specifying
	 * whether this field is required.  If the required
	 * parameter is set, validate will cause an error
	 * when no selection is made.
	 * 
	 * @param required true if one of the radio buttons must be selected
	 */
	public RadioButtonValidator(boolean required, String fieldName)
	{
		this.required = required;
		this.fieldName = fieldName;
	}
	
	public void addButton(Button radioButton){
		if ((radioButton.getStyle() | SWT.RADIO)!=0){
			throw new IllegalArgumentException("radioButton must be of type SWT.RADIO");
		}
		buttons.add(radioButton);
		radioButton.setData(VALIDATOR_KEY, this);
	}
	
	public String validateEditing() throws ValidationException {
		// Always ok
		return null;
	}

	public String validate() throws ValidationException {

		if (!required){
			return null;
		}

		for(Button button: buttons) {
			if (button.getSelection())
				return null;
		}

		String message = fieldName + " required";
		throw new ValidationException(message, null);
	}

}
