package org.marketcetera.photon.internal.strategy.engine.strategyagent;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
final class Messages {
    
    static I18NMessage2P STRATEGY_AGENT_CONNECTION_UPDATE_ROUTING_ERROR;
    static I18NMessage2P STRATEGY_AGENT_CONNECTION_UPDATE_PARAMETERS_ERROR;
    static I18NMessage1P INTERNAL_STRATEGY_AGENT_ENGINE_DISCONNECT_ON_LOGOUT_FAILED;
    
    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
