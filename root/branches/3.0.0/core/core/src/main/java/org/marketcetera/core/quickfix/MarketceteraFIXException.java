package org.marketcetera.core.quickfix;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.util.log.I18NBoundMessage;
import org.marketcetera.core.util.log.I18NBoundMessage1P;
import org.marketcetera.core.util.log.I18NBoundMessage2P;
import org.marketcetera.api.attributes.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;

/**
 * Exception with multiple constructors designed to handle and create messages
 * for various FIX-related errors
 *
 * @author Toli Kuznets
 * @version $Id: MarketceteraFIXException.java 16063 2012-01-31 18:21:55Z colin $
 */
@ClassVersion("$Id: MarketceteraFIXException.java 16063 2012-01-31 18:21:55Z colin $")
public class MarketceteraFIXException extends CoreException {
    public MarketceteraFIXException(I18NBoundMessage message) {
        super(message);
    }

    public MarketceteraFIXException(Throwable nested, I18NBoundMessage msg) {
        super(nested, msg);
    }

    public MarketceteraFIXException(Throwable nested) {
        super(nested);
    }

    public static MarketceteraFIXException createFieldNotFoundException(FieldNotFound fnf) {
        String fieldName = CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getHumanFieldName(fnf.field);
        return new MarketceteraFIXException(fnf, new I18NBoundMessage1P(Messages.FIX_FNF_NOMSG, fieldName));
    }

    public static MarketceteraFIXException createFieldNotFoundException(FieldNotFound fnf, Message message) {
        String fieldName = CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getHumanFieldName(fnf.field);
        return new MarketceteraFIXException(fnf, new I18NBoundMessage2P(Messages.FIX_FNF_MSG, fieldName, message));
    }
    private static final long serialVersionUID = 1L;
}
