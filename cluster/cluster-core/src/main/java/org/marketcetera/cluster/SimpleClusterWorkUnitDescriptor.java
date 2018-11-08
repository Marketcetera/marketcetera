package org.marketcetera.cluster;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/* $License$ */

/**
 * Describes a cluster work unit.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ClusterWorkUnitDescriptor.java 16648 2015-10-23 20:24:13Z colin $
 * @since 2.5.0
 */
@XmlRootElement(name="clusterWorkUnitDescriptor")
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleClusterWorkUnitDescriptor
        implements ClusterWorkUnitDescriptor, Comparable<SimpleClusterWorkUnitDescriptor>
{
    /**
     * Create a new ClusterWorkUnitDescriptor instance.
     *
     * @param inWorkUnitSpec a <code>ClusterWorkUnitSpec</code> value
     * @param inWorkUnitMember a <code>String</code> value
     */
    public SimpleClusterWorkUnitDescriptor(ClusterWorkUnitSpec inWorkUnitSpec,
                                           String inWorkUnitMember)
    {
        setWorkUnitSpec(inWorkUnitSpec);
        setWorkUnitMember(inWorkUnitMember);
    }
    /**
     * Create a new ClusterWorkUnitDescriptor instance.
     */
    public SimpleClusterWorkUnitDescriptor() {}
    /**
     * Create a new SimpleClusterWorkUnitDescriptor instance.
     *
     * @param inWorkUnitDescriptor a <code>ClusterWorkUnitDescriptor</code> value
     */
    public SimpleClusterWorkUnitDescriptor(ClusterWorkUnitDescriptor inWorkUnitDescriptor)
    {
        setTimestamp(inWorkUnitDescriptor.getTimestamp());
        setWorkUnitMember(inWorkUnitDescriptor.getWorkUnitMember());
        setWorkUnitSpec(inWorkUnitDescriptor.getWorkUnitSpec());
    }
    /**
     * Get the workUnitSpec value.
     *
     * @return a <code>ClusterWorkUnitSpec</code> value
     */
    public ClusterWorkUnitSpec getWorkUnitSpec()
    {
        return workUnitSpec;
    }
    /**
     * Sets the workUnitSpec value.
     *
     * @param inWorkUnitSpec a <code>ClusterWorkUnitSpec</code> value
     */
    public void setWorkUnitSpec(ClusterWorkUnitSpec inWorkUnitSpec)
    {
        if(inWorkUnitSpec instanceof SimpleClusterWorkUnitSpec) {
            workUnitSpec = (SimpleClusterWorkUnitSpec)inWorkUnitSpec;
        } else {
            workUnitSpec = new SimpleClusterWorkUnitSpec(inWorkUnitSpec);
        }
    }
    /**
     * Get the workUnitMember value.
     *
     * @return a <code>String</code> value
     */
    public String getWorkUnitMember()
    {
        return workUnitMember;
    }
    /**
     * Sets the workUnitMember value.
     *
     * @param inWorkUnitMember a <code>String</code> value
     */
    public void setWorkUnitMember(String inWorkUnitMember)
    {
        workUnitMember = inWorkUnitMember;
    }
    /**
     * Get the timestamp value.
     *
     * @return a <code>long</code> value
     */
    public long getTimestamp()
    {
        return timestamp;
    }
    /**
     * Sets the timestamp value.
     *
     * @param inTimestamp a <code>long</code> value
     */
    public void setTimestamp(long inTimestamp)
    {
        timestamp = inTimestamp;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return workUnitSpec.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SimpleClusterWorkUnitDescriptor inO)
    {
        return workUnitSpec.compareTo(inO.workUnitSpec);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(workUnitMember).append(workUnitSpec).toHashCode();
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
        SimpleClusterWorkUnitDescriptor other = (SimpleClusterWorkUnitDescriptor) obj;
        return new EqualsBuilder().append(workUnitSpec,other.workUnitSpec).append(workUnitMember,other.workUnitMember).isEquals();
    }
    /**
     * work unit specification
     */
    private SimpleClusterWorkUnitSpec workUnitSpec;
    /**
     * member on which the work unit is active
     */
    private String workUnitMember;
    /**
     * last time this work unit's active state was verified
     */
    private long timestamp;
    private static final long serialVersionUID = 803281777079385901L;
}
