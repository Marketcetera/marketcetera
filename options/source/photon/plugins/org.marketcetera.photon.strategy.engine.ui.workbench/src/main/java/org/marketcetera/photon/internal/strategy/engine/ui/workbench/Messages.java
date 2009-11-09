package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

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
 * @since $Release$
 */
@ClassVersion("$Id$")
final class Messages {

    static I18NMessage0P STRATEGY_PROPERTY_PAGE_DELETE_MENU_ITEM__TEXT;
    static I18NMessage0P STRATEGY_PROPERTY_PAGE_ADD_MENU_ITEM__TEXT;
    static I18NMessage0P STRATEGY_PROPERTY_PAGE_ADD_BUTTON__TEXT;
    static I18NMessage0P STRATEGY_PROPERTY_PAGE_PARAMETERS_DESCRIPTION__LABEL;
    static I18NMessage1P STRATEGY_PROPERTY_PAGE_UPDATE__TASK_NAME;
    static I18NMessage1P STRATEGY_PROPERTY_PAGE_UPDATE_FAILED;
    static I18NMessage0P NEW_PROPERTY_INPUT_DIALOG__TEXT;
    static I18NMessage0P NEW_PROPERTY_INPUT_DIALOG_KEY__LABEL;
    static I18NMessage0P NEW_PROPERTY_INPUT_DIALOG_KEY__DESCRIPTION;
    static I18NMessage0P NEW_PROPERTY_INPUT_DIALOG_VALUE__LABEL;
    static I18NMessage2P STRATEGY_ENGINES_ADAPTER_FACTORY_DEPLOYED_STRATEGY_LABEL;
    static I18NMessage0P STRATEGY_ENGINES_VIEW_FAILED_TO_OPEN_PROPERTIES;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
