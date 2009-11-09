package org.marketcetera.photon.internal.strategy.engine.embedded;

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
    
    static I18NMessage0P EMBEDDED_ENGINE_IMPL__NAME;
    static I18NMessage0P EMBEDDED_ENGINE_IMPL__DESCRIPTION;
    static I18NMessage0P EMBEDDED_CONNECTION_SAVE_FAILED;
    static I18NMessage1P PERSISTENCE_SERVICE_UNEXPECTED_OBJECT;
    static I18NMessage0P PERSISTENCE_SERVICE_NO_FILE;
    static I18NMessage0P PERSISTENCE_SERVICE_RESTORE_FAILED;
    static I18NMessage1P PERSISTENCE_SERVICE_DEPLOY_FAILED;
    static I18NMessage1P PERSISTENCE_SERVICE_IGNORED_STRATEGY_WITH_NO_SCRIPT_PATH;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
