package org.marketcetera.server.service.impl;

import org.marketcetera.server.service.OrderValidator;
import org.marketcetera.server.service.OrderValidatorFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class OrderValidatorFactoryImpl
        implements OrderValidatorFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderValidatorFactory#getValidator(org.marketcetera.trade.Order)
     */
    @Override
    public OrderValidator getValidator()
    {
        return OrderValidatorImpl.INSTANCE;
    }
}
