package org.marketcetera.photon.internal.strategy;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Common {@link Strategy} related validation functions.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class StrategyValidation {

	/**
	 * Validates that a display name is not blank.
	 * 
	 * @param displayName
	 *            the display name
	 * @return validation result
	 */
	public static IStatus validateDisplayNameNotBlank(String displayName) {
		return validateNotBlank(Messages.STRATEGYUI_DISPLAY_NAME_LABEL
				.getText(), displayName);
	}

	/**
	 * Validates that a display name is unique.
	 * 
	 * @param displayName
	 *            the display name
	 * @return validation result
	 */
	public static IStatus validateDisplayNameUnique(String displayName) {
		if (!StrategyManager.getCurrent().isUniqueName(displayName)) {
			return ValidationStatus
					.error(Messages.STRATEGY_VALIDATION_NAME_NOT_UNIQUE
							.getText(displayName));
		}
		return ValidationStatus.ok();
	}

	/**
	 * Validates that an arbitrary field is not blank.
	 * 
	 * @param field
	 *            the human readable name of the field for error messages
	 * @param string
	 *            the field value to validate
	 * @return the validation result
	 */
	public static IStatus validateNotBlank(String field, String string) {
		if (StringUtils.isBlank(string)) {
			return ValidationStatus
					.error(Messages.STRATEGY_VALIDATION_REQUIRED_FIELD_BLANK
							.getText(field));
		}
		return ValidationStatus.ok();
	}
}
