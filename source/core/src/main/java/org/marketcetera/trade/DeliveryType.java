package org.marketcetera.trade;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum DeliveryType
        implements CfiType
{
    /**
     * 
     */
    CASH('C'),
    /**
     * 
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
     * 
     */
    private final char cfiCode;
}
