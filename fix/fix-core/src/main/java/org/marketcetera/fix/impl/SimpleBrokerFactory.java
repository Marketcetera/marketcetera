package org.marketcetera.fix.impl;

import org.marketcetera.brokers.BrokerFactory;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.fix.FixSession;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link SimpleBroker} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class SimpleBrokerFactory
        implements BrokerFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerFactory#create(org.marketcetera.fix.FixSession, org.marketcetera.brokers.SessionCustomization)
     */
    @Override
    public SimpleBroker create(FixSession inFixSession,
                               SessionCustomization inSessionCustomization)
    {
        return new SimpleBroker(inFixSession,
                                inSessionCustomization);
    }
}
