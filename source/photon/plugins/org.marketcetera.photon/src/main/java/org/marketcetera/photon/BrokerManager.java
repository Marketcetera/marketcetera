package org.marketcetera.photon;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.ComputedList;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.client.dest.DestinationsStatus;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages a collection of brokers objects.
 * 
 * This class manages a {@link WritableList}, and as such, it is thread safe. An
 * exception will be thrown if it is accessed from any thread other than the one
 * that created it.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class BrokerManager implements IBrokerIdValidator {

	/**
	 * The default/null broker.
	 */
	public static final Broker DEFAULT_BROKER = new Broker(Messages.BROKER_MANAGER_DEFAULT_BROKER_NAME.getText(), null);

	/**
	 * Returns the singleton instance for the currently running plug-in.
	 * 
	 * @return the singleton instance
	 */
	public static BrokerManager getCurrent() {
		return PhotonPlugin.getDefault().getBrokerManager();
	}

	private IObservableList mBrokers = WritableList
			.withElementType(DestinationStatus.class);

	private IObservableList mAvailableBrokers = new AvailableBrokers();

	/**
	 * Returns an observable list of the available brokers managed by this
	 * class.
	 * 
	 * @return the available brokers
	 */
	public IObservableList getAvailableBrokers() {
		return mAvailableBrokers;
	}

	public void setBrokersStatus(DestinationsStatus statuses) {
		mBrokers.clear();
		mBrokers.addAll(statuses.getDestinations());
	}

	private final class AvailableBrokers extends ComputedList {

		@Override
		protected List<?> calculate() {
			List<Broker> list = new ArrayList<Broker>();
			list.add(DEFAULT_BROKER);
			for (Object object : mBrokers) {
				DestinationStatus brokerStatus = (DestinationStatus) object;
				if (brokerStatus.getLoggedOn()) {
					list.add(new Broker(brokerStatus.getName(), brokerStatus
							.getId()));
				}
			}
			return list;
		}
	}

	@Override
	public boolean isValid(String brokerId) {
		for (Object object : mAvailableBrokers) {
			if (((DestinationStatus) object).getId().equals(brokerId))
				return true;
		}
		return false;
	}

	/**
	 * A Photon abstraction for a broker.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")
	public final static class Broker {
		private String mName;
		private DestinationID mId;

		private Broker(String name, DestinationID id) {
			mName = name;
			mId = id;
		}

		public String getName() {
			return mName;
		}

		public DestinationID getId() {
			return mId;
		}
	}

}
