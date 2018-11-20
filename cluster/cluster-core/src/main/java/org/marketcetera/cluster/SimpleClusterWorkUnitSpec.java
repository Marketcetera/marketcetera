package org.marketcetera.cluster;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/* $License$ */

/**
 * Describes a cluster work unit.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ClusterWorkUnitSpec.java 16648 2015-10-23 20:24:13Z colin $
 * @since 2.5.0
 */
public class SimpleClusterWorkUnitSpec
        implements ClusterWorkUnitSpec
{
    /**
     * Create a new ClusterWorkUnitSpec instance.
     */
    public SimpleClusterWorkUnitSpec() {}
    /**
     * Create a new ClusterWorkUnitSpec instance.
     *
     * @param inWorkUnitType a <code>ClusterWorkUnitType</code> value
     * @param inWorkUnitId a <code>String</code> value
     * @param inWorkUnitUid a <code>String</code> value
     */
    public SimpleClusterWorkUnitSpec(ClusterWorkUnitType inWorkUnitType,
                               String inWorkUnitId,
                               String inWorkUnitUid)
    {
        workUnitId = inWorkUnitId;
        workUnitUid = inWorkUnitUid;
        workUnitType = inWorkUnitType;
    }
    /**
     * Create a new ClusterWorkUnitSpec instance.
     *
     * @param inWorkUnit a <code>ClusterWorkUnit</code> value
     */
    public SimpleClusterWorkUnitSpec(ClusterWorkUnit inWorkUnit)
    {
        workUnitId = inWorkUnit.id();
        workUnitType = inWorkUnit.type();
    }
    /**
     * Create a new SimpleClusterWorkUnitSpec instance.
     *
     * @param inWorkUnitSpec a <code>ClusterWorkUnitSpec</code> value
     */
    public SimpleClusterWorkUnitSpec(ClusterWorkUnitSpec inWorkUnitSpec)
    {
        setWorkUnitId(inWorkUnitSpec.getWorkUnitId());
        setWorkUnitType(inWorkUnitSpec.getWorkUnitType());
        setWorkUnitUid(inWorkUnitSpec.getWorkUnitUid());
    }
    /**
     * Get the workUnitId value.
     *
     * @return a <code>String</code> value
     */
    public String getWorkUnitId()
    {
        return workUnitId;
    }
    /**
     * Sets the workUnitId value.
     *
     * @param inWorkUnitId a <code>String</code> value
     */
    public void setWorkUnitId(String inWorkUnitId)
    {
        workUnitId = inWorkUnitId;
    }
    /**
     * Get the workUnitType value.
     *
     * @return a <code>ClusterWorkUnitType</code> value
     */
    public ClusterWorkUnitType getWorkUnitType()
    {
        return workUnitType;
    }
    /**
     * Sets the workUnitType value.
     *
     * @param inWorkUnitType a <code>ClusterWorkUnitType</code> value
     */
    public void setWorkUnitType(ClusterWorkUnitType inWorkUnitType)
    {
        workUnitType = inWorkUnitType;
    }
    /**
     * Get the workUnitUid value.
     *
     * @return a <code>String</code> value
     */
    public String getWorkUnitUid()
    {
        return workUnitUid;
    }
    /**
     * Sets the workUnitUid value.
     *
     * @param a <code>String</code> value
     */
    public void setWorkUnitUid(String inWorkUnitUid)
    {
        workUnitUid = inWorkUnitUid;
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ClusterWorkUnitSpec inO)
    {
        return new CompareToBuilder().append(workUnitId,inO.getWorkUnitId()).append(workUnitUid,inO.getWorkUnitUid()).toComparison();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuilder().append(workUnitId).append('-').append(workUnitUid).toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(workUnitId).append(workUnitType).append(workUnitUid).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleClusterWorkUnitSpec other = (SimpleClusterWorkUnitSpec) obj;
        return new EqualsBuilder().append(workUnitId,other.workUnitId).append(workUnitType,other.workUnitType).append(workUnitUid,other.workUnitUid).isEquals();
    }
    /**
     * uniquely identifies the work unit
     */
    private String workUnitId;
    /**
     * indicates the work unit type
     */
    private ClusterWorkUnitType workUnitType;
    /**
     * optionally identifies the UID of the work unit of a {@link ClusterWorkUnitType#SINGLETON_RUNTIME} instance
     */
    private String workUnitUid = MASTER_UID;
    private static final long serialVersionUID = 4149388804926770257L;
}
