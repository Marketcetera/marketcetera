package org.marketcetera.tradingclient;

import java.util.List;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.rpc.BaseClient;

/* $License$ */

/**
 * Provides trading-related services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradingClient
        extends BaseClient
{
    /**
     * Get open orders.
     *
     * @param inPageNumber an <code>int</code> value
     * @param inPageSize an <code>int</code> value
     * @return a <code>List&lt;ExecutionReport&gt;</code> value
     */
    List<ExecutionReport> getOpenOrders(int inPageNumber,
                                        int inPageSize);
    /**
     * Submit the given order.
     *
     * @param inOrderSingle a <code>List&lt;OrderSingle&gt;</code> value
     * @return a <code>List&lt;SendOrderResponse&gt;</code> value
     */
    List<SendOrderResponse> sendOrders(List<OrderSingle> inOrderSingle);
    /**
     * Submit the given order cancel request.
     *
     * @param inOrderCancel an <code>OrderCancel</code> value
     * @return an <code>OrderID</code> value
     */
    OrderID cancelOrder(OrderCancel inOrderCancel);
    /**
     * Submit the given order cancel replace request.
     *
     * @param inOrderReplace an <code>OrderReplace</code> value
     * @return an <code>OrderID</code> value
     */
    OrderID modifyOrder(OrderReplace inOrderReplace);
}
