package org.marketcetera.photon.ui.validation;

import java.math.BigDecimal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.PhotonPlugin;

public class DecimalRequiredValidator extends StringRequiredValidator {

	
	private IStatus errorStatus;

	public DecimalRequiredValidator(){
		errorStatus = new Status(Status.ERROR, PhotonPlugin.ID, Status.OK, "Decimal number required", null);
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
				new BigDecimal((String)value);
			} catch (Throwable t){
				return errorStatus;
			}
			return Status.OK_STATUS;
		} else {
			throw new IllegalArgumentException("Argument must be String");
		}
	}

}
