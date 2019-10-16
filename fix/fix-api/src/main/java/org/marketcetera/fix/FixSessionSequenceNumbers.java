package org.marketcetera.fix;

import java.io.Serializable;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionSequenceNumbers
        implements Serializable
{
    /**
     * Get the nextSenderSeqNum value.
     *
     * @return an <code>int</code> value
     */
    public int getNextSenderSeqNum()
    {
        return nextSenderSeqNum;
    }
    /**
     * Sets the nextSenderSeqNum value.
     *
     * @param inNextSenderSeqNum an <code>int</code> value
     */
    public void setNextSenderSeqNum(int inNextSenderSeqNum)
    {
        nextSenderSeqNum = inNextSenderSeqNum;
    }
    /**
     * Get the nextTargetSeqNum value.
     *
     * @return an <code>int</code> value
     */
    public int getNextTargetSeqNum()
    {
        return nextTargetSeqNum;
    }
    /**
     * Sets the nextTargetSeqNum value.
     *
     * @param inNextTargetSeqNum an <code>int</code> value
     */
    public void setNextTargetSeqNum(int inNextTargetSeqNum)
    {
        nextTargetSeqNum = inNextTargetSeqNum;
    }
    /**
     * Get the sessionId value.
     *
     * @return a <code>quickfix.SessionID</code> value
     */
    public quickfix.SessionID getSessionId()
    {
        return sessionId;
    }
    /**
     * Sets the sessionId value.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     */
    public void setSessionId(quickfix.SessionID inSessionId)
    {
        sessionId = inSessionId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FixSessionSequenceNumbers [sessionId=").append(sessionId).append(", nextSenderSeqNum=")
                .append(nextSenderSeqNum).append(", nextTargetSeqNum=").append(nextTargetSeqNum).append("]");
        return builder.toString();
    }
    /**
     * Create a new FixSessionSequenceNumbers instance.
     * 
     * @param inSession a <code>quickfix.Session</code> value 
     */
    public FixSessionSequenceNumbers(quickfix.Session inSession)
    {
        nextSenderSeqNum = inSession.getExpectedSenderNum();
        nextTargetSeqNum = inSession.getExpectedTargetNum();
        sessionId = inSession.getSessionID();
    }
    /**
     * next sender seq num
     */
    private int nextSenderSeqNum;
    /**
     * next target seq num
     */
    private int nextTargetSeqNum;
    /**
     * session id value
     */
    private quickfix.SessionID sessionId;
    private static final long serialVersionUID = 7232708766552632614L;
}
