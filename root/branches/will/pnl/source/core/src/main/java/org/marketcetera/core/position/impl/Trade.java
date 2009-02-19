package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A trade that is used to compute position information.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Trade {

    /**
     * The possible side of a trade.
     */
    @ClassVersion("$Id$")
    enum Side {
        BUY, SELL
    };

    /**
     * Return the symbol that was traded.
     * 
     * @return the symbol
     */
    String getSymbol();

    /**
     * Return the account in which the trade was made.
     * 
     * @return the account, may be null
     */
    String getAccount();

    /**
     * Return the id of the trader who performed the trade.
     * 
     * @return the trader id
     */
    String getTraderId();

    /**
     * Return the side of the trade.
     * 
     * @return the side
     */
    Side getSide();

    /**
     * Return the price of the trade.
     * 
     * @return the price
     */
    BigDecimal getPrice();

    /**
     * Return the quantity of the trade.
     * 
     * @return the quantity
     */
    BigDecimal getQuantity();

    /**
     * Return the unique sequence number of the trade used for ordering trades.
     * No two trades can have the same sequence number.
     * 
     * @return the sequence number
     */
    long getSequenceNumber();
}
