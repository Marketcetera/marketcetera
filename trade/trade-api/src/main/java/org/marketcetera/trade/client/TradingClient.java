package org.marketcetera.trade.client;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokerStatusPublisher;
import org.marketcetera.brokers.BrokersStatus;
import org.marketcetera.core.BaseClient;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSummary;

/* $License$ */

/**
 * Provides trading-related services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradingClient
        extends BaseClient,ReportPublisher,BrokerStatusPublisher
{
    /**
     * Get open orders.
     *
     * @return a <code>List&lt;OrderSummary&gt;</code> value
     */
    List<OrderSummary> getOpenOrders();
    /**
     * Get open orders.
     *
     * @param inPageNumber an <code>int</code> value
     * @param inPageSize an <code>int</code> value
     * @return a <code>List&lt;OrderSummary&gt;</code> value
     */
    List<OrderSummary> getOpenOrders(int inPageNumber,
                                     int inPageSize);
    /**
     * Submit the given orders.
     *
     * @param inOrders a <code>List&lt;Order&gt;</code> value
     * @return a <code>List&lt;SendOrderResponse&gt;</code> value
     */
    List<SendOrderResponse> sendOrders(List<Order> inOrders);
    /**
     * Submit the given order.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>SendOrderResponse</code> value
     */
    SendOrderResponse sendOrder(Order inOrder);
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
    /**
     * Adds a server connection status listener, which receives all
     * the server connection status changes.
     *
     * <p>If the same listener is added more than once, it will receive
     * notifications as many times as it has been added.</p>
     *
     * <p>The listeners are notified in the reverse order of their
     * addition.</p>
     *
     * @param listener The listener which should be supplied the
     * server connection status changes.
     */
    void addServerStatusListener(ServerStatusListener listener);
    /**
     * Removes a server connection status listener that was previously
     * added via {@link
     * #addServerStatusListener(ServerStatusListener)}.
     *
     * <p>If the listener was added more than once, only its most
     * recently added instance will be removed.</p>
     *
     * @param listener The listener which should stop receiving server
     * connection status changes.
     */
    void removeServerStatusListener(ServerStatusListener listener);
    /**
     * Returns the position of the supplied instrument based on reports generated and received on or before the supplied date in UTC.
     *
     * @param inDate a <code>Date</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getPositionAsOf(Date inDate,
                               Instrument inInstrument);
    /**
     * Returns all positions based on reports generated and received on or before the supplied date in UTC.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;Instrument&gt;,BigDecimal&gt;</code> value
     */
    Map<PositionKey<Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate);
    /**
     * Returns all positions of options with the given root symbols based on reports generated and received on or before the supplied date in UTC.
     *
     * @param inDate a <code>Date</code> value
     * @param inRootElements a <code>String[]</code> value
     * @return a <code>Map&lt;PositionKey&lt;Optiont&gt;,BigDecimal&gt;</code> value
     */
    Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                               String... inRootSymbols);
    /**
     * Adds an exception listener. The exception listeners are notified
     * whenever the client encounters connectivity issues when communicating
     * with the server.
     * <p>
     * The listeners are notified only when connectivity issues are
     * encountered when sending or receiving messages, ie. when any of
     * the <code>send*()</code> methods are invoked, or when the
     * client receives a message and encounters errors processing it
     * before delivering it to {@link ReportListener} or {@link
     * BrokerStatusListener}, or when client heartbeats cannot reach
     * the server.
     * <p>
     * If the same listener is added more than once, it will receive
     * notifications as many times as it's been added.
     * <p>
     * The listeners are notified in the reverse order of their addition.
     *
     * @param inListener the listener instance.
     */
    void addExceptionListener(ExceptionListener inListener);
    /**
     * Removes exception listener that was previously added via
     * {@link #addExceptionListener(java.beans.ExceptionListener)}. The
     * listener will stop receiving exception notifications after this
     * method returns.
     * If the listener was added more than once, only its most
     * recently added occurrence will be removed. 
     *
     * @param inListener The exception listener that should no longer
     */
    void removeExceptionListener(ExceptionListener inListener);
    /**
     * Returns the server's broker status.
     *
     * @return The status.
     *
     * completed.
     */
    BrokersStatus getBrokersStatus();
//    /**
//     * Adds the given report to the system data flow.
//     * 
//     * <p>Reports added this way will be added to the system data bus. Reports will be
//     * persisted and become part of the system record. All clients will receive this
//     * report.
//     * 
//     * <p><em>This will affect reported positions</em></p>.
//     *
//     * @param inReport a <code>FIXMessageWrapper</code> value
//     * @param inBrokerID a <code>BrokerID</code> value
//     * @param inHierarchy a <code>Hierarchy</code> value
//     */
//    void addReport(FIXMessageWrapper inReport,
//                   BrokerID inBrokerID,
//                   Hierarchy inHierarchy);
//    /**
//     * Removes the given report from the persistent report store.
//     * 
//     * <p>Reports removed this way will not be added to the system data bus and no clients
//     * will receive this report.
//     * 
//     * <p><em>This will affect reported positions</em></p>.
//     *
//     * @param inReport an <code>ExecutionReportImpl</code> value
//     */
//    void deleteReport(ExecutionReportImpl inReport);
    /**
     * Resolves the given symbol to an <code>Instrument</code>.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     */
    Instrument resolveSymbol(String inSymbol);
    /**
     * Find the root order ID for the order chain of the given order ID.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderID</code> value
     */
    OrderID findRootOrderIdFor(OrderID inOrderID);
}
