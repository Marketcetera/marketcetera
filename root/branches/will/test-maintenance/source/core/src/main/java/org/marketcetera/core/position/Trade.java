package org.marketcetera.core.position;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A trade that is used to compute position information.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface Trade {

    /**
     * Return the symbol that was traded.
     * 
     * @return the symbol, cannot be null or empty
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
     * @return the trader id, may be null
     */
    String getTraderId();

    /**
     * Return the price of the trade.
     * 
     * @return the price, must be greater than zero
     */
    BigDecimal getPrice();

    /**
     * Return the quantity of the trade. A positive quantity implies a buy and a
     * negative quantity implies a sell.
     * 
     * @return the quantity, cannot be null or zero
     */
    BigDecimal getQuantity();

    /**
     * Return the unique sequence number of the trade used for ordering trades.
     * No two trades can have the same sequence number.
     * 
     * @return the sequence number, must be greater than zero
     */
    long getSequenceNumber();
}
