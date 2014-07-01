package org.marketcetera.client.brokers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The collective web service representation of the status of all
 * brokers.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class BrokersStatus
{
    /**
     * Creates a new collective status representation, given the status of the brokers.
     *
     * @param inBrokers a <code>List&lt;BrokerStatus&gt;</code> value
     */
    public BrokersStatus(List<BrokerStatus> inBrokers)
    {
        brokers = inBrokers;
    }
    /**
     * Create a new BrokersStatus instance.
     */
    public BrokersStatus()
    {
        brokers = new ArrayList<BrokerStatus>();
    }
    /**
     * Returns the status of the receiver's brokers.
     * 
     * <p>The returned list is not modifiable.
     *
     * @return a <code>List&lt;BrokerStatus&gt;</code> value
     */
    public List<BrokerStatus> getBrokers()
    {
        return Collections.unmodifiableList(brokers);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BrokersStatus [mBrokers=").append(brokers).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        return builder.toString();
    }
    /**
     * brokers value
     */
    private final List<BrokerStatus> brokers;
}
