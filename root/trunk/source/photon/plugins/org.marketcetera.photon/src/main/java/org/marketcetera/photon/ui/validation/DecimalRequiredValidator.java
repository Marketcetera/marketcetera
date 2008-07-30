package org.marketcetera.photon.ui.validation;

import java.math.BigDecimal;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

/**
 * Instance of {@link IValidator} that requires a non-null input that
 * represents a decimal number.
 * 
 * @author gmiller
 *
 */
public class DecimalRequiredValidator
    extends StringRequiredValidator
    implements Messages
{

	
	private IStatus errorStatus;

	/**
	 * Create a new validator.
	 */
	public DecimalRequiredValidator(){
		errorStatus = new Status(Status.ERROR,
		                         PhotonPlugin.ID,
		                         Status.OK,
		                         DECIMAL_REQUIRED.getText(),
		                         null);
	}

	/**
	 * Determines if the given value represents a decimal (as a string).
	 * 
	 * @param value the value to validate
	 * @return {@link Status#OK_STATUS} if the value represents a decimal, or an {@link IStatus} with severity {@link IStatus#ERROR} otherwise
	 */
	@Override
	public IStatus validate(Object value) {
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
			throw new IllegalArgumentException(ARGUMENT_MUST_BE_STRING.getText());
		}
	}

}
