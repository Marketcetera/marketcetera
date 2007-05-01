package org.marketcetera.photon.ui.validation;


public abstract class AbstractToggledValidator implements IToggledValidator {

	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
