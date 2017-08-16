package org.marketcetera.brokers;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The collective representation of all broker status values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id: BrokersStatus.java 17411 2017-04-28 14:50:38Z colin $")
public class ClusteredBrokersStatus
        implements Serializable, Iterable<ClusteredBrokerStatus>,BrokersStatus
{
    /**
     * Creates a new collective status representation, given the status of the brokers.
     *
     * @param inBrokers a <code>List&lt;BrokerStatus&gt;</code> value
     */
    public ClusteredBrokersStatus(List<ClusteredBrokerStatus> inBrokers)
    {
        this();
        brokers.addAll(inBrokers);
    }
    /**
     * Create a new ClusteredBrokersStatus instance.
     */
    public ClusteredBrokersStatus()
    {
        brokers = new LinkedList<>();
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
    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<ClusteredBrokerStatus> iterator()
    {
        return brokers.iterator();
    }
    /**
     * brokers value
     */
    private final LinkedList<ClusteredBrokerStatus> brokers;
    private static final long serialVersionUID = -5848029285343673726L;
}
