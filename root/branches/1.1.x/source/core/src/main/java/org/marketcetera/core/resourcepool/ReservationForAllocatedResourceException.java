package org.marketcetera.core.resourcepool;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.core.Messages;
import org.marketcetera.util.log.I18NBoundMessage1P;


class ReservationForAllocatedResourceException
        extends ResourcePoolException
{
    private static final long serialVersionUID = 4053461153409187526L;
    private Resource mResource;

    ReservationForAllocatedResourceException(Resource inResource)
    {
        super(new I18NBoundMessage1P(Messages.INFO_WAITING_FOR_RESOURCE,
                                     ObjectUtils.toString(inResource,null)));
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
