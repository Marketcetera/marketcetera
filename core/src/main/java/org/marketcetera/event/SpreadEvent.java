package org.marketcetera.event;

import org.marketcetera.trade.DeliveryType;
import org.marketcetera.trade.FutureType;
import org.marketcetera.trade.FutureUnderlyingAssetType;
import org.marketcetera.trade.StandardType;

/* $License$ */

/**
 * Indicates that the implementing class represents a spread event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SpreadEvent
        extends HasSpread,Event,HasProviderSymbol
{
    /**
     * Gets the future type.
     *
     * @return a <code>FutureType</code> value
     */
    public FutureType getLeg1Type();
    /**
     * Gets the future underlying asset type.
     *
     * @return a <code>FutureUnderlyingAssertType</code> value
     */
    public FutureUnderlyingAssetType getLeg1UnderylingAssetType();
    /**
     * Gets the delivery type.
     *
     * @return a <code>DeliveryType</code> value
     */
    public DeliveryType getLeg1DeliveryType();
    /**
     * Gets the standard type.
     *
     * @return a <code>StandardType</code> value
     */
    public StandardType getLeg1StandardType();
    /**
     * Returns the contract size.
     *
     * @return an <code>int</code> value
     */
    public int getLeg1ContractSize();
    /**
     * Gets the future type.
     *
     * @return a <code>FutureType</code> value
     */
    public FutureType getLeg2Type();
    /**
     * Gets the future underlying asset type.
     *
     * @return a <code>FutureUnderlyingAssertType</code> value
     */
    public FutureUnderlyingAssetType getLeg2UnderylingAssetType();
    /**
     * Gets the delivery type.
     *
     * @return a <code>DeliveryType</code> value
     */
    public DeliveryType getLeg2DeliveryType();
    /**
     * Gets the standard type.
     *
     * @return a <code>StandardType</code> value
     */
    public StandardType getLeg2StandardType();
    /**
     * Returns the contract size.
     *
     * @return an <code>int</code> value
     */
    public int getLeg2ContractSize();
}
