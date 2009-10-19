package org.marketcetera.photon;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.observable.Realm;
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
 * This class is thread safe.
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

    private final IObservableList mBrokers = new WritableList(new SyncRealm(),
            new ArrayList<Object>(), BrokersStatus.class);

    private final IObservableList mAvailableBrokers = new AvailableBrokers();

    /**
     * Returns an observable list of the available brokers managed by this
     * class.
     * 
     * @return the available brokers
     */
    public IObservableList getAvailableBrokers() {
        return mAvailableBrokers;
    }

    /**
     * Updates the available brokers from a {@link BrokersStatus} event.
     * 
     * @param statuses
     *            the new statuses
     */
    public synchronized void setBrokersStatus(BrokersStatus statuses) {
        mBrokers.clear();
        mBrokers.addAll(statuses.getBrokers());
    }

    @Override
    public boolean isValid(String brokerId) {
        if (StringUtils.isBlank(brokerId)) {
            return false;
        }
        synchronized (this) {
            for (Object object : mAvailableBrokers) {
                Broker broker = (Broker) object;
                if (broker.getId() != null && broker.getId().getValue() != null
                        && broker.getId().getValue().equals(brokerId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Synchronizes access to the available brokers list.
     */
    @ClassVersion("$Id$")
    private final class SyncRealm extends Realm {
        @Override
        public boolean isCurrent() {
            return true;
        }

        @Override
        protected void syncExec(Runnable runnable) {
            synchronized (BrokerManager.this) {
                super.syncExec(runnable);
            }
        }
    }

    /**
     * List that combines the default {@link BrokerManager#AUTO_SELECT_BROKER} broker with the available ones
     */
    @ClassVersion("$Id$")
    private final class AvailableBrokers extends ComputedList {

        public AvailableBrokers() {
            super(mBrokers.getRealm(), mBrokers.getElementType());
        }

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

    /**
     * A Photon abstraction for a broker.
     */
    @ClassVersion("$Id$")
    public final static class Broker {
        private final String mName;
        private final BrokerID mId;

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
