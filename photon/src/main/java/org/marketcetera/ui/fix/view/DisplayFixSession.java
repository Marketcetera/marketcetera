package org.marketcetera.ui.fix.view;

import java.util.Objects;

import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSessionStatus;

/* $License$ */

/**
 * Provides a display POJO version of an <code>ActiveFixSession</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayFixSession
{
    /**
     * Create a new DisplayFixSession instance.
     */
    public DisplayFixSession() {}
    /**
     * Create a new DisplayFixSession instance.
     *
     * @param inFixSession an <code>ActiveFixSession</code> value
     */
    public DisplayFixSession(ActiveFixSession inFixSession)
    {
        name = inFixSession.getFixSession().getName();
        description = inFixSession.getFixSession().getDescription();
        sessionId = inFixSession.getFixSession().getSessionId();
        brokerId = inFixSession.getFixSession().getBrokerId();
        hostId = inFixSession.getClusterData().toString();
        status = inFixSession.getStatus();
        targetSeqNum = inFixSession.getTargetSequenceNumber();
        senderSeqNum = inFixSession.getSenderSequenceNumber();
        source = inFixSession;
    }
    /**
     * Get the source value.
     *
     * @return an <code>ActiveFixSession</code> value
     */
    public ActiveFixSession getSource()
    {
        return source;
    }
    /**
     * Sets the source value.
     *
     * @param inSource an <code>ActiveFixSession</code> value
     */
    public void setSource(ActiveFixSession inSource)
    {
        source = inSource;
    }
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * Get the sessionId value.
     *
     * @return a <code>String</code> value
     */
    public String getSessionId()
    {
        return sessionId;
    }
    /**
     * Sets the sessionId value.
     *
     * @param inSessionId a <code>String</code> value
     */
    public void setSessionId(String inSessionId)
    {
        sessionId = inSessionId;
    }
    /**
     * Get the brokerId value.
     *
     * @return a <code>String</code> value
     */
    public String getBrokerId()
    {
        return brokerId;
    }
    /**
     * Sets the brokerId value.
     *
     * @param inBrokerId a <code>String</code> value
     */
    public void setBrokerId(String inBrokerId)
    {
        brokerId = inBrokerId;
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
     * Get the status value.
     *
     * @return a <code>FixSessionStatus</code> value
     */
    public FixSessionStatus getStatus()
    {
        return status;
    }
    /**
     * Sets the status value.
     *
     * @param inStatus a <code>FixSessionStatus</code> value
     */
    public void setStatus(FixSessionStatus inStatus)
    {
        status = inStatus;
    }
    /**
     * Get the targetSeqNum value.
     *
     * @return an <code>int</code> value
     */
    public int getTargetSeqNum()
    {
        return targetSeqNum;
    }
    /**
     * Sets the targetSeqNum value.
     *
     * @param inTargetSeqNum an <code>int</code> value
     */
    public void setTargetSeqNum(int inTargetSeqNum)
    {
        targetSeqNum = inTargetSeqNum;
    }
    /**
     * Get the senderSeqNum value.
     *
     * @return an <code>int</code> value
     */
    public int getSenderSeqNum()
    {
        return senderSeqNum;
    }
    /**
     * Sets the senderSeqNum value.
     *
     * @param inSenderSeqNum an <code>int</code> value
     */
    public void setSenderSeqNum(int inSenderSeqNum)
    {
        senderSeqNum = inSenderSeqNum;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(brokerId,
                            description,
                            hostId,
                            name,
                            senderSeqNum,
                            sessionId,
                            status,
                            targetSeqNum);
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
        if (!(obj instanceof DisplayFixSession)) {
            return false;
        }
        DisplayFixSession other = (DisplayFixSession) obj;
        return Objects.equals(brokerId,
                              other.brokerId)
                && Objects.equals(description,
                                  other.description)
                && Objects.equals(hostId,
                                  other.hostId)
                && Objects.equals(name,
                                  other.name)
                && senderSeqNum == other.senderSeqNum && Objects.equals(sessionId,
                                                                        other.sessionId)
                && status == other.status && targetSeqNum == other.targetSeqNum;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DisplayFixSession [name=").append(name).append(", description=").append(description)
                .append(", sessionId=").append(sessionId).append(", brokerId=").append(brokerId).append(", hostId=")
                .append(hostId).append(", status=").append(status).append(", targetSeqNum=").append(targetSeqNum)
                .append(", senderSeqNum=").append(senderSeqNum).append("]");
        return builder.toString();
    }
    private String name;
    private String description;
    private String sessionId;
    private String brokerId;
    private String hostId;
    private FixSessionStatus status;
    private int targetSeqNum;
    private int senderSeqNum;
    private ActiveFixSession source;
}
