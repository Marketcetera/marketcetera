package org.marketcetera.core.trade;

import org.marketcetera.api.systemmodel.instruments.HasCFICode;

/* $License$ */

/**
 * Indicates the commodity underlying asset type of a <code>Future</code>.
 *
 * @version $Id$
 * @since 2.1.0
 */
public enum CommodityUnderlyingAssetType
        implements HasCFICode, FutureUnderlyingAssetType
{
    /**
     * agricultural underlying asset type
     */
    AGRICULTURAL('A'),
    /**
     * extraction underlying asset type
     */
    EXTRACTION('E'),
    /**
     * industrial underlying asset type
     */
    INDUSTRIAL('I'),
    /**
     * service underlying asset type
     */
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
     * the cfi code of the commodity underlying asset type 
     */
    private final char cfiCode;
}
