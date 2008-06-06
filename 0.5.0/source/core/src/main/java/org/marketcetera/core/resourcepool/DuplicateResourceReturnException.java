package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MessageKey;

/**
 * Thrown when a {@link Resource} is returned (@see {@link ResourcePool#returnResource(Resource)} twice. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
public class DuplicateResourceReturnException
        extends ResourcePoolException
{
    private static final long serialVersionUID = 1418252833897858089L;

    /**
     * Create a new DuplicateResourceReturnException instance.
     */
    public DuplicateResourceReturnException(String inMessage)
    {
        super(inMessage);
    }

    /**
     * Create a new DuplicateResourceReturnException instance.
     */
    public DuplicateResourceReturnException(String inMsg,
                                            Throwable inNested)
    {
        super(inMsg,
              inNested);
    }

    /**
     * Create a new DuplicateResourceReturnException instance.
     */
    public DuplicateResourceReturnException(Throwable inNested)
    {
        super(inNested);
    }

    /**
     * Create a new DuplicateResourceReturnException instance.
     */
    public DuplicateResourceReturnException(MessageKey inKey)
    {
        super(inKey);
    }

    /**
     * Create a new DuplicateResourceReturnException instance.
     */
    public DuplicateResourceReturnException(MessageKey inKey,
                                            Throwable inNested)
    {
        super(inKey,
              inNested);
    }
}
