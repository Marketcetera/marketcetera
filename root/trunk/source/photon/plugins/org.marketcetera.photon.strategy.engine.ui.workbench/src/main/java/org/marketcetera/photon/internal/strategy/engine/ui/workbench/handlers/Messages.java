package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
final class Messages {
    
    static I18NMessage2P UNDEPLOY_HANDLER__TASK_NAME;
    static I18NMessage0P UNDEPLOY_HANDLER_FAILED;
    static I18NMessage2P START_HANDLER__TASK_NAME;
    static I18NMessage0P START_HANDLER_FAILED;
    static I18NMessage2P STOP_HANDLER__TASK_NAME;
    static I18NMessage0P STOP_HANDLER_FAILED;
    static I18NMessage1P REFRESH_HANDLER_REFRESH_ENGINE__TASK_NAME;
    static I18NMessage2P REFRESH_HANDLER_REFRESH_STRATEGY__TASK_NAME;
    static I18NMessage0P REFRESH_HANDLER_FAILED;
    static I18NMessage0P RESTART_HANDLER_FAILED;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
