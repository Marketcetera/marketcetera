package org.marketcetera.photon.ui.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

public class StringRequiredValidator
    implements IValidator, Messages
{

	private IStatus errorStatus;

	public StringRequiredValidator(){
		errorStatus = new Status(Status.ERROR,
		                         PhotonPlugin.ID,
		                         Status.OK,
		                         VALUE_REQUIRED.getText(),
		                         null);
	}
	
	public IStatus validate(Object value) {
		if (value == null){
			return errorStatus;
		}
		if (value instanceof String){
			String strValue = ((String)value);
			if (strValue.trim().length() ==0){
				return errorStatus;
			} else {
				return Status.OK_STATUS;
			}
		} else {
			throw new IllegalArgumentException(ARGUMENT_MUST_BE_STRING.getText());
		}
	}

}
