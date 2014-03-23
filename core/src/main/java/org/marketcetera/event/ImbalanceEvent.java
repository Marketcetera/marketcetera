package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates a market imbalance event has occurred.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ImbalanceEvent
        extends Event, HasEventType, HasInstrument
{
    /**
     * Gets the auction type value.
     *
     * @return an <code>AuctionType</code> value
     */
    AuctionType getAuctionType();
    /**
     * Gets the imbalance exchange.
     *
     * @return a <code>String</code> value
     */
    String getExchange();
    /**
     * Gets the far price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getFarPrice();
    /**
     * Gets the imbalance volume value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getImbalanceVolume();
    /**
     * Gets the instrument status value.
     *
     * @return an <code>InstrumentStatus</code> value
     */
    InstrumentStatus getInstrumentStatus();
    /**
     * Gets the market status value.
     *
     * @return a <code>MarketStatus</code> value
     */
    MarketStatus getMarketStatus();
    /**
     * Gets the near price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getNearPrice();
    /**
     * Gets the paired volume value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getPairedVolume();
    /**
     * Gets the reference price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getReferencePrice();
    /**
     * Gets the imbalance type value.
     *
     * @return an <code>ImbalanceType</code> value
     */
    ImbalanceType getImbalanceType();
    /**
     * Gets the short sale restricted value.
     *
     * @return a <code>boolean</code> value
     */
    boolean isShortSaleRestricted();
}
