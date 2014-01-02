package org.marketcetera.client.brokers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.util.misc.ClassVersion;

/**
 * The collective web service representation of the status of all
 * brokers.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@ClassVersion("$Id$")
public class BrokersStatus
{

    // INSTANCE DATA.

    private final List<BrokerStatus> mBrokers;


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
        mBrokers=brokers;
    }

    /**
     * Creates a new collective status representation. This empty
     * constructor is intended for use by JAXB.
     */

    protected BrokersStatus()
    {
        mBrokers = new ArrayList<BrokerStatus>();
    }


    // INSTANCE METHODS.

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
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BrokersStatus [mBrokers=").append(mBrokers).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        return builder.toString();
    }
}
