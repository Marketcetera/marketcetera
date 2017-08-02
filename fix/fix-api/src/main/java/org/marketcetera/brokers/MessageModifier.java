package org.marketcetera.brokers;

import quickfix.Message;

/* $License$ */

/**
 * Modify a FIX message before it is sent or after it is received from or to a broker.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MessageModifier
{
    /**
     * Modify the given message targeted to the given broker.
     *
     * @param inBroker a <code>Broker</code> value
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
     */
    boolean modify(Broker inBroker,
                   Message inMessage);
}
