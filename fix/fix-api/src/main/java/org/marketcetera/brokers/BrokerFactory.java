package org.marketcetera.brokers;

import org.marketcetera.fix.FixSession;

/* $License$ */

/**
 * Creates {@link Broker} objects.
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
     * @param inSessionCustomization a <code>SessionCustomization</code> value
     * @return a <code>Broker</code> value
     */
    Broker create(FixSession inFixSession,
                  SessionCustomization inSessionCustomization);
}
