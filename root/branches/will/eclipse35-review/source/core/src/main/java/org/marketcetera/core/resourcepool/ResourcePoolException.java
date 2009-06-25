package org.marketcetera.core.resourcepool;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

public class ResourcePoolException
    extends I18NException
{
    private static final long serialVersionUID = 2700304790645953795L;

    public ResourcePoolException(I18NBoundMessage message)
    {
        super(message);
    }

    public ResourcePoolException(Throwable nested)
    {
        super(nested);
    }

    public ResourcePoolException(Throwable nested,
                                 I18NBoundMessage message)
    {
        super(nested,
              message);
    }
}
