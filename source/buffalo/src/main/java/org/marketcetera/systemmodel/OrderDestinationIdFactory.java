package org.marketcetera.systemmodel;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderDestinationIdFactory
{
    /**
     * 
     *
     *
     * @param inValue
     * @return
     */
    public OrderDestinationID create(String inValue);
}
