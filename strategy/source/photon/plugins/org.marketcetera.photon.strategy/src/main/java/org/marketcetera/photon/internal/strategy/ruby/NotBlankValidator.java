package org.marketcetera.photon.internal.strategy.ruby;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.marketcetera.photon.internal.strategy.Messages;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Validator the fails for blank string fields.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class NotBlankValidator extends MultiValidator {
	private String mField;
	private IObservableValue mValue;

	/**
	 * Constructor.
	 * 
	 * @param value
	 *            the observable to validate
	 * @param field
	 *            a human readable description of the field for the error
	 *            message, preferably the label
	 */
	NotBlankValidator(IObservableValue value, String field) {
		Assert.isLegal(value.getValueType() == String.class);
		mValue = value;
		mField = field;
	}

	@Override
	public IStatus validate() {
		String string = (String) mValue.getValue();
		if (StringUtils.isBlank(string)) {
			return ValidationStatus
					.error(Messages.REGISTER_RUBY_STRATEGY_REQUIRED_FIELD_BLANK
							.getText(mField));
		}
		return ValidationStatus.ok();
	}
}