package org.marketcetera.fix.impl;

import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.MutableActiveFixSession;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Provides a POJO {@link MutableActiveFixSession} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleActiveFixSession
        extends SimpleFixSession
        implements MutableActiveFixSession
{
    /**
     * Create a new SimpleActiveFixSession instance.
     *
     * @param inActiveFixSession an <code>ActiveFixSession</code> value
     */
    public SimpleActiveFixSession(ActiveFixSession inActiveFixSession)
    {
        setAffinity(inActiveFixSession.getAffinity());
        setBrokerId(inActiveFixSession.getBrokerId());
        setDescription(inActiveFixSession.getDescription());
        setHost(inActiveFixSession.getHost());
        setInstance(inActiveFixSession.getInstance());
        setIsAcceptor(inActiveFixSession.isAcceptor());
        setIsDeleted(inActiveFixSession.isDeleted());
        setIsEnabled(inActiveFixSession.isEnabled());
        setMappedBrokerId(inActiveFixSession.getMappedBrokerId());
        setName(inActiveFixSession.getName());
        setPort(inActiveFixSession.getPort());
        setSenderSequenceNumber(inActiveFixSession.getSenderSequenceNumber());
        setSessionId(inActiveFixSession.getSessionId());
        setSessionSettings(Maps.newHashMap(inActiveFixSession.getSessionSettings()));
        setStatus(inActiveFixSession.getStatus());
        setTargetSequenceNumber(inActiveFixSession.getTargetSequenceNumber());
    }
    /**
     * Create a new SimpleActiveFixSession instance.
     */
    public SimpleActiveFixSession() {}
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getInstance()
     */
    @Override
    public String getInstance()
    {
        return instance;
    }
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
     * @see org.marketcetera.fix.MutableActiveFixSession#setInstance(java.lang.String)
     */
    @Override
    public void setInstance(String inInstance)
    {
        instance = inInstance;
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleActiveFixSession [instance=").append(instance).append(", targetSequenceNumber=")
                .append(targetSequenceNumber).append(", senderSequenceNumber=").append(senderSequenceNumber)
                .append(", fixSessionStatus=").append(fixSessionStatus).append(", getAffinity()=").append(getAffinity())
                .append(", getBrokerId()=").append(getBrokerId()).append(", getMappedBrokerId()=")
                .append(getMappedBrokerId()).append(", getSessionId()=").append(getSessionId())
                .append(", isAcceptor()=").append(isAcceptor()).append(", isEnabled()=").append(isEnabled())
                .append(", isDeleted()=").append(isDeleted()).append(", getPort()=").append(getPort())
                .append(", getHost()=").append(getHost()).append(", getSessionSettings()=").append(getSessionSettings())
                .append(", getName()=").append(getName()).append(", getDescription()=").append(getDescription())
                .append("]");
        return builder.toString();
    }
    /**
     * instance value
     */
    private String instance;
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
