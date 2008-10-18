package org.marketcetera.photon.ui.validation;

import java.math.BigInteger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

public class IntegerRequiredValidator
    extends StringRequiredValidator
    implements Messages
{

	private IStatus errorStatus;

	public IntegerRequiredValidator(){
		errorStatus = new Status(Status.ERROR,
		                         PhotonPlugin.ID,
		                         Status.OK,
		                         INTEGER_REQUIRED.getText(),
		                         null);
	}

	@Override
	public IStatus validate(Object value) {
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
			throw new IllegalArgumentException(ARGUMENT_MUST_BE_STRING.getText());
		}
	}

}
