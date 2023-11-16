package org.marketcetera.trade;

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
 * @version $Id$
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
     * @param inMessage a <code>quickfix.Message</code> value
     * @return an <code>OrderID</code> value or <code>null</code> if no root order id exists
     */
    OrderID getRootOrderId(quickfix.Message inMessage);
    /**
     * Records outgoing messages, if necessary.
     *
     * @param inMessage a <code>quickfix.Message</code> value
     */
    void receiveOutgoingMessage(quickfix.Message inMessage);
}
