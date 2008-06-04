package org.marketcetera.orderloader;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class OrderParsingException extends MarketceteraException
{
    public OrderParsingException(String inField)
    {
        super(inField);
    }

    public OrderParsingException(String inField, Throwable nested)
    {
        super(inField, nested);
    }
    public OrderParsingException(String inField, String inValue, Throwable nested)
    {
        super("Unable to convert field ["+inField+"] with value ["+inValue+"] to FIX format", nested);
    }
    public OrderParsingException(Throwable nested)
    {
        super(nested);
    }
}
