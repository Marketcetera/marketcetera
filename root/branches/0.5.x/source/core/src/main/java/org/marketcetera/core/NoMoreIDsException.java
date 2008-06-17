package org.marketcetera.core;


/**
 * Excpetion to denote that we ran out of IDs while getting them
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class NoMoreIDsException extends MarketceteraException
{
	private static final long serialVersionUID = -6403447553151646661L;

	public NoMoreIDsException(String message)
    {
        super(message);
    }

    public NoMoreIDsException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

    public NoMoreIDsException(Throwable nested)
    {
        super(nested);
    }
}
