package org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage1P;
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
    
    static I18NMessage1P CONNECT_HANDLER__TASK_NAME;
    static I18NMessage1P CONNECT_HANDLER_FAILED;
    static I18NMessage1P DISCONNECT_HANDLER__TASK_NAME;
    static I18NMessage1P DISCONNECT_HANDLER_FAILED;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
