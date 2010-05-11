package org.marketcetera.trade;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum StandardType
        implements CfiType
{
    /**
     * 
     */
    STANDARD('S'),
    /**
     * 
     */
    NON_STANDARD('N');
    /* (non-Javadoc)
     * @see org.marketcetera.trade.CfiType#getCfiCode()
     */
    @Override
    public char getCfiCode()
    {
        return cfiCode;
    }
    /**
     * Create a new StandardType instance.
     *
     * @param inCfiCode
     */
    private StandardType(char inCfiCode)
    {
        cfiCode = inCfiCode;
    }
    /**
     * 
     */
    private final char cfiCode;
}
