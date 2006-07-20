package org.marketcetera.jcyclone;

import org.jcyclone.core.handler.EventHandlerException;
import org.marketcetera.quickfix.MarketceteraFIXException;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
public class MarketceteraEventHandlerException extends EventHandlerException {
    private Exception nested;

    public MarketceteraEventHandlerException() {
    }

    public MarketceteraEventHandlerException(String s) {
        super(s);
    }

    public MarketceteraEventHandlerException(String s, Exception inNested)
    {
        super(s);
        nested = inNested;
    }

    public MarketceteraEventHandlerException(MarketceteraFIXException mfix)
    {
        super(mfix.getMessage());
        nested = mfix;
    }

    public Exception getNested() { return nested; }

}
