package org.marketcetera.fix;

import java.io.Serializable;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.core.Util;

import quickfix.SessionID;

/* $License$ */

/**
 * Contains attributes of a FIX session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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
        sessionId = inSessionId.toString();
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
        return new SessionID(sessionId);
    }
    /**
     * Get the object rendered as a string.
     *
     * @return a <code>String</code> value
     */
    public String getAsString()
    {
        Properties properties = new Properties();
        properties.setProperty("sessionId",
                               sessionId.toString());
        properties.setProperty("nextSenderSeqNum",
                               String.valueOf(nextSenderSeqNum));
        properties.setProperty("nextTargetSeqNum",
                               String.valueOf(nextTargetSeqNum));
        properties.setProperty("acceptorPort",
                               String.valueOf(acceptorPort));
        return Util.propertiesToString(properties);
    }
    /**
     * Generate a <code>FixSessionAttributes</code> value from the given string.
     *
     * @param inValue a <code>String</code> value
     * @return a <code>FixSessionAttributes</code> value
     */
    public static FixSessionAttributes getFromString(String inValue)
    {
        Properties properties = Util.propertiesFromString(inValue);
        SessionID sessionId = new SessionID(properties.getProperty("sessionId"));
        int nextSenderSeqNum = Integer.parseInt(properties.getProperty("nextSenderSeqNum"));
        int nextTargetSeqNum = Integer.parseInt(properties.getProperty("nextTargetSeqNum"));
        int acceptorPort = Integer.parseInt(properties.getProperty("acceptorPort"));
        return new FixSessionAttributes(sessionId,
                                        nextSenderSeqNum,
                                        nextTargetSeqNum,
                                        acceptorPort);
    }
    /**
     * session id value
     */
    private String sessionId;
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
