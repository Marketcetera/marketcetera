package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageKey;
import quickfix.FieldNotFound;
import quickfix.Message;

/**
 * Exception with multiple constructors designed to handle and create messages
 * for various FIX-related errors
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class MarketceteraFIXException extends MarketceteraException {
    public MarketceteraFIXException(String message) {
        super(message);
    }

    public MarketceteraFIXException(String msg, Throwable nested) {
        super(msg, nested);
    }

    public MarketceteraFIXException(Throwable nested) {
        super(nested);
    }

    public static MarketceteraFIXException createFieldNotFoundException(FieldNotFound fnf) {
        return createFieldNotFoundException(fnf, null);
    }
    public static MarketceteraFIXException createFieldNotFoundException(FieldNotFound fnf, Message message) {
        String fieldName = FIXDataDictionaryManager.getCurrentFixDataDictionary().getHumanFieldName(fnf.field);
        String msg = MessageKey.FIX_FNF.getLocalizedMessage(fieldName);
        String msgSuffix = (message == null) ? "" : ": "+message;
        return new MarketceteraFIXException(msg+msgSuffix, fnf);
    }
}
