package org.marketcetera.photon.ui.validation;

import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.parser.PriceImage;

public class PriceTextValidator extends NumericTextValidator {

	public PriceTextValidator(Text field, String fieldName, boolean negativeAllowed, boolean nullAllowed) {
		super(field, fieldName, true, negativeAllowed, false, nullAllowed);
	}

	@Override
	public String validate() throws ValidationException {
		if (checkMKT()){
			return null;
		} else {
			return super.validate();
		} 
	}

	@Override
	public String validateEditing() throws ValidationException {
		if (checkMKT()){
			return null;
		} else {
			return super.validateEditing();
		} 
	}

	private boolean checkMKT() {
		boolean done = false;
		String mktImage = PriceImage.MKT.toString();
		String fieldText = field.getText();
		if (mktImage.equals(fieldText)){
			done = true;
		}
		if (mktImage.equalsIgnoreCase(fieldText)){
			field.setText(mktImage);
			done = true;
		}
		return done;
	}


	
	
}
