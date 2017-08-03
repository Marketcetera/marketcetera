package org.marketcetera.fix.impl;

import java.util.Collection;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.brokers.BrokerFactory;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.fix.FixSession;

/* $License$ */

/**
 * Creates {@link SimpleBroker} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleBrokerFactory
        implements BrokerFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerFactory#create(org.marketcetera.fix.FixSession, java.util.Collection, java.util.Collection, java.util.Collection)
     */
    @Override
    public SimpleBroker create(FixSession inFixSession,
                               Collection<MessageModifier> inOrderModifiers,
                               Collection<MessageModifier> inResponseModifiers,
                               Collection<BrokerAlgoSpec> inBrokerAlgos)
    {
        return new SimpleBroker(inFixSession,
                                inOrderModifiers,
                                inResponseModifiers,
                                inBrokerAlgos);
    }
}
