package org.marketcetera.server.service.impl;

import org.marketcetera.server.service.OrderDestinationManager;
import org.marketcetera.server.service.OrderValidator;
import org.marketcetera.trade.Order;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
enum OrderValidatorImpl
        implements OrderValidator
{
    INSTANCE;
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderValidator#validate(org.marketcetera.trade.Order)
     */
    @Override
    public <T extends Order> void validate(T inOrder)
    {
        SLF4JLoggerProxy.debug(OrderValidatorImpl.class,
                               "{} beginning to process {}",
                               this,
                               inOrder);
        
    }
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
        return LABEL;
    }
    /**
     * 
     */
    @Autowired
    private OrderDestinationManager orderDestinationManager;
    /**
     * self-identification
     */
    private static final String LABEL = "Order Validator";
}
