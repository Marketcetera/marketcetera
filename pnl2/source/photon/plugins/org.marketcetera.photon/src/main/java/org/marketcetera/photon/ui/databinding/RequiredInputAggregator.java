package org.marketcetera.photon.ui.databinding;

import java.util.List;

import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.Messages;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Determines whether all the observable values in the given list (which
 * should represent UI elements) have values.  If not, the aggregated status 
 * value is set to error.
 * 
 * @author gmiller
 * @version $Id$
 * @since 1.0.0
 *
 */
@ClassVersion("$Id")
public class RequiredInputAggregator
    extends ComputedValue
    implements Messages
{
	final List<IObservableValue> observables;
	
	public RequiredInputAggregator(List<IObservableValue> observables) {
		super();
		this.observables = observables;
	}

	@Override
	protected Object calculate() {
		for (IObservableValue observableValue : observables) {
			Object value;
			if ((value = observableValue.getValue()) == null || value.toString().length() == 0){
				return ValidationStatus.error(INPUT_REQUIRED.getText());
			}
		}
		
		return Status.OK_STATUS;
	}

}
