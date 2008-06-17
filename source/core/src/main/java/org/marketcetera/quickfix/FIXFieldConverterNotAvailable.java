package org.marketcetera.quickfix;

import org.marketcetera.core.MarketceteraException;

/**
 * Throws when the {@link FIXDataDictionary} is not found for
 * the specified version of FIX Protocol
 * @author Toli Kuznets
 * @version $Id$
 */
public class FIXFieldConverterNotAvailable extends MarketceteraException {
    public FIXFieldConverterNotAvailable(String message) {
        super(message);
    }

    public FIXFieldConverterNotAvailable(String msg, Throwable nested) {
        super(msg, nested);
    }
}
