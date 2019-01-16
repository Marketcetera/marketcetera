package org.marketcetera.core.fix;

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
     * Get the acceptor host value.
     *
     * @return a <code>String</code> value
     */
    String getAcceptorHost();
    /**
     * Get the acceptor protocol value.
     *
     * @return a <code>String</code> value
     */
    String getAcceptorProtocol();
}
