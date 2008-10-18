package org.marketcetera.photon.ui.validation;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class StringSetValidator extends SetValidator<String> {

	public StringSetValidator(Collection<String> collection, String pluginID,
			String errorMessage) {
		super(collection, pluginID, errorMessage);
	}

	public StringSetValidator(Collection<String> collection, String pluginID,
			String errorMessage, boolean errorOnNoMatch) {
		super(collection, pluginID, errorMessage, errorOnNoMatch);
	}

	
	@Override
	public IStatus validate(Object arg0) {
		if (validSet.contains(arg0) || validSet.contains(arg0.toString().toUpperCase())){
			return Status.OK_STATUS;
		} else {
			return noMatchStatus;
		}
	}
}
