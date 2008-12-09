package org.marketcetera.photon;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.observable.list.ComputedList;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.LabelProvider;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.trade.BrokerID;
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
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class BrokerManager implements IBrokerIdValidator {

	/**
	 * The default/null broker.
	 */
	public static final Broker AUTO_SELECT_BROKER = new Broker(
			Messages.BROKER_MANAGER_AUTO_SELECT.getText(), null);

	/**
	 * Returns the singleton instance for the currently running plug-in.
	 * 
	 * @return the singleton instance
	 */
	public static BrokerManager getCurrent() {
		return PhotonPlugin.getDefault().getBrokerManager();
	}

	private IObservableList mBrokers = WritableList
			.withElementType(BrokerStatus.class);

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

	public void setBrokersStatus(BrokersStatus statuses) {
		mBrokers.clear();
		mBrokers.addAll(statuses.getBrokers());
	}

	private final class AvailableBrokers extends ComputedList {

		@Override
		protected List<?> calculate() {
			List<Broker> list = new ArrayList<Broker>();
			list.add(AUTO_SELECT_BROKER);
			for (Object object : mBrokers) {
				BrokerStatus brokerStatus = (BrokerStatus) object;
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
		if (StringUtils.isBlank(brokerId)) {
			return false;
		}
		for (Object object : mAvailableBrokers) {
			Broker broker = (Broker) object;
			if (broker.getId() != null && broker.getId().getValue() != null
					&& broker.getId().getValue().equals(brokerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A Photon abstraction for a broker.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	@ClassVersion("$Id$")
	public final static class Broker {
		private String mName;
		private BrokerID mId;

		private Broker(String name, BrokerID id) {
			mName = name;
			mId = id;
		}

		/**
		 * Returns the broker name.
		 * 
		 * @return the broker name
		 */
		public String getName() {
			return mName;
		}

		/**
		 * Returns the broker id.
		 * 
		 * @return the broker id
		 */
		public BrokerID getId() {
			return mId;
		}
	}

	/**
	 * Adapter for displaying {@link Broker} objects.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	@ClassVersion("$Id$")
	public final static class BrokerLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			Broker broker = (Broker) element;
			if (broker == BrokerManager.AUTO_SELECT_BROKER)
				return broker.getName();
			return Messages.BROKER_LABEL_PATTERN.getText(broker.getName(),
					broker.getId());
		}
	}

}
