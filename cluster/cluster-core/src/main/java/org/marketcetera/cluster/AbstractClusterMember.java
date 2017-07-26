package org.marketcetera.cluster;

import org.marketcetera.cluster.service.ClusterMember;

/* $License$ */

/**
 * Identifies a cluster member.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractClusterMember
        implements ClusterMember
{
    /**
     * Create a new HazelcastClusterMember instance.
     *
     * @param inUuid a <code>String</code> value
     */
    public AbstractClusterMember(String inUuid)
    {
        uuid = inUuid;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterMember#getUuid()
     */
    @Override
    public String getUuid()
    {
        return uuid;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ClusterMember [").append(uuid).append("]");
        return builder.toString();
    }
    /**
     * uniquely identifies a cluster member
     */
    private final String uuid;
}
