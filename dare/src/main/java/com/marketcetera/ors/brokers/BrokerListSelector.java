package com.marketcetera.ors.brokers;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Order;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The in-memory representation of the selector.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: BrokerListSelector.java 17266 2017-04-28 14:58:00Z colin $
 */

/* $License$ */

@ClassVersion("$Id: BrokerListSelector.java 17266 2017-04-28 14:58:00Z colin $")
public class BrokerListSelector
        implements Selector
{

    // INSTANCE DATA.

    private final Brokers mBrokers;
    private final SpringSelector mSpringSelector;
    private List<SelectorEntry> mEntries;
    private BrokerID mDefaultBrokerID;


    // CONSTRUCTORS.

    /**
     * Creates a new selector based on the given configuration.
     *
     * @param springSelector The configuration.
     */

    public BrokerListSelector(Brokers brokers,
                              SpringSelector springSelector)
    {
        mBrokers=brokers;
        mSpringSelector=springSelector;
        if (getSpringSelector().getEntries()!=null) {
            mEntries=new ArrayList<SelectorEntry>
                (getSpringSelector().getEntries().size());
            for (SpringSelectorEntry se:getSpringSelector().getEntries()) {
                mEntries.add(new SelectorEntry(se));
            }
        }
        if (getSpringSelector().getDefaultBroker()!=null) {
            mDefaultBrokerID=new BrokerID
                (getSpringSelector().getDefaultBroker().getId());
        } else {
            mDefaultBrokerID=null;
        }
    }
    /**
     * Create a new Selector instance.
     */
    public BrokerListSelector()
    {
        mBrokers = null;
        mSpringSelector = null;
    }

    // INSTANCE METHODS.

    private Brokers getBrokers()
    {
        return mBrokers;
    }

    /**
     * Returns the receiver's configuration.
     *
     * @return The configuration.
     */

    public SpringSelector getSpringSelector()
    {
        return mSpringSelector;
    }

    /**
     * Returns the receiver's entries.
     *
     * @return The entries. It may be null.
     */

    public List<SelectorEntry> getEntries()
    {
        return mEntries;
    }

    /**
     * Returns the receiver's default broker ID.
     *
     * @return The ID. It may be null.
     */

    public BrokerID getDefaultBroker()
    {
        return mDefaultBrokerID;
    }
    /**
     * Set the default broker id to use.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     */
    public void setDefaultBroker(BrokerID inBrokerId)
    {
        mDefaultBrokerID = inBrokerId;
    }

    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.Selector#chooseBroker(org.marketcetera.trade.Order)
     */

    @Override
    public BrokerID chooseBroker
        (Order order)
    {
        // Broker was explicit.

        BrokerID bID=order.getBrokerID();
        if (bID!=null) {
            return bID;
        }
        // Search through entries (if any) for the first one that matches
        if(getEntries()!=null) {
            for(SelectorEntry e:getEntries()) {
                if(e.getSkipIfUnavailable() && !getBrokers().getBroker(e.getBroker()).getLoggedOn()) {
                    continue;
                }
                if(e.routeToBroker(order)) {
                    return e.getBroker();
                }
            }
        }

        // Return the default, if any.

        if (getDefaultBroker()!=null) {
            return getDefaultBroker();
        }

        // No match.

        return null;
    }
}
