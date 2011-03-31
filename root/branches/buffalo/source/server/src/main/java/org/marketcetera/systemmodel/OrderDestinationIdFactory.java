package org.marketcetera.systemmodel;

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
