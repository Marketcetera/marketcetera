package org.marketcetera.ors.dao;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.history.ExecutionReportSummary;
import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Option;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 10/21/13
 * Time: 9:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutionReportDao {

     int deleteReportsFor(PersistentReport report);

     int deleteReportsIn(List<PersistentReport> inReports);

     BigDecimal getEquityPositionAsOf (final SimpleUser inUser, final Date inDate,final Equity inEquity);

     List<ExecutionReportSummary> getOpenOrders(SimpleUser inUser);

     Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf(SimpleUser inUser,Date inDate);

     BigDecimal getCurrencyPositionAsOf(SimpleUser inUser, Date inDate, Currency inCurrency);

     Map<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf(SimpleUser inUser, Date inDate);

     Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(
            SimpleUser inUser, Date inDate);

     BigDecimal getFuturePositionAsOf(SimpleUser inUser, Date inDate,
                                            Future inFuture);

     BigDecimal getOptionPositionAsOf(SimpleUser inUser, Date inDate,
                                            Option inOption);

     Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(
            SimpleUser inUser, Date inDate);

     Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(
            SimpleUser inUser, Date inDate, String[] inSymbols);

}
