package org.marketcetera.ors;

import java.io.Serializable;

import org.marketcetera.cluster.ClusterData;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Represents a session id generated on a particular instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class GeneratedSessionId
        implements Serializable
{
    /**
     * Create a new GeneratedSessionId instance.
     */
    public GeneratedSessionId() {}
    /**
     * Create a new GeneratedSessionId instance.
     *
     * @param inSessionId
     * @param inClusterData
     */
    public GeneratedSessionId(SessionId inSessionId,
                              ClusterData inClusterData)
    {
        sessionId = inSessionId;
        clusterData = inClusterData;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuilder().append(sessionId).append(" generated on " ).append(clusterData).toString();
    }
    /**
     * Get the sessionId value.
     *
     * @return a <code>SessionId</code> value
     */
    public SessionId getSessionId()
    {
        return sessionId;
    }
    /**
     * Sets the sessionId value.
     *
     * @param a <code>SessionId</code> value
     */
    public void setSessionId(SessionId inSessionId)
    {
        sessionId = inSessionId;
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
     * Sets the clusterData value.
     *
     * @param a <code>ClusterData</code> value
     */
    public void setClusterData(ClusterData inClusterData)
    {
        clusterData = inClusterData;
    }
    /**
     * generated session id
     */
    private SessionId sessionId;
    /**
     * instance on which this session was generated
     */
    private ClusterData clusterData;
    private static final long serialVersionUID = -874858647019306425L;
}
