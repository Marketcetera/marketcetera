package org.marketcetera.ors.brokers;

import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Order;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The in-memory representation of a selector entry.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SelectorEntry
{

    // INSTANCE DATA.

    private final SpringSelectorEntry mSpringSelectorEntry;
    private final BrokerID mBrokerID;


    // CONSTRUCTORS.

    /**
     * Creates a new entry based on the given configuration.
     *
     * @param springSelectorEntry The configuration.
     */

    public SelectorEntry(SpringSelectorEntry springSelectorEntry)
    {
        mSpringSelectorEntry=springSelectorEntry;
        mBrokerID=new BrokerID(getSpringSelectorEntry().getBroker().getId());
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's configuration.
     *
     * @return The configuration.
     */

    public SpringSelectorEntry getSpringSelectorEntry()
    {
        return mSpringSelectorEntry;
    }
    /**
     * Returns the receiver's broker ID.
     *
     * @return The ID.
     */

    public BrokerID getBroker()
    {
        return mBrokerID;
    }

    /**
     * Returns the receiver's skip-if-unavailable flag.
     *
     * @return The flag.
     */

    public boolean getSkipIfUnavailable()
    {
        return getSpringSelectorEntry().getSkipIfUnavailable();
    }
    /**
     * Indicates if the given <code>Order</code> should be routed to the selector broker.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>boolean</code> value
     */
    public boolean routeToBroker(Order inOrder)
    {
        return getSpringSelectorEntry().routeToBroker(inOrder);
    }
}
