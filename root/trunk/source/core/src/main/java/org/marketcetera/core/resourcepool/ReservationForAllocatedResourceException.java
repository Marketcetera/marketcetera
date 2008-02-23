package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MessageKey;

class ReservationForAllocatedResourceException
        extends ResourcePoolException
{
    private static final long serialVersionUID = 4053461153409187526L;

    ReservationForAllocatedResourceException(String inMessage)
    {
        super(inMessage);
    }

    ReservationForAllocatedResourceException(String inMsg,
                                                Throwable inNested)
    {
        super(inMsg,
              inNested);
    }

    ReservationForAllocatedResourceException(Throwable inNested)
    {
        super(inNested);
    }

    ReservationForAllocatedResourceException(MessageKey inKey)
    {
        super(inKey);
    }

    ReservationForAllocatedResourceException(MessageKey inKey,
                                                Throwable inNested)
    {
        super(inKey,
              inNested);
    }
}
