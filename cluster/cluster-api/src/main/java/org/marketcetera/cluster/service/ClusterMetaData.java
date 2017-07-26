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

/**
 * Holds data about each cluster member.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HazelcastClusterService.java 16827 2016-05-24 14:40:08Z colin $
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterMetaData
        implements Serializable,Comparable<ClusterMetaData>
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
        if (!(obj instanceof ClusterMetaData)) {
            return false;
        }
        ClusterMetaData other = (ClusterMetaData) obj;
        return new EqualsBuilder().append(clusterData,other.clusterData).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ClusterMetaData inO)
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
     * @return a <code>SortedSet<ClusterWorkUnitDescriptor></code> value
     */
    public SortedSet<ClusterWorkUnitDescriptor> getActiveWorkUnits()
    {
        return activeWorkUnits;
    }
    /**
     * Create a new MetaData instance.
     *
     * @param inClusterData a <code>ClusterData</code> value
     * @param inActiveWorkUnits a <code>Set&lt;ClusterWorkUnitDescriptor&gt;</code> value
     */
    public ClusterMetaData(ClusterData inClusterData,
                    Set<ClusterWorkUnitDescriptor> inActiveWorkUnits)
    {
        clusterData = inClusterData;
        activeWorkUnits.addAll(inActiveWorkUnits);
    }
    /**
     * Create a new MetaData instance.
     */
    @SuppressWarnings("unused")
    private ClusterMetaData()
    {
        clusterData = null;
    }
    /**
     * cluster data describing the cluster member
     */
    private final ClusterData clusterData;
    /**
     * collection of currently active work unit descriptors on the cluster member
     */
    private final SortedSet<ClusterWorkUnitDescriptor> activeWorkUnits = new TreeSet<>();
    private static final long serialVersionUID = -5779094662161910163L;
}