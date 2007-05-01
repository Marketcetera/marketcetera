package org.marketcetera.photon.ui.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import quickfix.DataDictionary;
import quickfix.field.TimeInForce;

public class DataDictionaryValidator extends AbstractToggledValidator {

	DataDictionary dictionary;
	private IStatus errorStatus;
	private final int fieldNumber;
	private IStatus tifWarningStatus;

	
	public DataDictionaryValidator(DataDictionary dictionary, int fieldNumber, String errorMessage, String pluginID) {
		super();
		this.dictionary = dictionary;
		this.fieldNumber = fieldNumber;
		errorStatus = new Status(IStatus.ERROR, pluginID, IStatus.OK, errorMessage, null);
		tifWarningStatus = new Status(IStatus.WARNING, pluginID, IStatus.OK, "May not be valid for current FIX version", null);

	}


	public IStatus validate(Object obj) {
		if (!isEnabled()) {
			return Status.OK_STATUS;
		}
		boolean isFieldValue = dictionary.isFieldValue(fieldNumber, obj.toString());
		// Oooh this is ugly.  Technically "AT_THE_CLOSE" was not introduced
		// until FIX4.3, however we should probably still allow it at this level...
		if (fieldNumber == TimeInForce.FIELD && TimeInForce.AT_THE_CLOSE == ((Character)obj)
				&& !isFieldValue)
		{
			return tifWarningStatus;
		}
		if (isFieldValue){
			return Status.OK_STATUS;
		} else {
			return errorStatus;
		}
	}

}
