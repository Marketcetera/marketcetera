package org.marketcetera.brokers;

import java.util.Collection;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.fix.FixSession;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BrokerFactory
{
    /**
     * Create a <code>Broker</code> object.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inOrderModifiers a <code>Collection&lt;MessageModifier&gt;</code> value
     * @param inResponseModifiers a <code>Collection&lt;MessageModifier&gt;</code> value
     * @param inBrokerAlgos a <code>Collection&lt;BrokerAlgoSpec&gt;</code> value
     * @return
     */
    Broker create(FixSession inFixSession,
                  Collection<MessageModifier> inOrderModifiers,
                  Collection<MessageModifier> inResponseModifiers,
                  Collection<BrokerAlgoSpec> inBrokerAlgos);
}
