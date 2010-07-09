package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The delivery type of a <code>Future</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum DeliveryType
        implements HasCFICode
{
    /**
     * cash delivery type 
     */
    CASH('C'),
    /**
     * physical delivery type
     */
    PHYSICAL('P');
    /* (non-Javadoc)
     * @see org.marketcetera.trade.CfiType#getCfiCode()
     */
    @Override
    public char getCfiCode()
    {
        return cfiCode;
    }
    /**
     * Create a new FutureDeliveryType instance.
     *
     * @param inCfiCode
     */
    private DeliveryType(char inCfiCode)
    {
        cfiCode = inCfiCode;
    }
    /**
     * the cfi code of the future delivery type
     */
    private final char cfiCode;
}
