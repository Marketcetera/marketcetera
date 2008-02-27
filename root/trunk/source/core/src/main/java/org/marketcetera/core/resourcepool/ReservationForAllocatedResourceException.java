package org.marketcetera.core.resourcepool;

import org.marketcetera.core.MessageKey;


class ReservationForAllocatedResourceException
        extends ResourcePoolException
{
    private static final long serialVersionUID = 4053461153409187526L;
    private Resource mResource;

    ReservationForAllocatedResourceException(Resource inResource)
    {
        super(MessageKey.INFO_WAITING_FOR_RESOURCE.getLocalizedMessage(new Object[] { inResource.toString() } ));
        setResource(inResource);
    }

    /**
     * @return the resource
     */
    Resource getResource()
    {
        return mResource;
    }

    /**
     * @param inResource the resource to set
     */
    private void setResource(Resource inResource)
    {
        mResource = inResource;
    }
}
