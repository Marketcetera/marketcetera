package org.marketcetera.photon.ui.databinding;

import java.util.List;

import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.Messages;

/**
 * Determines whether all the observable values in the given list (which
 * should represent UI elements) have values.  If not, the aggregated status 
 * value is set to error.
 * 
 * @author gmiller
 *
 */
public class RequiredInputAggregator
    extends ComputedValue
    implements Messages
{
	public static final String REQUIRES_USER_INPUT_KEY = "REQUIRES_USER_INPUT"; //$NON-NLS-1$
	final List<IObservableValue> observables;
	
	public RequiredInputAggregator(List<IObservableValue> observables) {
		super();
		this.observables = observables;
	}

	@Override
	protected Object calculate() {
		IStatus returnStatus = null;
		for (IObservableValue observableValue : observables) {
			Object value;
			if ((value = observableValue.getValue()) == null || value.toString().length() == 0){
				returnStatus = ValidationStatus.error(INPUT_REQUIRED.getText());
			}
		}
		
		if (returnStatus == null){
			return Status.OK_STATUS;
		} else {
			return returnStatus;
		}
	}

}
