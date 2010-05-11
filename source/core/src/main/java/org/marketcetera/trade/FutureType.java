package org.marketcetera.trade;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum FutureType
        implements CfiType
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
