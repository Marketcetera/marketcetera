package org.marketcetera.photon.ui.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An instance of {@link IValidator} that ignores the "null"
 * value.  That is, {@link Status#OK_STATUS}
 * is returned if the value is null or a zero-length string.
 * 
 * Non-null values are passed through to a wrapped validator.
 * 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 *
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class IgnoreNullValidator implements IValidator {

	private final IValidator wrappedValidator;
	
	/**
	 * Create a new validator that passes through to another.
	 * 
	 * @param wrappedValidator
	 */
	public IgnoreNullValidator(IValidator wrappedValidator) {
		this.wrappedValidator = wrappedValidator;
	}

	/**
	 * Returns {@link Status#OK_STATUS} if value is null or a
	 * zero-length string.  Returns the result of {@link IValidator#validate(Object)} on the
	 * wrapped validator in all other cases. 
	 */
	public IStatus validate(Object value) {
		if (value == null || value.toString().length()==0){
			return Status.OK_STATUS;
		}
		return wrappedValidator.validate(value);
	}

}
