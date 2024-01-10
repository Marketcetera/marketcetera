package org.marketcetera.cluster;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/* $License$ */

/**
 * Describes this cluster instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="clusterData")
@XmlAccessorType(XmlAccessType.NONE)
public class SimpleClusterData
        implements MutableClusterData,Serializable,Comparable<SimpleClusterData>
{
    /**
     * Create a new ClusterData instance.
     *
     * @param inTotalInstances an <code>int</code> value
     * @param inHostId a <code>String</code> value
     * @param inHostNumber an <code>int</code> value
     * @param inInstanceNumber an <code>int</code> value
     * @param inUuid a <code>String</code> value
     */
    public SimpleClusterData(int inTotalInstances,
                             String inHostId,
                             int inHostNumber,
                             int inInstanceNumber,
                             String inUuid)
    {
        instanceNumber = inInstanceNumber;
        hostNumber = inHostNumber;
        hostId = inHostId;
        totalInstances = inTotalInstances;
        uuid = inUuid;
    }
    /**
     * Create a new ClusterData instance.
     */
    public SimpleClusterData() {}
    /**
     * Create a new SimpleClusterData instance.
     *
     * @param inClusterData a <code>ClusterData</code> value
     */
    public SimpleClusterData(ClusterData inClusterData)
    {
        setHostId(inClusterData.getHostId());
        setHostNumber(inClusterData.getHostNumber());
        setInstanceNumber(inClusterData.getInstanceNumber());
        setTotalInstances(inClusterData.getTotalInstances());
        setUuid(inClusterData.getUuid());
    }
    /**
     * Get the instanceNumber value.
     *
     * @return an <code>int</code> value
     */
    public int getInstanceNumber()
    {
        return instanceNumber;
    }
    /**
     * Sets the instanceNumber value.
     *
     * @param inInstanceNumber an <code>int</code> value
     */
    public void setInstanceNumber(int inInstanceNumber)
    {
        instanceNumber = inInstanceNumber;
    }
    /**
     * Get the hostNumber value.
     *
     * @return an <code>int</code> value
     */
    public int getHostNumber()
    {
        return hostNumber;
    }
    /**
     * Sets the hostNumber value.
     *
     * @param inHostNumber an <code>int</code> value
     */
    public void setHostNumber(int inHostNumber)
    {
        hostNumber = inHostNumber;
    }
    /**
     * Get the hostId value.
     *
     * @return a <code>String</code> value
     */
    public String getHostId()
    {
        return hostId;
    }
    /**
     * Sets the hostId value.
     *
     * @param inHostId a <code>String</code> value
     */
    public void setHostId(String inHostId)
    {
        hostId = inHostId;
    }
    /**
     * Get the totalInstances value.
     *
     * @return an <code>int</code> value
     */
    public int getTotalInstances()
    {
        return totalInstances;
    }
    /**
     * Sets the totalInstances value.
     *
     * @param inTotalInstances an <code>int</code> value
     */
    public void setTotalInstances(int inTotalInstances)
    {
        totalInstances = inTotalInstances;
    }
    /**
     * Get the uuid value.
     *
     * @return a <code>String</code> value
     */
    public String getUuid()
    {
        return uuid;
    }
    /**
     * Sets the uuid value.
     *
     * @param inUuid a <code>String</code> value
     */
    public void setUuid(String inUuid)
    {
        uuid = inUuid;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuilder().append("host").append(hostNumber).append("-").append(instanceNumber).toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(hostId).append(instanceNumber).toHashCode();
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
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SimpleClusterData)) {
            return false;
        }
        SimpleClusterData other = (SimpleClusterData)obj;
        return new EqualsBuilder().append(hostId,other.hostId).append(instanceNumber,other.instanceNumber).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SimpleClusterData inO)
    {
        return new CompareToBuilder().append(hostNumber,inO.getHostNumber()).append(instanceNumber,inO.getInstanceNumber()).toComparison();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.HasMutableView#getMutableView()
     */
    @Override
    public MutableClusterData getMutableView()
    {
        return this;
    }
    /**
     * the instance number of this member
     */
    @XmlAttribute
    private int instanceNumber;
    /**
     * the host number of this member
     */
    @XmlAttribute
    private int hostNumber;
    /**
     * the host id of this member
     */
    @XmlAttribute
    private String hostId;
    /**
     * the total expected number of instances in the cluster
     */
    @XmlAttribute
    private int totalInstances;
    /**
     * the unique identified of this instance in the cluster
     */
    @XmlAttribute
    private String uuid;
    private static final long serialVersionUID = -8742803205412217859L;
}
