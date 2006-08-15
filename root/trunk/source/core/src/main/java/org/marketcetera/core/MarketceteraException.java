package org.marketcetera.core;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class MarketceteraException extends Exception
{
    /** Should used with an already localized string
     * otherwise, use the {@link #MarketceteraException(MessageKey)} instead
     * @param message
     */
    public MarketceteraException(String message)
    {
        super(message);
    }

    /** Should used with an already localized string
     * otherwise, use the {@link #MarketceteraException(MessageKey, Throwable)} instead
     */
    public MarketceteraException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

    public MarketceteraException(Throwable nested) { super(nested); }

    public MarketceteraException(MessageKey inKey)
    {
        super(MessageKey.getMessageString(inKey.toString()));
    }
    public MarketceteraException(MessageKey inKey, Throwable nested)
    {
        super(MessageKey.getMessageString(inKey.toString()), nested);
    }
}
