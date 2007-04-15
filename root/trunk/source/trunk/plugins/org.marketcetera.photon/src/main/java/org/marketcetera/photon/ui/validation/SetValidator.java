package org.marketcetera.photon.ui.validation;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.PhotonPlugin;

public class SetValidator<T> implements IValidator {

	private HashSet<T> validSet;
	private IStatus errorStatus;

	public SetValidator(Collection<T> collection, String pluginID,  String errorMessage) {
		validSet = new HashSet<T>(collection);
		errorStatus = new Status(IStatus.ERROR, pluginID, IStatus.OK, errorMessage, null);
	}

	public IStatus validate(Object arg0) {
		if (validSet.contains(arg0)){
			return Status.OK_STATUS;
		} else {
			return errorStatus;
		}
	}

}
