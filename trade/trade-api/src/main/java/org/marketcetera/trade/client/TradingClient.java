package org.marketcetera.trade.client;

import java.util.List;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.core.BaseClient;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;

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
     * @return a <code>List&lt;ExecutionReport&gt;</code> value
     */
    List<ExecutionReport> getOpenOrders();
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
     * Submit the given orders.
     *
     * @param inOrders a <code>List&lt;OrderSingle&gt;</code> value
     * @return a <code>List&lt;SendOrderResponse&gt;</code> value
     */
    List<SendOrderResponse> sendOrders(List<OrderSingle> inOrders);
    /**
     * Submit the given order.
     *
     * @param inOrderSingle an <code>OrderSingle</code> value
     * @return a <code>SendOrderResponse</code> value
     */
    SendOrderResponse sendOrder(OrderSingle inOrderSingle);
    /**
     * Submit the given order.
     *
     * @param inFixOrder a <code>FIXOrder</code> value
     * @return a <code>SendOrderResponse</code> value
     */
    SendOrderResponse sendOrder(FIXOrder inFixOrder);
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
    /**
     *
     *
     * @param inReportListener a <code>ReportListener</code> value
     */
    void addReportListener(ReportListener inReportListener);
    /**
     *
     *
     * @param inReportListener a <code>ReportListener</code> value
     */
    void removeReportListener(ReportListener inReportListener);
    /**
     *
     *
     * @param inBrokerStatusListener
     */
    void addBrokerStatusListener(BrokerStatusListener inBrokerStatusListener);
    /**
     *
     *
     * @param inBrokerStatusListener
     */
    void removeBrokerStatusListener(BrokerStatusListener inBrokerStatusListener);
}
