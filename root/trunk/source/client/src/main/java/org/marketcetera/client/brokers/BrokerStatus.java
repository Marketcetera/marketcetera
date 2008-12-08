package org.marketcetera.client.brokers;

import java.io.Serializable;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The web service representation of a single broker's status.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class BrokerStatus
    implements Serializable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private String mName;
    private BrokerID mId;
    private boolean mLoggedOn;


    // CONSTRUCTORS.

    /**
     * Creates a new status representation, given the broker
     * information.
     *
     * @param name The broker name.
     * @param id The broker ID.
     * @param loggedOn The logon flag.
     */

    public BrokerStatus
        (String name,
         BrokerID id,
         boolean loggedOn)
    {
        setName(name);
        setId(id);
        setLoggedOn(loggedOn);
    }

    /**
     * Creates a new status representation. This empty constructor is
     * intended for use by JAXB.
     */

    protected BrokerStatus() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's name to the given value.
     *
     * @param name The name.
     */

    public void setName
        (String name)
    {
        mName=name;
    }

    /**
     * Returns the receiver's name.
     *
     * @return The name.
     */

    public String getName()
    {
        return mName;
    }

    /**
     * Sets the receiver's broker ID to the given value,
     *
     * @param id The ID.
     */

    public void setId
        (BrokerID id)
    {
        mId=id;
    }

    /**
     * Returns the receiver's broker ID.
     *
     * @return The ID.
     */

    public BrokerID getId()
    {
        return mId;
    }

    /**
     * Sets the receiver's logon flag to the given value.
     *
     * @param loggedOn The flag.
     */

    public void setLoggedOn
        (boolean loggedOn)
    {
        mLoggedOn=loggedOn;
    }

    /**
     * Returns the receiver's logon flag.
     *
     * @return The flag.
     */

    public boolean getLoggedOn()
    {
        return mLoggedOn;
    }

    @Override
    public String toString()
    {
        return String.format("Broker: %s(%s,%s)", //$NON-NLS-1$
                             String.valueOf(mName),
                             String.valueOf(mId),
                             mLoggedOn);
    }
}
