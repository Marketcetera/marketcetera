package org.marketcetera.photon.ui;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.custom.CCombo;

public class CComboValidator implements IControlValidator {

	private final List<String> possibilities;
	private final CCombo combo;
	private final String fieldName;
	private final boolean nullAllowed;

	public CComboValidator(CCombo cCombo, String fieldName, List<String> name,
			boolean nullAllowed) {
		combo = cCombo;
		this.fieldName = fieldName;
		this.possibilities = name;
		this.nullAllowed = nullAllowed;
		cCombo.setData(VALIDATOR_KEY, this);
	}
	public String validate() throws ValidationException {
		normalize();
		String errorString = validateString(combo.getText(), fieldName, possibilities, nullAllowed);
		if (errorString == null){
			return null;
		} else {
			throw new ValidationException(fieldName+" "+errorString, combo);
		}
	}
	public String validateEditing() throws ValidationException {
		normalize();
		String errorString = validateString(combo.getText(), fieldName, possibilities, true);
		if (errorString == null){
			return null;
		} else {
			throw new ValidationException(fieldName+" "+errorString, combo);
		}
	}

	public static String validateString(String toValidate, String fieldName, List<String> pPossibilities,
			boolean nullAllowed) throws ValidationException
	{
		if (toValidate == null || "".equals(toValidate)){
			if (nullAllowed){
				return null;
			} else {
				return "must not be null";
			}
		}
		if (pPossibilities.contains(toValidate)){
			return null;
		}
		return "must be one of "+pPossibilities;
	}

	private void normalize() {
		String fieldText = combo.getText();
		for (String aPossibility : possibilities) {
			if (!aPossibility.equals(fieldText) && aPossibility.equalsIgnoreCase(fieldText)){
				combo.setText(aPossibility);
			}
		}
	}
}
