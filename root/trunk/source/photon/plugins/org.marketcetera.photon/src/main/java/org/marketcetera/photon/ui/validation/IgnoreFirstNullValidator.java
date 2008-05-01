package org.marketcetera.photon.ui.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * An instance of {@link IValidator} that ignores the first "null"
 * value.  That is the first time validate is called, {@link Status#OK_STATUS}
 * is returned if the value is null or a zero-length string.
 * 
 * Non-null values and all subsequent calls are passed through to a wrapped validator.
 * 
 * @author gmiller
 *
 */
public class IgnoreFirstNullValidator implements IValidator {

	boolean isFirst = true;
	private final IValidator wrappedValidator;
	
	/**
	 * Create a new validator that passes through to another.
	 * 
	 * @param wrappedValidator
	 */
	public IgnoreFirstNullValidator(IValidator wrappedValidator) {
		this.wrappedValidator = wrappedValidator;
	}

	/**
	 * Returns {@link Status#OK_STATUS} if this is the first call, and value is null or a
	 * zero-length string.  Returns the result of {@link IValidator#validate(Object)} on the
	 * wrapped validator in all other cases. 
	 */
	public IStatus validate(Object value) {
		boolean wasFirst = isFirst;
		isFirst = false;
		if (wasFirst && (value == null || value.toString().length()==0)){
			return Status.OK_STATUS;
		}
		return wrappedValidator.validate(value);
	}

}
