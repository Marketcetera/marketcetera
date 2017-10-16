package org.marketcetera.trade.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.marketcetera.admin.User;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.IncomingMessage;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.HasMutableReportID;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.domain.Page;

import quickfix.SessionID;

/* $License$ */

/**
 * Provides access to reports.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReportService.java 17339 2017-08-10 02:14:34Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: ReportService.java 17339 2017-08-10 02:14:34Z colin $")
public interface ReportService
{
    /**
     * Saves the given report.
     *
     * @param inReport a <code>Report</code> value
     * @return a <code>Report</code> value
     */
    Report save(Report inReport);
    /**
     * Gets the report for the given report ID.
     *
     * @param inReportId a <code>ReportID</code> value
     * @return a <code>Report</code> value or <code>null</code>
     */
    Report getReportFor(ReportID inReportId);
    /**
     * Get the order status of the most recent order in the order chain to which the given order id belongs.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return an <code>OrderStatus</code> value
     */
    OrderStatus getOrderStatusForOrderChain(OrderID inOrderId);
    /**
     * Purges reports before the given date.
     *
     * @param inPurgeDate a <code>Date</code> value
     * @return an <code>int</code> value containing the number of reports purged
     */
    public int purgeReportsBefore(Date inPurgeDate);
    /**
     * Gets the reports visible to the given user since the given date.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>List&lt;ReportBase&gt;</code> value
     */
    public List<ReportBase> getReportsSince(User inUser,
                                            Date inDate);
    /**
     * Get the position of the given instrument as of the given date from the point of view of the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPositionAsOf(User inUser,
                                      Date inDate,
                                      Instrument inInstrument);
    /**
     * Gets the position of the given equity as of the given date from the point of view
     * of the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @param inEquity an <code>Equity</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getEquityPositionAsOf(User inUser,
                                            Date inDate,
                                            Equity inEquity);
    /**
     * Gets the open orders visible to the given user.
     *
     * @param inUser a <code>User</code> value
     * @return a <code>List&lt;ReportBaseImpl</code> value
     */
    public List<ReportBaseImpl> getOpenOrders(User inUser);
    /**
     * Gets all equity positions as of the given date visible to the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;Equity&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(User inUser,
                                                                         Date inDate);
    /**
     * Gets the position of the given currency as of the given date visible to the
     * given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @param inCurrency a <code>Currency</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCurrencyPositionAsOf(User inUser,
                                              Date inDate,
                                              Currency inCurrency);
    /**
     * Get all positions as of the given date visible to the given user.
     *
     * @param inUser a <code>User</code> value a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;? extends Instrument&gt;,BigDecimal&lt;</code> value
     */
    Map<PositionKey<? extends Instrument>,BigDecimal> getAllPositionsAsOf(User inUser,
                                                                          Date inDate);
    /**
     * Gets all currency positions as of the given date visible to the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;Currency&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(User inUser,
                                                                             Date inDate);
    /**
     * Gets all convertible bond positions as of the given date visible to the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;ConvertibleBond&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<ConvertibleBond>,BigDecimal> getAllConvertibleBondPositionsAsOf(User inUser,
                                                                                           Date inDate);
    /**
     * Gets all future positions as of the given date visible to the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;Future&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(User inUser,
                                                                         Date inDate);
    /**
     * Gets the position of the given future as of the given date visible to the
     * given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @param inFuture a <code>Future</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getFuturePositionAsOf(User inUser,
                                            Date inDate,
                                            Future inFuture);
    /**
     * Gets the position of the given convertible bond as of the given date visible to the
     * given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @param inConvertibleBond a <code>ConvertibleBond</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getConvertibleBondPositionAsOf(User inUser,
                                                     Date inDate,
                                                     ConvertibleBond inConvertibleBond);
    /**
     * Gets the position of the given option as of the given date visible to the
     * given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @param inOption an <code>Option</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOptionPositionAsOf(User inUser,
                                            Date inDate,
                                            Option inOption);
    /**
     * Gets all option positions as of the given date visible to the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;Option&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(User inUser,
                                                                         Date inDate);
    /**
     * Gets the positions of the options of the given root symbols as of the given date
     * visible to the given user.
     *
     * @param inUser a <code>User</code> value
     * @param inDate a <code>Date</code> value
     * @param inSymbols a <code>String[]</code> value
     * @return a <code>Map&lt;PositionKey&lt;Option&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(User inUser,
                                                                      Date inDate,
                                                                      String[] inSymbols);
    /**
     * Saves the given report.
     *
     * @param inReport a <code>ReportBase</code> value
     */
    public Report save(ReportBase inReport);
    /**
     * Delete the report with the given report ID.
     *
     * @param inReportId a <code>ReportID</code> value
     */
    void delete(ReportID inReportId);
    /**
     * Gets the order ID of the root of this order chain.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderID</code> value or <code>null</code>
     */
    OrderID getRootOrderIdFor(OrderID inOrderID);
    /**
     * Finds the last sequence number known for the given session since the given moment in time.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inDate a <code>Date</code> value
     * @return an <code>int</code> value
     */
    int findLastSequenceNumberFor(SessionID inSessionId,
                                  Date inDate);
    /**
     * Finds the ids of the unhandled incoming FIX messages of the given types for the given session since the given date.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessageTypes a <code>Set&lt;String&gt;</code> value
     * @param inSince a <code>Date</code> value
     * @return a <code>List&lt;Long&gt;</code> value
     */
    List<Long> findUnhandledIncomingMessageIds(SessionID inSessionId,
                                               Set<String> inMessageTypes,
                                               Date inSince);
    /**
     * Finds the incoming messages with the given ids.
     *
     * @param inIds a <code>Set&lt;Long&gt;</code> value
     * @return a <code>List&lt;IncomingMessage&gt;</code> value
     */
    List<IncomingMessage> findIncomingMessagesForIdIn(Set<Long> inIds);
    /**
     * Get a page of executions from oldest to newest.
     *
     * @param inPageNumber an <code>int</code> value
     * @param inPageSize an <code>int</code> value
     * @return a <code>Page&lt;ExecutionReportSummary&gt;</code> value
     */
    Page<? extends ExecutionReportSummary> getExecutions(int inPageNumber,
                                                         int inPageSize);
    /**
     * Assign a report id to the given report.
     *
     * @param inReport a <code>HasMutableReportID</code> value
     */
    void assignReportId(HasMutableReportID inReport);
    /**
     * Add the given report to the system data flow.
     * 
     * <p>Reports added this way will be added to the system data bus. Reports will be
     * persisted and become part of the system record. The report will be owned by the
     * given user.
     * 
     * <p><em>This will affect reported positions</em></p>.
     *
     * @param inMessage a <code>HasFIXMessage</code> value
     * @param inBrokerID a <code>BrokerID</code> value
     * @param inUserID a <code>UserID</code> value
     */
    void addReport(HasFIXMessage inMessage,
                   BrokerID inBrokerID,
                   UserID inUserId);
}
