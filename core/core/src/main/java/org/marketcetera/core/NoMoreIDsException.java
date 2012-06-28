package org.marketcetera.core;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.core.attributes.ClassVersion;

/**
 * Excpetion to denote that we ran out of IDs while getting them
 * @author Toli Kuznets
 * @version $Id: NoMoreIDsException.java 16063 2012-01-31 18:21:55Z colin $
 */
@ClassVersion("$Id: NoMoreIDsException.java 16063 2012-01-31 18:21:55Z colin $")
public class NoMoreIDsException extends CoreException
{
    private static final long serialVersionUID = -6403447553151646661L;

    public NoMoreIDsException(Throwable nested)
    {
        super(nested);
    }
    public NoMoreIDsException(I18NBoundMessage message)
    {
        super(message);
    }

    public NoMoreIDsException(Throwable nested, I18NBoundMessage message)
    {
        super(nested, message);
    }

}
