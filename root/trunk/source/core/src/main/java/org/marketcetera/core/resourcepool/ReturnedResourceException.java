package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MessageKey;

/**
 * Indicates an exception thrown when a {@link Resource} was returned to a {@link ResourcePool}.
 * 
 * @see {@link ResourcePool#returnResource(Resource)}
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 */
public class ReturnedResourceException
        extends ResourcePoolException
{
    private static final long serialVersionUID = 3444476929065238095L;

    /**
     * Create a new ReturnedResourceException instance.
     */
    public ReturnedResourceException(String inMessage)
    {
        super(inMessage);
    }

    /**
     * Create a new ReturnedResourceException instance.
     */
    public ReturnedResourceException(String inMsg,
                                     Throwable inNested)
    {
        super(inMsg,
              inNested);
    }

    /**
     * Create a new ReturnedResourceException instance.
     */
    public ReturnedResourceException(Throwable inNested)
    {
        super(inNested);
    }

    /**
     * Create a new ReturnedResourceException instance.
     */
    public ReturnedResourceException(MessageKey inKey)
    {
        super(inKey);
    }

    /**
     * Create a new ReturnedResourceException instance.
     */
    public ReturnedResourceException(MessageKey inKey,
                                     Throwable inNested)
    {
        super(inKey,
              inNested);
    }
}
