package org.marketcetera.systemmodel.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.systemmodel.OrderDestinationIdFactory;

/* $License$ */

/**
 * Constructs <code>OrderDestinationID</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class OrderDestinationIdFactoryImpl
        implements OrderDestinationIdFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.OrderDestinationIdFactory#create(java.lang.String)
     */
    @Override
    public OrderDestinationID create(String inValue)
    {
        inValue = StringUtils.trimToNull(inValue);
        Validate.notNull(inValue,
                         "Order destination ID value must not be null");
        return new OrderDestinationIdImpl(inValue);
    }
}
