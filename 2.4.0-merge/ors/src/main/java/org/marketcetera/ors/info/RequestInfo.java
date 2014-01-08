package org.marketcetera.ors.info;

import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Originator;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/**
 * A store for key-value pairs specific to a request.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface RequestInfo
    extends ReadWriteInfo
{

    /**
     * The {@link BrokerID} key for the broker associated with (as
     * recipient or originator of) the request message.
     */

    static final String BROKER_ID=
        "BROKER_ID"; //$NON-NLS-1$

    /**
     * The {@link Broker} key for the broker associated with (as
     * recipient or originator of) the request message.
     */

    static final String BROKER=
        "BROKER"; //$NON-NLS-1$

    /**
     * The {@link Originator} key for the request message.
     */

    static final String ORIGINATOR=
        "ORIGINATOR"; //$NON-NLS-1$

    /**
     * The {@link FIXMessageFactory} key for the broker associated
     * with (as recipient or originator of) the request message.
     */

    static final String FIX_MESSAGE_FACTORY=
        "FIX_MESSAGE_FACTORY"; //$NON-NLS-1$

    /**
     * The {@link Message} key for the request message as it currently
     * stands (as it is being subjected to modifications).
     */

    static final String CURRENT_MESSAGE=
        "CURRENT_MESSAGE"; //$NON-NLS-1$

    /**
     * Returns the receiver's session store.
     *
     * @return The session store.
     */

    SessionInfo getSessionInfo();
}
