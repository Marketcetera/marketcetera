package org.marketcetera.core.trade;


/* $License$ */

/**
 * The delivery type of a <code>Future</code>.
 *
 * @version $Id$
 * @since 2.1.0
 */
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
