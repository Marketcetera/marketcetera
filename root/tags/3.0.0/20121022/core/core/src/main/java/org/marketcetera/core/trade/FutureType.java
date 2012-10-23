package org.marketcetera.core.trade;

/* $License$ */

/**
 * Indicates the type of a <code>Future</code>.
 *
 * @version $Id: FutureType.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
public enum FutureType
        implements HasCFICode
{
    /**
     * Commodity futures
     */
    COMMODITY('C'),
    /**
     * Financial futures
     */
    FINANCIAL('F');
    /**
     * Get the cfiCode value.
     *
     * @return a <code>char</code> value
     */
    public char getCfiCode()
    {
        return cfiCode;
    }
    /**
     * Create a new FutureType instance.
     *
     * @param inCfiCode a <code>char</code> value
     */
    private FutureType(char inCfiCode)
    {
        cfiCode = inCfiCode;
    }
    /**
     * the CFI code associated with this future type
     */
    private final char cfiCode;
}
