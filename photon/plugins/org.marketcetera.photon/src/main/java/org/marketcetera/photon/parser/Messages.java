package org.marketcetera.photon.parser;

import org.marketcetera.photon.commons.ReflectiveMessages;
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
    
    static I18NMessage1P ORDER_SINGLE_PARSER_INVALID_SIDE;
    static I18NMessage1P ORDER_SINGLE_PARSER_NOT_A_DECIMAL;
    static I18NMessage1P ORDER_SINGLE_PARSER_INVALID_PRICE;
    static I18NMessage1P ORDER_SINGLE_PARSER_INVALID_TIF;
    static I18NMessage1P ORDER_SINGLE_PARSER_NO_VALUE_FOR_OPTIONAL_FIELD;
    static I18NMessage1P ORDER_SINGLE_PARSER_INVALID_OPTIONAL_FIELD;
    static I18NMessage1P ORDER_SINGLE_PARSER_INVALID_BROKER_ID;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
