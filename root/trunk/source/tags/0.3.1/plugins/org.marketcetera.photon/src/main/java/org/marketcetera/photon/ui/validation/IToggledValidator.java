package org.marketcetera.photon.ui.validation;

import org.eclipse.core.databinding.validation.IValidator;

/**
 * Validate values only when enabled.
 */
public interface IToggledValidator extends IValidator {
	boolean isEnabled();

	void setEnabled(boolean enabled);
}
