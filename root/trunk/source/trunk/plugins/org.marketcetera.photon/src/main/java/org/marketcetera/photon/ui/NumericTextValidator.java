package org.marketcetera.photon.ui;

import java.math.BigDecimal;
import java.math.MathContext;

import org.eclipse.swt.widgets.Text;

public class NumericTextValidator implements IControlValidator {

	private final boolean decimalAllowed;
	private final boolean negativeAllowed;
	private final boolean scientificAllowed;
	protected final Text field;
	private final String fieldName;
	private final boolean nullAllowed;

	public NumericTextValidator(Text field, String fieldName, boolean decimalAllowed,
			boolean negativeAllowed, boolean scientificAllowed, boolean nullAllowed) {
				this.field = field;
				this.fieldName = fieldName;
				this.decimalAllowed = decimalAllowed;
				this.negativeAllowed = negativeAllowed;
				this.scientificAllowed = scientificAllowed;
				this.nullAllowed = nullAllowed;
				field.setData(VALIDATOR_KEY, this);
	}

	public String validateEditing() throws ValidationException {
		String text = field.getText();
		String errorString = validateString(text, decimalAllowed, negativeAllowed, scientificAllowed,
				true);
		if (errorString == null){
			return null;
		} else {
			throw new ValidationException(fieldName+" "+errorString, field);
		}
	}

	public String validate() throws ValidationException {
		String text = field.getText();
		String errorString = validateString(text, decimalAllowed, negativeAllowed, scientificAllowed,
				nullAllowed);
		if (errorString == null){
			return null;
		} else {
			throw new ValidationException(fieldName+" "+errorString, field);
		}
	}

	public static String validateString(String text, boolean pDecimalAllowed,
			boolean pNegativeAllowed, boolean pScientificAllowed, boolean pNullAllowed) throws ValidationException {
		if ((text == null || "".equals(text)))
		{
			if (pNullAllowed){
				return null;
			} else {
				return "must not be null.";
			}
		}
		try {
			boolean isScientific = false;
			boolean isNegative;
			boolean isDecimal = false;
			BigDecimal dec;
			dec = new BigDecimal(text, MathContext.DECIMAL32);
			
			if (text.contains("e")||text.contains("E")){
				isScientific = true;
			}
			if (text.contains(".")){
				isDecimal = true;
			}
			isNegative = (dec.compareTo(BigDecimal.ZERO) < 0);
			
			if (isDecimal && !pDecimalAllowed){
				return "must be an integer.";
			}
			if (isNegative && !pNegativeAllowed) {
				return "must not be negative.";
			}
			if (isScientific && !pScientificAllowed){
				return "must be a number in standard notation.";
			}
			return null;
		} catch (NumberFormatException ex) {
			return "must be numeric"; 
		}
	}

	public boolean isDecimalAllowed() {
		return decimalAllowed;
	}

	public boolean isNegativeAllowed() {
		return negativeAllowed;
	}

	public boolean isNullAllowed() {
		return nullAllowed;
	}

	public boolean isScientificAllowed() {
		return scientificAllowed;
	}


	
}
