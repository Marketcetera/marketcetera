package org.marketcetera.trade;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum FinancialUnderlyingAssetType
        implements CfiType, FutureUnderlyingAssetType
{
    /**
     * 
     */
    STOCK('S'),
    /**
     * 
     */
    INDEX('I'),
    /**
     * 
     */
    DEBT('D'),
    /**
     * 
     */
    CURRENCY('C'),
    /**
     * 
     */
    OPTION('O'),
    /**
     * 
     */
    FUTURE('F'),
    /**
     * 
     */
    COMMODITY('T'),
    /**
     * 
     */
    SWAP('W'),
    /**
     * 
     */
    BASKET('B'),
    /**
     * 
     */
    OTHER('M');

    /* (non-Javadoc)
     * @see org.marketcetera.trade.CfiType#getCfiCode()
     */
    @Override
    public char getCfiCode()
    {
        return cfiCode;
    }
    /**
     * Create a new FinancialUnderlyingAssetType instance.
     *
     * @param inCfiCode
     */
    private FinancialUnderlyingAssetType(char inCfiCode)
    {
        cfiCode = inCfiCode;
    }
    /**
     * 
     */
    private final char cfiCode;
}
