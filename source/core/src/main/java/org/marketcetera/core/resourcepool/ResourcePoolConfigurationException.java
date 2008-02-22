package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MessageKey;

/**
 * Indicates the settings for the {@link ResourcePool} are misconfigured.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class ResourcePoolConfigurationException
    extends ResourcePoolException
{
    private static final long serialVersionUID = 3861667393623987041L;

    public ResourcePoolConfigurationException(String message)
    {
        super(message);
    }

    public ResourcePoolConfigurationException(String msg, 
                                              Throwable nested)
    {
        super(msg, 
              nested);
    }

    public ResourcePoolConfigurationException(Throwable nested)
    {
        super(nested);
    }

    public ResourcePoolConfigurationException(MessageKey inKey)
    {
        super(inKey);
    }

    public ResourcePoolConfigurationException(MessageKey inKey, 
                                              Throwable nested)
    {
        super(inKey, 
              nested);
    }
}
