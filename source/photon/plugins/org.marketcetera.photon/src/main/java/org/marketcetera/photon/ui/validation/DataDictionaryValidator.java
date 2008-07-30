package org.marketcetera.photon.ui.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.Messages;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;

import quickfix.DataDictionary;
import quickfix.field.TimeInForce;

/**
 * Instance of {@link IValidator} responsible for validating against
 * a QuickFIX data dictionary for a given field.
 * 
 * Note that the converters 
 * @author gmiller
 *
 */
public class DataDictionaryValidator
    implements IValidator, Messages
{

	DataDictionary dictionary;
	private IStatus errorStatus;
	private final int fieldNumber;
	private IStatus tifWarningStatus;

	/**
	 * Create a new validator for the given data dictionary and field number.
	 * 
	 * @param dictionary the data dictionary to use for validation
	 * @param fieldNumber the field number to validate
	 * @param errorMessage the error message to display upon validation failure
	 * @param pluginID the ID of the containing plugin
	 */
	public DataDictionaryValidator(DataDictionary dictionary, int fieldNumber, String errorMessage, String pluginID) {
		super();
		this.dictionary = dictionary;
		this.fieldNumber = fieldNumber;
		errorStatus = new Status(IStatus.ERROR, pluginID, IStatus.OK, errorMessage, null);
		tifWarningStatus = new Status(IStatus.WARNING,
		                              pluginID,
		                              IStatus.OK,
		                              MAY_NOT_BE_VALID_FOR_FIX_VERSION.getText(),
		                              null); //$NON-NLS-1$
	}


	/**
	 * Validate the given object against the data dictionary.  There is a special case to allow
	 * for the use of {@link TimeInForce#AT_THE_CLOSE} in all versions of FIX, because it is
	 * convenient to allow an intermediate representation to contain this value, and then
	 * to fix it up with a {@link FIXMessageAugmentor} later.
	 */
	public IStatus validate(Object obj) {
		if (obj == null){
			return errorStatus;
		}
		String objString = obj.toString();
		boolean isFieldValue = dictionary.isFieldValue(fieldNumber, objString);
		// Oooh this is ugly.  Technically "AT_THE_CLOSE" was not introduced
		// until FIX4.3, however we should probably still allow it at this level...
		
		// handle obj as String or char
		if (fieldNumber == TimeInForce.FIELD && objString.length() == 1 && TimeInForce.AT_THE_CLOSE == objString.charAt(0)
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
