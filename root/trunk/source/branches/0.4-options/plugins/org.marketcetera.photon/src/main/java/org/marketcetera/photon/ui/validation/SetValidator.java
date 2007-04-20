package org.marketcetera.photon.ui.validation;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SetValidator<T> extends AbstractToggledValidator {

	private HashSet<T> validSet;
	private IStatus errorStatus;

	public SetValidator(Collection<T> collection, String pluginID,  String errorMessage) {
		validSet = new HashSet<T>(collection);
		errorStatus = new Status(IStatus.ERROR, pluginID, IStatus.OK, errorMessage, null);
	}

	public IStatus validate(Object arg0) {
		if (!isEnabled()) {
			return Status.OK_STATUS;
		}
		if (validSet.contains(arg0)){
			return Status.OK_STATUS;
		} else {
			return errorStatus;
		}
	}

}
