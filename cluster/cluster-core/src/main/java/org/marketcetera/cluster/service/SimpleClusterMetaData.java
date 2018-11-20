package org.marketcetera.cluster.service;

import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterWorkUnitDescriptor;
import org.marketcetera.cluster.SimpleClusterData;
import org.marketcetera.cluster.SimpleClusterWorkUnitDescriptor;

/**
 * Holds data about each cluster member.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HazelcastClusterService.java 16827 2016-05-24 14:40:08Z colin $
 * @since $Release$
 */
@XmlRootElement(name="clusterMetaData")
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleClusterMetaData
        implements Serializable,Comparable<SimpleClusterMetaData>,ClusterMetaData
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return clusterData.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(clusterData).toHashCode();
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
        if (!(obj instanceof SimpleClusterMetaData)) {
            return false;
        }
        SimpleClusterMetaData other = (SimpleClusterMetaData) obj;
        return new EqualsBuilder().append(clusterData,other.clusterData).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SimpleClusterMetaData inO)
    {
        return new CompareToBuilder().append(clusterData,inO.clusterData).toComparison();
    }
    /**
     * Get the clusterData value.
     *
     * @return a <code>ClusterData</code> value
     */
    public ClusterData getClusterData()
    {
        return clusterData;
    }
    /**
     * Get the activeWorkUnits value.
     *
     * @return a <code>SortedSet&lt;ClusterWorkUnitDescriptor&gt;</code> value
     */
    public SortedSet<? extends ClusterWorkUnitDescriptor> getActiveWorkUnits()
    {
        return activeWorkUnits;
    }
    /**
     * Create a new MetaData instance.
     *
     * @param inClusterData a <code>ClusterData</code> value
     * @param inActiveWorkUnits a <code>Set&lt;ClusterWorkUnitDescriptor&gt;</code> value
     */
    public SimpleClusterMetaData(ClusterData inClusterData,
                                 Set<ClusterWorkUnitDescriptor> inActiveWorkUnits)
    {
        if(inClusterData instanceof SimpleClusterData) {
            clusterData = (SimpleClusterData)inClusterData;
        } else {
            clusterData = new SimpleClusterData(inClusterData);
        }
        inActiveWorkUnits.stream().forEach(workUnitDescriptor->
            activeWorkUnits.add(workUnitDescriptor instanceof SimpleClusterWorkUnitDescriptor?(SimpleClusterWorkUnitDescriptor)workUnitDescriptor:new SimpleClusterWorkUnitDescriptor(workUnitDescriptor)));
    }
    /**
     * Create a new MetaData instance.
     */
    @SuppressWarnings("unused")
    private SimpleClusterMetaData()
    {
        clusterData = null;
    }
    /**
     * cluster data describing the cluster member
     */
    private final SimpleClusterData clusterData;
    /**
     * collection of currently active work unit descriptors on the cluster member
     */
    private final SortedSet<SimpleClusterWorkUnitDescriptor> activeWorkUnits = new TreeSet<>();
    private static final long serialVersionUID = -5779094662161910163L;
}
