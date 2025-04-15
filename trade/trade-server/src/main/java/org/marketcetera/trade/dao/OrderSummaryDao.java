package org.marketcetera.trade.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.SecurityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides data store access to {@link PersistentOrderSummary} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderSummaryDao
        extends JpaRepository<PersistentOrderSummary,Long>,QuerydslPredicateExecutor<PersistentOrderSummary>
{
    /**
     * Find the order summary with the given report id.
     *
     * @param inReportId a <code>long</code> value
     * @return a <code>PersistentOrderStatus</code> value or <code>null</code>
     */
    PersistentOrderSummary findByReportId(long inReportId);
    /**
     * Find the order summaries with the given root order id.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @return a <code>List&lt;PersistentOrderStatus&gt;</code> value
     */
    List<PersistentOrderSummary> findByRootOrderId(OrderID inRootOrderId);
    /**
     * Find the order summary with the given root order id and individual order id.
     *
     * @param inRootID an <code>OrderID</code> value
     * @param inOrderID an <code>OrderID</code> value
     * @return a <code>PersistentOrderStatus</code>value
     */
    PersistentOrderSummary findByRootOrderIdAndOrderId(OrderID inRootID,
                                                      OrderID inOrderID);
    /**
     * Find the order status value with the given order id.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return a <code>Optional&lt;PersistentOrderStatus&gt;</code> value
     */
    Optional<PersistentOrderSummary> findByOrderId(OrderID inOrderId);
    /**
     * Find the most recent member from each order chain where the most recent member is open.
     *
     * @param inOpenOrderStatuses a <code>Set&lt;OrderStatus&gt;</code> value
     * @param inPageRequest a <code>Pageable</code> value
     * @return a <code>Page&lt;OrderSummary&gt;</code> value
     */
    @Query("select o1 from OrderSummary o1 where o1.orderStatus in ?1")
    Page<OrderSummary> findOpenOrders(Set<OrderStatus> inOpenOrderStatuses,
                                      Pageable inPageRequest);
                                      
    /**
     * Find most recent order summary for a root order ID.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inPageable a <code>Pageable</code> value
     * @return a <code>Page&lt;PersistentOrderSummary&gt;</code> value
     */
    Page<PersistentOrderSummary> findByRootOrderId(OrderID inRootOrderId, Pageable inPageable);
    
    /**
     * Find most recent execution order summary for a root order ID where security type is not null.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inPageable a <code>Pageable</code> value
     * @return a <code>Page&lt;PersistentOrderSummary&gt;</code> value
     */
    Page<PersistentOrderSummary> findByRootOrderIdAndSecurityTypeIsNotNull(OrderID inRootOrderId, Pageable inPageable);
    
    /**
     * Find order summaries by security type, transaction time and viewer.
     *
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inDate a <code>Date</code> value
     * @param inViewer a <code>PersistentUser</code> value
     * @param inPageable a <code>Pageable</code> value
     * @return a <code>List&lt;PersistentOrderSummary&gt;</code> value
     */
    List<PersistentOrderSummary> findBySecurityTypeAndTransactTimeLessThanEqualAndViewer(
            SecurityType inSecurityType, Date inDate, PersistentUser inViewer, Pageable inPageable);
    
    /**
     * Find order summaries by security type, transaction time and viewer in given set.
     *
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inDate a <code>Date</code> value
     * @param inViewers a <code>Set&lt;PersistentUser&gt;</code> value
     * @param inPageable a <code>Pageable</code> value
     * @return a <code>List&lt;PersistentOrderSummary&gt;</code> value
     */
    List<PersistentOrderSummary> findBySecurityTypeAndTransactTimeLessThanEqualAndViewerIn(
            SecurityType inSecurityType, Date inDate, Set<PersistentUser> inViewers, Pageable inPageable);
    
    /**
     * Find order summaries by symbol, security type, transaction time less than equal and viewer.
     *
     * @param inSymbol a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inDate a <code>Date</code> value
     * @param inViewer a <code>PersistentUser</code> value
     * @return a <code>List&lt;PersistentOrderSummary&gt;</code> value
     */
    List<PersistentOrderSummary> findBySymbolAndSecurityTypeAndTransactTimeLessThanEqualAndViewer(
            String inSymbol, SecurityType inSecurityType, Date inDate, PersistentUser inViewer);
    
    /**
     * Find order summaries by symbol, security type, transaction time less than equal and viewer in given set.
     *
     * @param inSymbol a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inDate a <code>Date</code> value
     * @param inViewers a <code>Set&lt;PersistentUser&gt;</code> value
     * @return a <code>List&lt;PersistentOrderSummary&gt;</code> value
     */
    List<PersistentOrderSummary> findBySymbolAndSecurityTypeAndTransactTimeLessThanEqualAndViewerIn(
            String inSymbol, SecurityType inSecurityType, Date inDate, Set<PersistentUser> inViewers);
    
    /**
     * Find order summaries by symbol, expiry, security type, transaction time less than equal and viewer.
     * 
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inDate a <code>Date</code> value
     * @param inViewer a <code>PersistentUser</code> value
     * @return a <code>List&lt;PersistentOrderSummary&gt;</code> value
     */
    List<PersistentOrderSummary> findBySymbolAndExpiryAndSecurityTypeAndTransactTimeLessThanEqualAndViewer(
            String inSymbol, String inExpiry, SecurityType inSecurityType, Date inDate, PersistentUser inViewer);
    
    /**
     * Find order summaries by symbol, expiry, security type, transaction time less than equal and viewer in given set.
     *
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inDate a <code>Date</code> value
     * @param inViewers a <code>Set&lt;PersistentUser&gt;</code> value
     * @return a <code>List&lt;PersistentOrderSummary&gt;</code> value
     */
    List<PersistentOrderSummary> findBySymbolAndExpiryAndSecurityTypeAndTransactTimeLessThanEqualAndViewerIn(
            String inSymbol, String inExpiry, SecurityType inSecurityType, Date inDate, Set<PersistentUser> inViewers);
    
    /**
     * Find order summaries by symbol, expiry, strike price, option type, security type, transaction time, and viewer.
     *
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inStrikePrice a <code>java.math.BigDecimal</code> value
     * @param inOptionType an <code>org.marketcetera.trade.OptionType</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inDate a <code>Date</code> value
     * @param inViewer a <code>PersistentUser</code> value
     * @return a <code>List&lt;PersistentOrderSummary&gt;</code> value
     */
    List<PersistentOrderSummary> findBySymbolAndExpiryAndStrikePriceAndOptionTypeAndSecurityTypeAndTransactTimeLessThanEqualAndViewer(
            String inSymbol, String inExpiry, java.math.BigDecimal inStrikePrice, org.marketcetera.trade.OptionType inOptionType, 
            SecurityType inSecurityType, Date inDate, PersistentUser inViewer);
    
    /**
     * Find order summaries by symbol, expiry, strike price, option type, security type, transaction time, and viewer in given set.
     *
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inStrikePrice a <code>java.math.BigDecimal</code> value
     * @param inOptionType an <code>org.marketcetera.trade.OptionType</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inDate a <code>Date</code> value
     * @param inViewers a <code>Set&lt;PersistentUser&gt;</code> value
     * @return a <code>List&lt;PersistentOrderSummary&gt;</code> value
     */
    List<PersistentOrderSummary> findBySymbolAndExpiryAndStrikePriceAndOptionTypeAndSecurityTypeAndTransactTimeLessThanEqualAndViewerIn(
            String inSymbol, String inExpiry, java.math.BigDecimal inStrikePrice, org.marketcetera.trade.OptionType inOptionType,
            SecurityType inSecurityType, Date inDate, Set<PersistentUser> inViewers);
}
