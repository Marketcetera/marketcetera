package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageKey;

public class ResourcePoolException
    extends MarketceteraException
{
    private static final long serialVersionUID = 2700304790645953795L;

    public ResourcePoolException(String message)
    {
        super(message);
    }

    public ResourcePoolException(String msg, 
                                 Throwable nested)
    {
        super(msg, 
              nested);
    }

    public ResourcePoolException(Throwable nested)
    {
        super(nested);
    }

    public ResourcePoolException(MessageKey inKey)
    {
        super(inKey);
    }

    public ResourcePoolException(MessageKey inKey, 
                                 Throwable nested)
    {
        super(inKey, 
              nested);
    }
}
