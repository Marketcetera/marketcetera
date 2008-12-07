package org.marketcetera.ors.brokers;

import java.util.ArrayList;
import java.util.List;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The in-memory representation of the selector.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class Selector
{

    // INSTANCE DATA.

    private final Brokers mDestinations;
    private final SpringSelector mSpringSelector;
    private List<SelectorEntry> mEntries;
    private final DestinationID mDefaultDestinationID;


    // CONSTRUCTORS.

    /**
     * Creates a new selector based on the given configuration.
     *
     * @param springSelector The configuration.
     */

    public Selector
        (Brokers destinations,
         SpringSelector springSelector)
    {
        mDestinations=destinations;
        mSpringSelector=springSelector;
        if (getSpringSelector().getEntries()!=null) {
            mEntries=new ArrayList<SelectorEntry>
                (getSpringSelector().getEntries().size());
            for (SpringSelectorEntry se:getSpringSelector().getEntries()) {
                mEntries.add(new SelectorEntry(se));
            }
        }
        if (getSpringSelector().getDefaultDestination()!=null) {
            mDefaultDestinationID=new DestinationID
                (getSpringSelector().getDefaultDestination().getId());
        } else {
            mDefaultDestinationID=null;
        }
    }


    // INSTANCE METHODS.

    private Brokers getDestinations()
    {
        return mDestinations;
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
     * Returns the receiver's default destination ID.
     *
     * @return The ID. It may be null.
     */

    public DestinationID getDefaultDestination()
    {
        return mDefaultDestinationID;
    }

    /**
     * Returns the ID of the destination the receiver selects for the
     * given order.
     *
     * @param order The order.
     *
     * @return The ID of the selected destination, or null if the
     * selector cannot make a selection.
     */

    public DestinationID chooseDestination
        (Order order)
    {
        // Destination was explicit.

        DestinationID orderDest=order.getDestinationID();
        if (orderDest!=null) {
            return orderDest;
        }

        // Search through entries (if any) for one that matches the
        // order type (provided the order has a known type).

        SecurityType orderType=order.getSecurityType();
        if ((orderType!=null) && (orderType!=SecurityType.Unknown) &&
            (getEntries()!=null)) {
            for (SelectorEntry e:getEntries()) {
                if (e.getSkipIfUnavailable() &&
                    !getDestinations().getDestination(e.getDestination()).
                    getLoggedOn()) {
                    continue;
                }
                if (e.getTargetType().equals(orderType)) {
                    return e.getDestination();
                }
            }
        }

        // Return the default, if any.

        if (getDefaultDestination()!=null) {
            return getDefaultDestination();
        }

        // No match.

        return null;
    }
}
