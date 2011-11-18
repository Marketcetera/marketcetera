package org.marketcetera.ors.config;

import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handles logout actions to a broker.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
@ClassVersion("$Id$")
public interface LogoutAction
{
    /**
     * Called upon logout from the given broker. 
     *
     * @param inBroker a <code>Broker</code> value
     * @param inSender an <code>IQuickFIXSender</code> value
     */
    public void onLogout(Broker inBroker,
                         IQuickFIXSender inSender);
}
