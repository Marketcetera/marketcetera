package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MessageKey;

class ResourcePoolTryAllocationAgainException
        extends ResourcePoolException
{
    private static final long serialVersionUID = 4053461153409187526L;

    ResourcePoolTryAllocationAgainException(String inMessage)
    {
        super(inMessage);
    }

    ResourcePoolTryAllocationAgainException(String inMsg,
                                                Throwable inNested)
    {
        super(inMsg,
              inNested);
    }

    ResourcePoolTryAllocationAgainException(Throwable inNested)
    {
        super(inNested);
    }

    ResourcePoolTryAllocationAgainException(MessageKey inKey)
    {
        super(inKey);
    }

    ResourcePoolTryAllocationAgainException(MessageKey inKey,
                                                Throwable inNested)
    {
        super(inKey,
              inNested);
    }
}
