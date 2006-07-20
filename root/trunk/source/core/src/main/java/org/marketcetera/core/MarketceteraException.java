package org.marketcetera.core;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class MarketceteraException extends Exception
{
    public MarketceteraException(String message)
    {
        super(message);
    }

    public MarketceteraException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

    public MarketceteraException(Throwable nested) { super(nested); }
}
