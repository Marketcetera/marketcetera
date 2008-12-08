package org.marketcetera.client.brokers;

import java.util.List;
import java.util.Collections;

import org.marketcetera.util.misc.ClassVersion;

/**
 * The collective web service representation of the status of all
 * brokers.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class BrokersStatus
{

    // INSTANCE DATA.

    private List<BrokerStatus> mBrokers;


    // CONSTRUCTORS.

    /**
     * Creates a new collective status representation, given the
     * status of the brokers.
     *
     * @param brokers The status.
     */

    public BrokersStatus
        (List<BrokerStatus> brokers)
    {
        setBrokers(brokers);
    }

    /**
     * Creates a new collective status representation. This empty
     * constructor is intended for use by JAXB.
     */

    protected BrokersStatus() {}


    // INSTANCE METHODS.

    /**
     * Sets the status of the receiver's brokers to the given
     * one.
     *
     * @param brokers The status.
     */

    public void setBrokers
        (List<BrokerStatus> brokers)
    {
        mBrokers=Collections.unmodifiableList(brokers);
    }

    /**
     * Returns the status of the receiver's brokers. The returned
     * list is not modifiable.
     *
     * @return The status.
     */

    public List<BrokerStatus> getBrokers()
    {
        return mBrokers;
    }
}
