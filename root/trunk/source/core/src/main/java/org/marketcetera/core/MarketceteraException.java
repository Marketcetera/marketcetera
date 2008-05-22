package org.marketcetera.core;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class MarketceteraException extends Exception
{
    private static final long serialVersionUID=1L;

    Throwable nestedException;

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
	nestedException = nested;
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

    @Override
    public String getMessage(){
	String superMessage = super.getMessage();
	if (superMessage == null && nestedException != null) {
		return nestedException.getMessage();
    }
	return superMessage;
    }

    @Override
    public String getLocalizedMessage(){
	String superMessage = super.getMessage();
	if (superMessage == null && nestedException != null) {
		return nestedException.getMessage();
	}
	return superMessage;
    }
}
