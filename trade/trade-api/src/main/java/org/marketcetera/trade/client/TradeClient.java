package org.marketcetera.trade.client;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.BaseClient;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.TradeMessagePublisher;

/* $License$ */

/**
 * Provides trading-related services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradeClient
        extends BaseClient,TradeMessagePublisher
{
    /**
     * Get the currently available FIX initiator sessions.
     *
     * @return a <code>List&lt;ActiveFixSession&gt;</code> value
     */
    List<ActiveFixSession> readAvailableFixInitiatorSessions();
    /**
     * Get open orders.
     *
     * @return a <code>Collection&lt;OrderSummary&gt;</code> value
     */
    Collection<OrderSummary> getOpenOrders();
    /**
     * Get open orders.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponset&lt;OrderSummary&gt;</code> value
     */
    CollectionPageResponse<OrderSummary> getOpenOrders(PageRequest inPageRequest);
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
     * @return a <code>Map&lt;PositionKey&lt;? extends Instrument&gt;,BigDecimal&gt;</code> value
     */
    Map<PositionKey<? extends Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate);
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
     * Add the given report to the system data flow.
     * 
     * <p>Reports added this way will be added to the system data bus. Reports will be
     * persisted and become part of the system record. The report will be owned by the
     * user logged in to the client.
     * 
     * <p><em>This will affect reported positions</em></p>.
     *
     * @param inReport a <code>HasFIXMessage</code> value
     * @param inBrokerID a <code>BrokerID</code> value
     */
    void addReport(HasFIXMessage inReport,
                   BrokerID inBrokerID);
    /**
     * Removes the given report from the persistent report store.
     * 
     * <p>Reports removed this way will not be added to the system data bus and no clients
     * will receive this report.
     * 
     * <p><em>This will affect reported positions</em></p>.
     *
     * @param inReportId a <code>ReportID</code> value
     */
    void deleteReport(ReportID inReportId);
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
    /**
     * Get the option roots for the given underlying.
     *
     * @param inUnderlying a <code>String</code> value
     * @return a <code>Collection&lt;String&gt;</code> value
     */
    Collection<String> getOptionRoots(String inUnderlying);
    /**
     * Get the underlying for the given root.
     *
     * @param inOptionRoot a <code>String</code> value
     * @return a <code>String</code> value
     */
    String getUnderlying(String inOptionRoot);
    /**
     * Get the most recent execution report for the order chain represented by the given order id.
     * 
     * <p>The given <code>OrderID</code> can be either from any order in the chain or the root order id for the chain.
     * In either case, the most recent execution report for the entire chain will be returned, not the given order necessarily.
     * 
     * <p>If no execution report exists for any order in the given chain, the call will return null.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return an <code>ExecutionReport</code> value or <code>null</code>
     */
    ExecutionReport getLatestExecutionReportForOrderChain(OrderID inOrderId);
    /**
     * Get reports with the given page request.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;Report&gt;</code> value
     */
    CollectionPageResponse<Report> getReports(PageRequest inPageRequest);
}
