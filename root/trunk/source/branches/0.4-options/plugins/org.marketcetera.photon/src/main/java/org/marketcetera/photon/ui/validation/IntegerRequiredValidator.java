package org.marketcetera.photon.ui.validation;

import java.math.BigInteger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.validation.StringRequiredValidator;

public class IntegerRequiredValidator extends StringRequiredValidator {

	private IStatus errorStatus;

	public IntegerRequiredValidator(){
		errorStatus = new Status(Status.ERROR, PhotonPlugin.ID, Status.OK, "Integer required", null);
	}

	@Override
	public IStatus validate(Object value) {
		if (!isEnabled()) {
			return Status.OK_STATUS;
		}
		if (value == null){
			return errorStatus;
		}
		if (value instanceof String){
			try {
				new BigInteger((String)value);
			} catch (Throwable t){
				return errorStatus;
			}
			return Status.OK_STATUS;
		} else {
			throw new IllegalArgumentException("Argument must be String");
		}
	}

}
