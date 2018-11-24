package org.marketcetera.brokers;

import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.QuickFIXSender;

/* $License$ */

/**
 * Handles logout actions to a broker.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: LogoutAction.java 16468 2014-05-12 00:36:56Z colin $
 * @since 2.1.4
 */
public interface LogoutAction
{
    /**
     * Called upon logout from the given broker. 
     *
     * @param inFixSession a <code>ServerFixSession</code> value
     * @param inSender a <code>QuickFIXSender</code> value
     */
    public void onLogout(ServerFixSession FixSession,
                         QuickFIXSender inSender);
}
