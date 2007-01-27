/*
 * Created by IntelliJ IDEA.
 * User: toli
 * Date: Dec 22, 2006
 * Time: 4:17:21 PM
 */
package org.marketcetera.oms;

import java.io.IOException;

/** This is intended to be a stand-in for future QuickfixJ JMX functionality
 * @deprecated
 */

public interface OMSAdminMBean {

    /**
     * Get the begin string (FIX version) for the session
     *
     * @return the begin string for the session
     */
    String getBeginString();

    /**
     * Get the target company ID for the session.
     *
     * @return the target company ID
     */
    String getTargetCompID();

    /**
     * Get the sender company ID for the session.
     *
     * @return the sender company ID
     */
    String getSenderCompID();

    /**
     * Get the session ID.
     * @return the session ID
     */
    String getSessionID();

    /**
     * Get the next sender message sequence number.
     *
     * @return the next sender message sequence number
     * @throws java.io.IOException
     */
    int getNextSenderMsgSeqNum() throws IOException;

    /**
     * Get the next sender message sequence number. 
     *
     * @throws IOException
     */
    public int getNextTargetMsgSeqNum() throws IOException;

    /** whetheer or not we have logged on to the counterparty fix server */
    boolean isLoggedOn();


}