package org.marketcetera.fix;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.HasClusterData;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Provides <code>BrokerStatus</code> with <code>ClusterData</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusteredBrokerStatus
        extends BrokerStatus
        implements HasClusterData
{
    /**
     * Create a new ClusteredBrokerStatus instance.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inClusterData a <code>ClusterData</code> value
     * @param inStatus a <code>FixSessionStatus</code> value
     * @param inIsLoggedOn a <code>boolean</code> value
     */
    public ClusteredBrokerStatus(FixSession inFixSession,
                                 ClusterData inClusterData,
                                 FixSessionStatus inStatus,
                                 boolean inIsLoggedOn)
    {
        super(inFixSession.getName(),
              new BrokerID(inFixSession.getBrokerId()),
              inIsLoggedOn,
              inFixSession.getSessionSettings());
        clusterData = inClusterData;
        status = inStatus;
        Validate.notNull(clusterData);
        Validate.notNull(status);
        Validate.notNull(inFixSession);
        host = inFixSession.getHost();
        port = inFixSession.getPort();
        Validate.notNull(host);
    }
    /**
     * Get the status value.
     *
     * @return a <code>FixSessionStatus</code> value
     */
    public FixSessionStatus getStatus()
    {
        return status;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.cluster.HasClusterData#getClusterData()
     */
    @Override
    public ClusterData getClusterData()
    {
        return clusterData;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Broker: ").append(getName()).append(" (").append(getId()).append(")");
        if(status == null) {
            builder.append("is unknown on ");
        } else {
            switch(status) {
                case AFFINITY_MISMATCH:
                    builder.append("is not bound on ");
                    builder.append(clusterData);
                    break;
                case BACKUP:
                    builder.append("is backup on ");
                    builder.append(clusterData);
                    break;
                case CONNECTED:
                    builder.append("is available on ");
                    builder.append(clusterData);
                    break;
                case DELETED:
                    builder.append("has been deleted");
                    break;
                case DISABLED:
                    builder.append("is disabled");
                    break;
                case DISCONNECTED:
                    builder.append("is disconnected on ");
                    builder.append(clusterData);
                    break;
                case NOT_CONNECTED:
                    builder.append("is not connected on ");
                    builder.append(clusterData);
                    break;
                case STOPPED:
                    builder.append("is stopped on ");
                    builder.append(clusterData);
                    break;
                default:
                    break;
            }
        }
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(getId()).append(clusterData).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof ClusteredBrokerStatus)) {
            return false;
        }
        ClusteredBrokerStatus other = (ClusteredBrokerStatus) obj;
        return new EqualsBuilder().append(getId(),other.getId()).append(clusterData,other.clusterData).isEquals();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.brokers.BrokerStatus#getHost()
     */
    @Override
    public String getHost()
    {
        return host;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.brokers.BrokerStatus#getPort()
     */
    @Override
    public int getPort()
    {
        return port;
    }
    /**
     * Create a new ClusteredBrokerStatus instance.
     */
    @SuppressWarnings("unused")
    private ClusteredBrokerStatus()
    {
        clusterData = null;
        status = null;
        host = null;
        port = 0;
    }
    /**
     * status value
     */
    private final FixSessionStatus status;
    /**
     * host value
     */
    private final String host;
    /**
     * port value
     */
    private final int port;
    /**
     * identifies the cluster
     */
    private final ClusterData clusterData;
    private static final long serialVersionUID = -1837912946225621L;
}
