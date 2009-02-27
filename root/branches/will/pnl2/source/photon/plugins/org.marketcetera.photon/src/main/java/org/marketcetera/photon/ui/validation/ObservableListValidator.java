package org.marketcetera.photon.ui.validation;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

/**
 * This class will validate a given value against a list, returning
 * {@link Status#OK_STATUS} if the value is found in the list.
 * 
 * @author gmiller
 *
 */
public class ObservableListValidator
    implements IValidator, Messages
{

	private final IObservableList list;
	private Status noMatchStatus;

	/**
	 * Construct a new validator that can validate the value passed to {@link #validate(Object)}
	 * against the list given here.  If the value is found in the list, {@link Status#OK_STATUS}
	 * is returned by validate().  If the value is not found in the list, a Status with either
	 * {@link IStatus#ERROR} is returned 
	 * 
	 * @param list the list to validate against
	 * @param pluginID the plugin ID to give to the {@link Status} constructor
	 * @param errorMessage the error message to pass to the {@link Status} constructor
	 * @throws NullPointerException if list is null;
	 * 
	 * @see Status#Status(int, String, int, String, Throwable)
	 */
	public ObservableListValidator(IObservableList list, String pluginID,
			String errorMessage) {
		this(list, pluginID, errorMessage, true);
	}

	/**
	 * Construct a new validator that can validate the value passed to {@link #validate(Object)}
	 * against the list given here.  If the value is found in the list, {@link Status#OK_STATUS}
	 * is returned by validate().  If the value is not found in the list, a Status with either
	 * {@link IStatus#WARNING} or {@link IStatus#ERROR} is returned based on the value of 
	 * errorOnNoMatch.
	 * 
	 * @param list the list to validate against
	 * @param pluginID the plugin ID to give to the {@link Status} constructor
	 * @param errorMessage the error message to pass to the {@link Status} constructor
	 * @param errorOnNoMatch true if the validator should result in an error upon lack of match, false if a warning should be generated.
	 * @throws NullPointerException if list is null;
	 * 
	 * @see Status#Status(int, String, int, String, Throwable)
	 */
	public ObservableListValidator(IObservableList list, String pluginID,
			String errorMessage, boolean errorOnNoMatch) {
		if (list == null){
			throw new NullPointerException();
		}
		this.list = list;
		int severity;
		if (errorOnNoMatch){
			severity = IStatus.ERROR;
		} else {
			severity = IStatus.WARNING;
		}
		noMatchStatus = new Status(severity, pluginID, IStatus.OK, errorMessage, null);
	}

	
	/**
	 * The given object is considered valid if it can be matched against the list
	 * stored in this validator.  The element is considered to be in the list if
	 * either {@link IObservableList#contains(Object)} returns true, or
	 * if the argument is converted to an uppercase string, and that string
	 * is found in the list.
	 * @return an {@link IStatus} with severity {@link IStatus#OK} on success, or otherwise {@link IStatus#WARNING} or {@link IStatus#ERROR}
	 * @see #ObservableListValidator(IObservableList, String, String, boolean)
	 * 
	 */
	public IStatus validate(Object arg0) {
		if (list.contains(arg0) || list.contains(arg0.toString().toUpperCase())){
			return Status.OK_STATUS;
		} else {
			if (arg0 instanceof String) {
				String theString = (String) arg0;
				if (theString.trim().length() ==0){
					return new Status(Status.ERROR,
					                  PhotonPlugin.ID,
					                  Status.OK,
					                  VALUE_REQUIRED.getText(),
					                  null);
				}
			}
			return noMatchStatus;
		}
	}
}
