package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MessageKey;

/**
 * Indicates that a {@link ResourcePool} is shutting down.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class ResourcePoolShuttingDownException
    extends ResourcePoolException
{
    private static final long serialVersionUID = -6518841025161443137L;

    public ResourcePoolShuttingDownException(String message)
    {
        super(message);
    }

    public ResourcePoolShuttingDownException(String msg, 
                                             Throwable nested)
    {
        super(msg, 
              nested);
    }

    public ResourcePoolShuttingDownException(Throwable nested)
    {
        super(nested);
    }

    public ResourcePoolShuttingDownException(MessageKey inKey)
    {
        super(inKey);
    }

    public ResourcePoolShuttingDownException(MessageKey inKey, 
                                             Throwable nested)
    {
        super(inKey, 
              nested);
    }
}
