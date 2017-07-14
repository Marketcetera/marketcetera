package com.marketcetera.fix;

import java.io.Serializable;

import quickfix.SessionID;

/* $License$ */

/**
 * Contains attributes of a FIX session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionAttributes
        implements Serializable
{
    /**
     * Create a new FixSessionAttributes instance.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inNextSenderSeqNum an <code>int</code> value
     * @param inNextTargetSeqNum an <code>int</code> value
     * @param inAcceptorPort an <code>int</code> value
     */
    public FixSessionAttributes(SessionID inSessionId,
                                int inNextSenderSeqNum,
                                int inNextTargetSeqNum,
                                int inAcceptorPort)
    {
        sessionId = inSessionId;
        nextSenderSeqNum = inNextSenderSeqNum;
        nextTargetSeqNum = inNextTargetSeqNum;
        acceptorPort = inAcceptorPort;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FixSessionAttributes [sessionId=").append(sessionId).append(", nextSenderSeqNum=")
                .append(nextSenderSeqNum).append(", nextTargetSeqNum=").append(nextTargetSeqNum)
                .append(", acceptorPort=").append(acceptorPort).append("]");
        return builder.toString();
    }
    /**
     * Get the acceptorPort value.
     *
     * @return an <code>int</code> value
     */
    public int getAcceptorPort()
    {
        return acceptorPort;
    }
    /**
     * Sets the acceptorPort value.
     *
     * @param inAcceptorPort an <code>int</code> value
     */
    public void setAcceptorPort(int inAcceptorPort)
    {
        acceptorPort = inAcceptorPort;
    }
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
     * @return a <code>SessionID</code> value
     */
    public SessionID getSessionId()
    {
        return sessionId;
    }
    /**
     * session id value
     */
    private SessionID sessionId;
    /**
     * next sender sequence number value
     */
    private int nextSenderSeqNum;
    /**
     * next target sequence number value
     */
    private int nextTargetSeqNum;
    /**
     * acceptor port value
     */
    private int acceptorPort;
    public static final String fixSessionAttributesKey = FixSessionAttributes.class.getSimpleName()+"-fixSessionAttributes";
    private static final long serialVersionUID = -6016789099143714659L;
}
