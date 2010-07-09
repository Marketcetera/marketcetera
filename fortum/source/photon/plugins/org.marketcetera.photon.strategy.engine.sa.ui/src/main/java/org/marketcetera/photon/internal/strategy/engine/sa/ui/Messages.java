package org.marketcetera.photon.internal.strategy.engine.sa.ui;

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

    static I18NMessage0P STRATEGY_AGENT_CONNECTION_COMPOSITE_READ_ONLY__LABEL;
    static LocalizedLabel STRATEGY_AGENT_CONNECTION_COMPOSITE_JMSURL;
    static LocalizedLabel STRATEGY_AGENT_CONNECTION_COMPOSITE_HOSTNAME;
    static LocalizedLabel STRATEGY_AGENT_CONNECTION_COMPOSITE_PORT;
    static LocalizedLabel STRATEGY_AGENT_CONNECTION_COMPOSITE_AUTO_CONNECT;
    static I18NMessage0P STRATEGY_AGENT_CONNECTION_COMPOSITE_INVALID_JMSURL;
    static I18NMessage0P STRATEGY_AGENT_CONNECTION_COMPOSITE_INVALID_PORT;
    static I18NMessage0P NEW_STRATEGY_AGENT_WIZARD_PAGE__TITLE;
    static I18NMessage0P NEW_STRATEGY_AGENT_WIZARD_PAGE__DESCRIPTION;
    static I18NMessage0P NEW_STRATEGY_AGENT_WIZARD_PAGE_CONFIGURATION_GROUP__LABEL;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
