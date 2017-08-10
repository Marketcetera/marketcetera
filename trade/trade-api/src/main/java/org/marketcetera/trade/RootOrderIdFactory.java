package org.marketcetera.trade;

import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.TradeMessage;

import quickfix.Message;

/* $License$ */

/**
 * Constructs root order id values.
 * 
 * <p>The root order ID is a Marketcetera para-FIX artifact that uniquely identifies an order chain.
 * An order chain can be loosely defined as all the FIX messages whose execution reports share the same broker OrderID (37) value.
 * When persisting messages, Marketcetera identifies the root order ID for an order chain and uses that to link messages together.
 * This is particularly useful when calculating positions to make sure that partial fills aren't counted twice.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: RootOrderIdFactory.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.5.0
 */
public interface RootOrderIdFactory
{
    /**
     * Gets the root order id associated with the given report.
     *
     * @param inReport a <code>TradeMessage</code> value
     * @return an <code>OrderID</code> value or <code>null</code> if no root order id exists
     */
    OrderID getRootOrderId(TradeMessage inReport);
    /**
     * Gets the root order id associated with the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return an <code>OrderID</code> value or <code>null</code> if no root order id exists
     */
    OrderID getRootOrderId(Message inMessage);
}
