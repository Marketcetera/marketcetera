package org.marketcetera.ors.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to reports.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ReportService
{
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
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>List&lt;ReportBase&gt;</code> value
     */
    public List<ReportBase> getReportsSince(SimpleUser inUser,
                                            Date inDate);
    /**
     * Gets the position of the given equity as of the given date from the point of view
     * of the given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inEquity an <code>Equity</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getEquityPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Equity inEquity);
    /**
     * Gets the open orders visible to the given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @return a <code>List&lt;ReportBaseImpl</code> value
     */
    public List<ReportBaseImpl> getOpenOrders(SimpleUser inUser);
    /**
     * Gets all equity positions as of the given date visible to the given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;Equity&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(SimpleUser inUser,
                                                                         Date inDate);
    /**
     * Gets the position of the given currency as of the given date visible to the
     * given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inCurrency a <code>Currency</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCurrencyPositionAsOf(SimpleUser inUser,
                                              Date inDate,
                                              Currency inCurrency);
    /**
     * Gets all currency positions as of the given date visible to the given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;Currency&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(SimpleUser inUser,
                                                                             Date inDate);
    /**
     * Gets all future positions as of the given date visible to the given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;Future&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(SimpleUser inUser,
                                                                         Date inDate);
    /**
     * Gets the position of the given future as of the given date visible to the
     * given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inFuture a <code>Future</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getFuturePositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Future inFuture);
    /**
     * Gets the position of the given option as of the given date visible to the
     * given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inOption an <code>Option</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOptionPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Option inOption);
    /**
     * Gets all option positions as of the given date visible to the given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>Map&lt;PositionKey&lt;Option&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(SimpleUser inUser,
                                                                         Date inDate);
    /**
     * Gets the positions of the options of the given root symbols as of the given date
     * visible to the given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inSymbols a <code>String[]</code> value
     * @return a <code>Map&lt;PositionKey&lt;Option&gt;,BigDecimal&lt;</code> value
     */
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(SimpleUser inUser,
                                                                      Date inDate,
                                                                      String[] inSymbols);
    /**
     * Saves the given report.
     *
     * @param inReport a <code>ReportBase</code> value
     */
    public PersistentReport save(ReportBase inReport);
    /**
     * Deletes the given report.
     *
     * @param inReport a <code>ReportBase</code> value
     */
    public void delete(ReportBase inReport);
    /**
     * Gets the principals established for the given order.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return a <code>Principals</code> value
     */
    public Principals getPrincipals(OrderID inOrderID);
}
