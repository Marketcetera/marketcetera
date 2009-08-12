package org.marketcetera.photon.strategy.engine.ui;

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
    static I18NMessage0P DEPLOY_STRATEGY_WIZARD_PAGE__TITLE;
    static I18NMessage0P DEPLOY_STRATEGY_WIZARD_PAGE__DESCRIPTION;
    static I18NMessage0P DEPLOY_STRATEGY_WIZARD_DEFAULT_BUTTON__TEXT;
    static I18NMessage0P DEPLOY_STRATEGY_WIZARD_WINDOW__TITLE;
    static I18NMessage1P DEPLOY_STRATEGY_WIZARD_DEPLOY__TASK_NAME;
    static I18NMessage1P DEPLOY_STRATEGY_WIZARD_DEPLOY_FAILED;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
