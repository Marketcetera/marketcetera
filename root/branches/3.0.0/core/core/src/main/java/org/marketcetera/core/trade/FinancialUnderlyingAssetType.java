package org.marketcetera.core.trade;


/* $License$ */

/**
 * Indicates the financial underlying asset type of a <code>Future</code>.
 *
 * @version $Id$
 * @since 2.1.0
 */
public enum FinancialUnderlyingAssetType
        implements HasCFICode, FutureUnderlyingAssetType
{
    /**
     * stock underlying asset
     */
    STOCK('S'),
    /**
     * index underlying asset
     */
    INDEX('I'),
    /**
     * debt underlying asset
     */
    DEBT('D'),
    /**
     * currency underlying asset
     */
    CURRENCY('C'),
    /**
     * option underlying asset
     */
    OPTION('O'),
    /**
     * future underlying asset
     */
    FUTURE('F'),
    /**
     * commodity underlying asset
     */
    COMMODITY('T'),
    /**
     * swap underlying asset
     */
    SWAP('W'),
    /**
     * basket underlying asset
     */
    BASKET('B'),
    /**
     * other underlying asset
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
     * the CFI code for the financial underlying asset type 
     */
    private final char cfiCode;
}
