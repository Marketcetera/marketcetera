package org.marketcetera.photon;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.LabelProvider;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    public static final Broker AUTO_SELECT_BROKER = new Broker(Messages.BROKER_MANAGER_AUTO_SELECT.getText(),null,null);

    /**
     * Returns the singleton instance for the currently running plug-in.
     * 
     * @return the singleton instance
     */
    public static BrokerManager getCurrent() {
        return PhotonPlugin.getDefault().getBrokerManager();
    }

    private final IObservableList mAvailableBrokers = new WritableList(new SyncRealm(),
                                                                       Lists.newArrayList(AUTO_SELECT_BROKER),
                                                                       Broker.class);
    private final IObservableList mUnmodifiableAvailableBrokers = Observables.unmodifiableObservableList(mAvailableBrokers);

    private final Map<BrokerID, Broker> mBrokerMap = Maps.newHashMap();

    /**
     * Returns an observable list of the available brokers managed by this
     * class. The returned list should not be modified.
     * 
     * @return the available brokers
     */
    public IObservableList getAvailableBrokers() {
        return mUnmodifiableAvailableBrokers;
    }

    /**
     * Updates the available brokers from a {@link BrokersStatus} event.
     * 
     * @param statuses
     *            the new statuses
     */
    public synchronized void setBrokersStatus(BrokersStatus statuses) {
        mBrokerMap.clear();
        mAvailableBrokers.clear();
        List<Broker> availableBrokers = Lists.newArrayList();
        availableBrokers.add(AUTO_SELECT_BROKER);
        for (BrokerStatus status : statuses.getBrokers()) {
            if (status.getLoggedOn()) {
                Broker broker = new Broker(status.getName(),
                                           status.getId(),
                                           status.getBrokerAlgos());
                mBrokerMap.put(status.getId(), broker);
                availableBrokers.add(broker);
            }
        }
        mAvailableBrokers.addAll(availableBrokers);
    }

    @Override
    public synchronized boolean isValid(String brokerId) {
        Validate.notNull(brokerId, "brokerID"); //$NON-NLS-1$
        return mBrokerMap.containsKey(new BrokerID(brokerId));
    }

    /**
     * Returns the {@link Broker} object for a given {@link BrokerID}. If no
     * Broker is found, null is returned.
     * 
     * @param brokerId
     *            the broker id
     * @return the broker
     */
    public synchronized Broker getBroker(BrokerID brokerId) {
        if (brokerId == null) {
            return AUTO_SELECT_BROKER;
        }
        return mBrokerMap.get(brokerId);
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
     * A Photon abstraction for a broker.
     */
    @ClassVersion("$Id$")
    public final static class Broker {
        private final String mName;
        private final BrokerID mId;
        private final Set<BrokerAlgoSpec> algos;

        private Broker(String name,
                       BrokerID id,
                       Set<BrokerAlgoSpec> inAlgos)
        {
            mName = name;
            mId = id;
            algos = inAlgos;
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
        /**
         * Get the algos value.
         *
         * @return a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
         */
        public Set<BrokerAlgoSpec> getAlgos()
        {
            return algos;
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
