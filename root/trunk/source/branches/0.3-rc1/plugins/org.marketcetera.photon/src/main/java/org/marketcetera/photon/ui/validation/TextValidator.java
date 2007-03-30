package org.marketcetera.photon.ui.validation;

import org.eclipse.swt.widgets.Text;

public class TextValidator implements IControlValidator{

	boolean nullAllowed;
	private final Text field;
	private final String fieldName;

	public TextValidator(Text field, String fieldName, boolean nullAllowed) {
		super();
		this.field = field;
		this.fieldName = fieldName;
		this.nullAllowed = nullAllowed;
	}

	public String validate() throws ValidationException {
		if (nullAllowed){
			return null;
		} else {
			String fieldText = field.getText();
			if (fieldText==null || "".equals(fieldText)){
				throw new ValidationException(fieldName + " must not be null",field);
			} else {
				return null;
			}
		}
	}

	public String validateEditing() throws ValidationException {
		return null;
	}
}
