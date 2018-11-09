package org.marketcetera.ors.config;

import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.util.misc.ClassVersion;


/* $License$ */

/**
 * Handles logon actions to a broker.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: LogonAction.java 16468 2014-05-12 00:36:56Z colin $
 * @since 2.1.4
 */
@ClassVersion("$Id: LogonAction.java 16468 2014-05-12 00:36:56Z colin $")
public interface LogonAction
{
    /**
     * Called upon successful logon to the given broker. 
     *
     * @param inBroker a <code>Broker</code> value
     * @param inSender an <code>IQuickFIXSender</code> value
     */
    public void onLogon(Broker inBroker,
                        QuickFIXSender inSender);
}
