package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MessageKey;

/**
 * No {@link Resource} objects were available from a {@link ResourcePool}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class NoResourceException
    extends ResourcePoolException
{
    private static final long serialVersionUID = -5650883526190777507L;

    public NoResourceException(String message)
    {
        super(message);
    }

    public NoResourceException(String msg, 
                               Throwable nested)
    {
        super(msg, 
              nested);
    }

    public NoResourceException(Throwable nested)
    {
        super(nested);
    }

    public NoResourceException(MessageKey inKey)
    {
        super(inKey);
    }

    public NoResourceException(MessageKey inKey, 
                               Throwable nested)
    {
        super(inKey, 
              nested);
    }
}
