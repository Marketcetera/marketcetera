package org.marketcetera.photon.ui.validation;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SetValidator<T> implements IValidator {

	protected HashSet<T> validSet;
	protected IStatus noMatchStatus;

	public SetValidator(Collection<T> collection, String pluginID,  String errorMessage) {
		this(collection, pluginID, errorMessage, true);
	}
	public SetValidator(Collection<T> collection, String pluginID,  String errorMessage, boolean errorOnNoMatch) {
		validSet = new HashSet<T>(collection);
		int severity;
		if (errorOnNoMatch){
			severity = IStatus.ERROR;
		} else {
			severity = IStatus.WARNING;
		}
		noMatchStatus = new Status(severity, pluginID, IStatus.OK, errorMessage, null);
	}

	public IStatus validate(Object arg0) {
		if (validSet.contains(arg0)){
			return Status.OK_STATUS;
		} else {
			return noMatchStatus;
		}
	}

}
