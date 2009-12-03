package org.marketcetera.photon.views;

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
 * @since 2.0.0
 */
@ClassVersion("$Id$")
final class Messages {
    
    static I18NMessage0P ORDER_TICKET_VIEW_SIDE__LABEL;
    static I18NMessage0P ORDER_TICKET_VIEW_QUANTITY__LABEL;
    static I18NMessage0P ORDER_TICKET_VIEW_SYMBOL__LABEL;
    static I18NMessage0P ORDER_TICKET_VIEW_ORDER_TYPE__LABEL;
    static I18NMessage0P ORDER_TICKET_VIEW_PRICE__LABEL;
    static I18NMessage0P ORDER_TICKET_VIEW_BROKER__LABEL;
    static I18NMessage0P ORDER_TICKET_VIEW_CANNOT_BIND_TO_TICKET;
    static I18NMessage1P ORDER_TICKET_VIEW_NOT_DECIMAL_ERROR;
    static I18NMessage0P OPTION_ORDER_TICKET_VIEW_EXPIRY__LABEL;
    static I18NMessage0P OPTION_ORDER_TICKET_VIEW_STRIKE_PRICE__LABEL;
    static I18NMessage0P OPTION_ORDER_TICKET_VIEW_OPTION_TYPE__LABEL;
    static I18NMessage0P OPTION_ORDER_TICKET_VIEW_NEW__HEADING;
    static I18NMessage0P OPTION_ORDER_TICKET_VIEW_REPLACE__HEADING;
    static I18NMessage0P STOCK_ORDER_TICKET_VIEW_NEW__HEADING;
    static I18NMessage0P STOCK_ORDER_TICKET_VIEW_REPLACE__HEADING;
    
    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
