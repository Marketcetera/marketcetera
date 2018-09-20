package org.marketcetera.cluster;

/* $License$ */

/**
 * Creates {@link SimpleClusterData} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleClusterDataFactory
        implements MutableClusterDataFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.DomainObjectFactory#create(org.marketcetera.core.DomainObject)
     */
    @Override
    public SimpleClusterData create(ClusterData inClusterData)
    {
        return new SimpleClusterData(inClusterData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.MutableDomainObjectFactory#create()
     */
    @Override
    public SimpleClusterData create()
    {
        return new SimpleClusterData();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.cluster.ClusterDataFactory#create(int, java.lang.String, int, int, java.lang.String)
     */
    @Override
    public SimpleClusterData create(int inTotalInstances,
                                    String inHostId,
                                    int inHostNumber,
                                    int inInstanceNumber,
                                    String inMemberUuid)
    {
        return new SimpleClusterData(inTotalInstances,
                                     inHostId,
                                     inHostNumber,
                                     inInstanceNumber,
                                     inMemberUuid);
    }
}
