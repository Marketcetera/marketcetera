package org.marketcetera.photon.internal.strategy.engine.ui;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.photon.commons.ui.LocalizedLabel;
import org.marketcetera.util.log.I18NMessage0P;
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
    /*
     * UI Text
     */
    static LocalizedLabel STRATEGY_DEPLOYMENT_COMPOSITE_SCRIPT;
    static LocalizedLabel STRATEGY_DEPLOYMENT_COMPOSITE_LANGUAGE;
    static LocalizedLabel STRATEGY_DEPLOYMENT_COMPOSITE_CLASS;
    static LocalizedLabel STRATEGY_DEPLOYMENT_COMPOSITE_INSTANCE_NAME;
    static LocalizedLabel STRATEGY_DEPLOYMENT_COMPOSITE_ROUTE;
    static I18NMessage0P STRATEGY_DEPLOYMENT_COMPOSITE_CONFIGURATION_GROUP__LABEL;
    static I18NMessage0P STRATEGY_DEPLOYMENT_COMPOSITE_ENGINE_SELECTION_TABLE__LABEL;
    static I18NMessage0P STRATEGY_DEPLOYMENT_COMPOSITE_ENGINE__LABEL;
    static LocalizedLabel STRATEGY_ENGINE_IDENTIFICATION_COMPOSITE_NAME;
    static LocalizedLabel STRATEGY_ENGINE_IDENTIFICATION_COMPOSITE_DESCRIPTION;
    static LocalizedLabel DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_INSTANCE_NAME;
    static LocalizedLabel DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_CLASS;
    static LocalizedLabel DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_LANGUAGE;
    static LocalizedLabel DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_SCRIPT;
    static LocalizedLabel DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_ROUTE;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
