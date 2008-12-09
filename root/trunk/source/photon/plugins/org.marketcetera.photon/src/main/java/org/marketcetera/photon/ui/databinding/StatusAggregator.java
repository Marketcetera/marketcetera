package org.marketcetera.photon.ui.databinding;


import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.util.misc.ClassVersion;
/* $License$ */

/**
 * Aggregate the status values of a number of observable values.
 * The status returned will reflect the highest severity of
 * any of the component statuses.
 * 
 * @author gmiller
 * @version $Id$
 * @since 1.0.0
 *
 */
@ClassVersion("$Id")
public class StatusAggregator extends ComputedValue {

	private final IObservableValue[] values;

	public StatusAggregator(IObservableValue ... values) {
		this.values = values;
	}
	
	@Override
	protected Object calculate() {
		int highestSeverity = IStatus.OK;
		IStatus highestStatus = null;
		for (IObservableValue observableValue : values) {
			IStatus currentStatus = ((IStatus)observableValue.getValue());
			int currentSeverity = currentStatus.getSeverity();
			if (currentSeverity > highestSeverity){
				highestStatus = currentStatus;
				highestSeverity = currentSeverity;
			}
		}
		if (highestStatus != null){
			//PhotonPlugin.getMainConsoleLogger().error("StatusAggregator returing status:" +highestStatus);
			return highestStatus;
		} else {
			//PhotonPlugin.getMainConsoleLogger().error("StatusAggregator returing OK");
			return Status.OK_STATUS;
		}
	}


}
