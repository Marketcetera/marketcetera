package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage0P;
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
    /*
     * UI Text
     */
    static I18NMessage1P UNDEPLOY_HANDLER__TASK_NAME;
    static I18NMessage1P UNDEPLOY_HANDLER_FAILED;
    static I18NMessage1P START_HANDLER__TASK_NAME;
    static I18NMessage1P START_HANDLER_FAILED;
    static I18NMessage1P STOP_HANDLER__TASK_NAME;
    static I18NMessage1P STOP_HANDLER_FAILED;
    static I18NMessage1P REFRESH_HANDLER_REFRESH_ENGINE__TASK_NAME;
    static I18NMessage1P REFRESH_HANDLER_REFRESH_STRATEGY__TASK_NAME;
    static I18NMessage0P REFRESH_HANDLER_FAILED;
    static I18NMessage1P START_ALL_HANDLER__TASK_NAME;
    static I18NMessage0P START_ALL_HANDLER_FAILED;
    static I18NMessage1P STOP_ALL_HANDLER__TASK_NAME;
    static I18NMessage0P STOP_ALL_HANDLER_FAILED;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
