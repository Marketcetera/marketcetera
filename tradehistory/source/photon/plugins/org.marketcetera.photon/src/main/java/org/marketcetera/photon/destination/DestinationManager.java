package org.marketcetera.photon.destination;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.ComputedList;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.client.dest.DestinationsStatus;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages a collection of {@link DestinationStatus} objects.
 * 
 * This class manages a {@link WritableList} of {@link DestinationStatus}
 * objects, and as such, it is thread safe. An exception will be thrown if it is
 * accessed from any thread other than the one that created it.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DestinationManager implements IDestinationValidator {

	/**
	 * Returns the singleton instance for the currently running plug-in.
	 * 
	 * @return the singleton instance
	 */
	public static IDestinationValidator getCurrent() {
		return PhotonPlugin.getDefault().getDestinationManager();
	}

	private IObservableList mDestinations = WritableList
			.withElementType(DestinationStatus.class);

	private IObservableList mAvailableDestinations = new AvailableDestinations();

	public IObservableList getAvailableDestinations() {
		return mAvailableDestinations;
	}

	void setDestinationsStatus(DestinationsStatus statuses) {
		mDestinations.clear();
		mDestinations.addAll(statuses.getDestinations());
	}

	private class AvailableDestinations extends ComputedList {

		@SuppressWarnings("unchecked")
		@Override
		protected List calculate() {
			List list = new ArrayList();
			for (Object object : mDestinations) {
				if (((DestinationStatus) object).getLoggedOn()) {
					list.add(object);
				}
			}
			return list;
		}
	}

	@Override
	public boolean isValid(String destination) {
		for (Object object : mAvailableDestinations) {
			if (((DestinationStatus) object).getId().equals(destination))
				return true;
		}
		return false;
	}

}
