package org.marketcetera.trade.client;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokerStatusPublisher;
import org.marketcetera.brokers.BrokersStatus;
import org.marketcetera.core.BaseClient;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
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
        extends BaseClient,TradeMessagePublisher,BrokerStatusPublisher
{
    /**
     * Get open orders.
     *
     * @return a <code>Collection&lt;? extends OrderSummary&gt;</code> value
     */
    Collection<? extends OrderSummary> getOpenOrders();
    /**
     * Get open orders.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponset&lt;? extends OrderSummary&gt;</code> value
     */
    CollectionPageResponse<? extends OrderSummary> getOpenOrders(PageRequest inPageRequest);
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
     * Add the given broker status listener.
     *
     * @param inBrokerStatusListener a <code>BrokerStatusListener</code> value
     */
    void addBrokerStatusListener(BrokerStatusListener inBrokerStatusListener);
    /**
     * Remove the given broker status listener.
     *
     * @param inBrokerStatusListener a <code>BrokerStatusListener</code> value
     */
    void removeBrokerStatusListener(BrokerStatusListener inBrokerStatusListener);
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
     * Get the status of all brokers.
     * 
     * @return a <code>BrokersStatus</code> value
     */
    BrokersStatus getBrokersStatus();
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
}
