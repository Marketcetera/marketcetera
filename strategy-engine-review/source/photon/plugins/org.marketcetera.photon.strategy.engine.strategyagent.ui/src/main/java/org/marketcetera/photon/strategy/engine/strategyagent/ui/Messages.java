package org.marketcetera.photon.strategy.engine.strategyagent.ui;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage0P;
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

    static I18NMessage0P NEW_STRATEGY_AGENT_WIZARD__TITLE;
    static I18NMessage0P NEW_STRATEGY_AGENT_WIZARD_ADD_ENGINE_FAILED;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
