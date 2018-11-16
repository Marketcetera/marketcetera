package org.marketcetera.fix;

/* $License$ */

/**
 * Provides a connection to a session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SessionConnector.java 85164 2016-03-03 21:27:19Z colin $
 * @since 1.0.1
 */
public interface SessionConnector
{
    /**
     * Resets connection value.
     */
    void resetConnection();
    /**
     * Disconnects the connection value.
     */
    void disconnectConnection();
    /**
     * Resets the sequence number value.
     */
    void resetSequenceNumber();
    /**
     * Sets the next sender sequence number value.
     *
     * @param inNextSequenceNumber an <code>int</code> value
     */
    void setNextSenderSequenceNumber(int inNextSequenceNumber);
    /**
     * Gets the next sender sequence number value.
     *
     * @return an <code>int</code> value
     */
    int getNextSenderSequenceNumber();
    /**
     * Sets the next target sequence number value;
     *
     * @param inNextSequenceNumber an <code>int</code> value
     */
    void setNextTargetSequenceNumber(int inNextSequenceNumber);
    /**
     * Gets the next target sequence number value.
     *
     * @return an <code>int</code> value
     */
    int getNextTargetSequenceNumber();
    /**
     * Gets the actual port on which the acceptor session is listening.
     *
     * @return an <code>int</code> value
     */
    int getAcceptorPort();
}
