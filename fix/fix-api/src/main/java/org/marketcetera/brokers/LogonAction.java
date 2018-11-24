package org.marketcetera.brokers;

import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.QuickFIXSender;

/* $License$ */

/**
 * Handles logon actions to a broker.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: LogonAction.java 16468 2014-05-12 00:36:56Z colin $
 * @since 2.1.4
 */
public interface LogonAction
{
    /**
     * Called upon successful logon to the given broker. 
     *
     * @param inFixSession a <code>ServerFixSession</code> value
     * @param inSender a <code>QuickFIXSender</code> value
     */
    public void onLogon(ServerFixSession inFixSession,
                        QuickFIXSender inSender);
}
