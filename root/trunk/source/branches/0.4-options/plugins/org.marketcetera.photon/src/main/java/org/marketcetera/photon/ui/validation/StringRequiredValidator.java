package org.marketcetera.photon.ui.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.PhotonPlugin;

public class StringRequiredValidator extends AbstractToggledValidator {

	private IStatus errorStatus;

	public StringRequiredValidator(){
		errorStatus = new Status(Status.ERROR, PhotonPlugin.ID, Status.OK, "Value required", null);
	}
	
	public IStatus validate(Object value) {
		if (!isEnabled()) {
			return Status.OK_STATUS;
		}
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
			throw new IllegalArgumentException("Argument must be String");
		}
	}

}
