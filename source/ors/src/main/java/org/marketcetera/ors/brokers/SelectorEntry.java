package org.marketcetera.ors.brokers;

import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.util.except.I18NRuntimeException;
import org.marketcetera.util.log.I18NBoundMessage1P;
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
    private final SecurityType mTargetType;
    private final BrokerID mBrokerID;


    // CONSTRUCTORS.

    /**
     * Creates a new entry based on the given configuration.
     *
     * @param springSelectorEntry The configuration.
     */

    public SelectorEntry
        (SpringSelectorEntry springSelectorEntry)
    {
        mSpringSelectorEntry=springSelectorEntry;
        mTargetType=SecurityType.getInstanceForFIXValue
            (getSpringSelectorEntry().getTargetType());
        if (SecurityType.Unknown.equals(getTargetType())) {
            throw new I18NRuntimeException
                (new I18NBoundMessage1P
                 (Messages.UNKNOWN_SECURITY_TYPE,
                  getSpringSelectorEntry().getTargetType()));
                                           
        }
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
     * Returns the receiver's target type.
     *
     * @return The target type, which will guaranteed to be known.
     */

    public SecurityType getTargetType()
    {
        return mTargetType;
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
}
