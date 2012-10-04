package org.marketcetera.core.trade;

/* $License$ */

/**
 * Indicates the standard type of a <code>Future</code>.
 *
 * @version $Id: StandardType.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
public enum StandardType
        implements HasCFICode
{
    /**
     * standard type
     */
    STANDARD('S'),
    /**
     * non-standard type
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
     * the cfi code associated with the standard type 
     */
    private final char cfiCode;
}
