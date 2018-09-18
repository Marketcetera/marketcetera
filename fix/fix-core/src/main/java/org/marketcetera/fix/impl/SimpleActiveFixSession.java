package org.marketcetera.fix.impl;

import java.io.Serializable;

import org.marketcetera.cluster.ClusterData;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.MutableActiveFixSession;

/* $License$ */

/**
 * Provides a POJO {@link MutableActiveFixSession} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleActiveFixSession
        implements MutableActiveFixSession,Serializable
{
    /**
     * Create a new SimpleActiveFixSession instance.
     *
     * @param inActiveFixSession an <code>ActiveFixSession</code> value
     */
    public SimpleActiveFixSession(ActiveFixSession inActiveFixSession)
    {
        setClusterData(inActiveFixSession.getClusterData());
        setSenderSequenceNumber(inActiveFixSession.getSenderSequenceNumber());
        setStatus(inActiveFixSession.getStatus());
        setTargetSequenceNumber(inActiveFixSession.getTargetSequenceNumber());
    }
    /**
     * Create a new SimpleActiveFixSession instance.
     */
    public SimpleActiveFixSession() {}
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getTargetSequenceNumber()
     */
    @Override
    public int getTargetSequenceNumber()
    {
        return targetSequenceNumber;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getSenderSequenceNumber()
     */
    @Override
    public int getSenderSequenceNumber()
    {
        return senderSequenceNumber;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getStatus()
     */
    @Override
    public FixSessionStatus getStatus()
    {
        return fixSessionStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setTargetSequenceNumber(int)
     */
    @Override
    public void setTargetSequenceNumber(int inTargetSequenceNumber)
    {
        targetSequenceNumber = inTargetSequenceNumber;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setSenderSequenceNumber(int)
     */
    @Override
    public void setSenderSequenceNumber(int inSenderSequenceNumber)
    {
        senderSequenceNumber = inSenderSequenceNumber;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setStatus(org.marketcetera.fix.FixSessionStatus)
     */
    @Override
    public void setStatus(FixSessionStatus inFixSessionStatus)
    {
        fixSessionStatus = inFixSessionStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getClusterData()
     */
    @Override
    public ClusterData getClusterData()
    {
        return clusterData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getFixSession()
     */
    @Override
    public FixSession getFixSession()
    {
        return fixSession;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.HasMutableView#getMutableView()
     */
    @Override
    public MutableActiveFixSession getMutableView()
    {
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setFixSession(org.marketcetera.fix.FixSession)
     */
    @Override
    public void setFixSession(FixSession inFixSession)
    {
        fixSession = inFixSession;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setClusterData(org.marketcetera.cluster.ClusterData)
     */
    @Override
    public void setClusterData(ClusterData inClusterData)
    {
        clusterData = inClusterData;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleActiveFixSession [fixSession=").append(fixSession).append(", clusterData=")
                .append(clusterData).append(", targetSequenceNumber=").append(targetSequenceNumber)
                .append(", senderSequenceNumber=").append(senderSequenceNumber).append(", fixSessionStatus=")
                .append(fixSessionStatus).append("]");
        return builder.toString();
    }
    /**
     * FIX session value
     */
    private FixSession fixSession;
    /**
     * cluster data value
     */
    private ClusterData clusterData;
    /**
     * target sequence number value
     */
    private int targetSequenceNumber;
    /**
     * sender sequence number value
     */
    private int senderSequenceNumber;
    /**
     * fix session status value
     */
    private FixSessionStatus fixSessionStatus;
    private static final long serialVersionUID = 2114962365315641093L;
}
