package org.marketcetera.fix;

import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides static FIX session attributes.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSettingsProvider
{
    /**
     * Get the log factory value.
     *
     * @param inSessionSettings a <code>SessionSettings</code> value
     * @return a <code>LogFactory</code> value
     */
    LogFactory getLogFactory(SessionSettings inSessionSettings);
    /**
     * Get the message factory value.
     *
     * @return a <code>MessageFactory</code> value
     */
    MessageFactory getMessageFactory();
    /**
     * Get the message store factory value.
     *
     * @param inSessionSettings a <code>SessionSettings</code> value
     * @return a <code>MessageStoreFactory</code> value
     */
    MessageStoreFactory getMessageStoreFactory(SessionSettings inSessionSettings);
    /**
     * Get the acceptor port value.
     *
     * @return an <code>int</code> value
     */
    int getAcceptorPort();
    /**
     * Set the acceptor port value.
     *
     * @param inAcceptorPort an <code>int</code> value
     */
    void setAcceptorPort(int inAcceptorPort);
    /**
     * Get the acceptor host value.
     *
     * @return a <code>String</code> value
     */
    String getAcceptorHost();
    /**
     * Set the acceptor host value.
     *
     * @param inAcceptorHost a <code>String</code> value
     */
    void setAcceptorHost(String inAcceptorHost);
    /**
     * Get the acceptor protocol value.
     *
     * @return a <code>String</code> value
     */
    String getAcceptorProtocol();
    /**
     * Set the acceptor protocol value.
     *
     * @param inAcceptorProtocol a <code>String</code> value
     */
    void setAcceptorProtocol(String inAcceptorProtocol);
}
