package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MessageKey;

/**
 * Represents an error that occurs during creation of a {@link Resource}.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class ResourceCreationException
    extends ResourcePoolException
{
    private static final long serialVersionUID = 24305846170056515L;

    public ResourceCreationException(String message)
    {
        super(message);
    }

    public ResourceCreationException(Throwable cause)
    {
        super(cause);
    }

    public ResourceCreationException(String message, 
                                     Throwable cause)
    {
        super(message, 
              cause);
    }

    public ResourceCreationException(MessageKey inKey)
    {
        super(inKey);
    }

    public ResourceCreationException(MessageKey inKey, 
                                     Throwable nested)
    {
        super(inKey, 
              nested);
    }
}
