package org.marketcetera.trade;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum CommodityUnderlyingAssetType
        implements CfiType, FutureUnderlyingAssetType
{
    AGRICULTURAL('A'),
    EXTRACTION('E'),
    INDUSTRIAL('I'),
    SERVICE('S');
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
     * Create a new CommodityUnderlyingAssetType instance.
     *
     * @param inCfiCode
     */
    private CommodityUnderlyingAssetType(char inCfiCode)
    {
        cfiCode = inCfiCode;
    }
    /**
     * 
     */
    private final char cfiCode;
}
