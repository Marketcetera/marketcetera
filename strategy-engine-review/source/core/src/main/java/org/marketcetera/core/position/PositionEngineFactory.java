package org.marketcetera.core.position;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.core.position.impl.Messages;
import org.marketcetera.core.position.impl.PositionEngineImpl;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.FunctionList.Function;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.util.concurrent.Lock;

/* $License$ */

/**
 * Factory for creating position engines.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class PositionEngineFactory {

    /**
     * Create a position engine for a dynamic list of trades.
     * 
     * @param trades
     *            list of trades, cannot be null
     * @param incomingPositionSupport
     *            support for incoming positions, cannot be null
     * @return a position engine
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public static PositionEngine create(EventList<Trade> trades,
            IncomingPositionSupport incomingPositionSupport, MarketDataSupport marketDataSupport) {
        Validate.noNullElements(new Object[] { trades, incomingPositionSupport });
        Lock readLock = trades.getReadWriteLock().readLock();
        readLock.lock();
        try {
            FilterList<Trade> validTrades = new FilterList<Trade>(trades, new ValidationMatcher());
            return new PositionEngineImpl(validTrades, incomingPositionSupport, marketDataSupport);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Convenience method when using reports.
     * 
     * @param reports
     *            list of reports, cannot be null
     * @param incomingPositionSupport
     *            support for incoming positions, cannot be null
     * @return a position engine
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public static PositionEngine createFromReports(EventList<ReportBase> reports,
            IncomingPositionSupport incomingPositionSupport, MarketDataSupport marketDataSupport) {
        Validate.noNullElements(new Object[] { reports, incomingPositionSupport });
        Lock readLock = reports.getReadWriteLock().readLock();
        readLock.lock();
        try {
            FilterList<ReportBase> fills = new FilterList<ReportBase>(reports, new FillMatcher());
            FunctionList<ReportBase, Trade> trades = new FunctionList<ReportBase, Trade>(fills,
                    new TradeFunction());
            return create(trades, incomingPositionSupport, marketDataSupport);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Convenience method when using report holders.
     * 
     * @param holders
     *            list of report holders, cannot be null
     * @param incomingPositionSupport
     *            support for incoming positions, cannot be null
     * @return a position engine
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public static PositionEngine createFromReportHolders(EventList<ReportHolder> holders,
            IncomingPositionSupport incomingPositionSupport, MarketDataSupport marketDataSupport) {
        Validate.noNullElements(new Object[] { holders, incomingPositionSupport });
        Lock readLock = holders.getReadWriteLock().readLock();
        readLock.lock();
        try {
            FunctionList<ReportHolder, ReportBase> reports = new FunctionList<ReportHolder, ReportBase>(
                    holders, new ReportExtractor());
            return createFromReports(reports, incomingPositionSupport, marketDataSupport);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Function extracting reports from report holders.
     */
    @ClassVersion("$Id$")
    private final static class ReportExtractor implements Function<ReportHolder, ReportBase> {

        @Override
        public ReportBase evaluate(ReportHolder sourceValue) {
            return sourceValue.getReport();
        }

    }

    /**
     * Matcher that matches fills and partial fills.
     */
    @ClassVersion("$Id$")
    private final static class FillMatcher implements Matcher<ReportBase> {

        @Override
        public boolean matches(ReportBase item) {
            OrderStatus orderStatus = item.getOrderStatus();
            return item instanceof ExecutionReport
                    && (orderStatus == OrderStatus.PartiallyFilled || orderStatus == OrderStatus.Filled);
        }
    }

    /**
     * Matcher that matches valid trades, as specified by the {@link Trade} interface.
     */
    @ClassVersion("$Id$")
    private final static class ValidationMatcher implements Matcher<Trade> {

        @Override
        public boolean matches(Trade item) {
            if (notEmpty(item.getSymbol()) && positive(item.getPrice())
                    && notZero(item.getQuantity())) {
                return true;
            } else {
                Messages.VALIDATION_MATCHER_INVALID_TRADE.error(this, item);
                return false;
            }
        }

        private boolean notEmpty(String string) {
            return StringUtils.isNotEmpty(string);
        }

        private boolean notNull(Object object) {
            return object != null;
        }

        private boolean notZero(BigDecimal number) {
            return notNull(number) && number.signum() != 0;
        }

        private boolean positive(BigDecimal number) {
            return notNull(number) && number.signum() == 1;
        }
    }

    /**
     * Function mapping execution reports to trades.
     * <p>
     * Note that even though the parameter type is ReportBase, the source elements must be
     * ExecutionReport that represent fills or partial fills. Use {@link FillMatcher} to ensure
     * this.
     */
    @ClassVersion("$Id$")
    private final static class TradeFunction implements Function<ReportBase, Trade> {

        @Override
        public Trade evaluate(ReportBase sourceValue) {
            return new ExecutionReportAdapter((ExecutionReport) sourceValue);
        }
    }

    /**
     * Adapts an {@link ExecutionReport} to be used as a Trade.
     * 
     * Note: this class may break the contract of Trade, knowing that invalid trades will be
     * filtered out in {@link PositionEngineFactory#create(EventList, IncomingPositionSupport)}.
     */
    private final static class ExecutionReportAdapter implements Trade {

        private final ExecutionReport mReport;

        /**
         * Constructor.
         * 
         * @param report
         *            execution report to adapt
         */
        public ExecutionReportAdapter(ExecutionReport report) {
            mReport = report;
        }

        @Override
        public String getSymbol() {
            MSymbol symbol = mReport.getSymbol();
            return symbol == null ? null : symbol.toString();
        }

        @Override
        public String getAccount() {
            return mReport.getAccount();
        }

        @Override
        public String getTraderId() {
            // use viewer id since the viewer is the originator, the one the position is associated
            // with
            UserID viewer = mReport.getViewerID();
            return viewer == null ? null : viewer.toString();
        }

        @Override
        public BigDecimal getPrice() {
            return mReport.getLastPrice();
        }

        @Override
        public BigDecimal getQuantity() {
            BigDecimal lastQuantity = mReport.getLastQuantity();
            if (lastQuantity != null) {
                switch (mReport.getSide()) {
                case Buy:
                    return lastQuantity;
                case Sell:
                case SellShort:
                case SellShortExempt:
                    return lastQuantity.negate();
                }
            }
            return null;
        }

        @Override
        public long getSequenceNumber() {
            ReportID reportId = mReport.getReportID();
            return reportId == null ? null : reportId.longValue();
        }

        @Override
        public String toString() {
            return Messages.EXECUTION_REPORT_ADAPTER_TO_STRING.getText(String.valueOf(getSymbol()),
                    String.valueOf(getAccount()), String.valueOf(getTraderId()), String
                            .valueOf(getPrice()), String.valueOf(getQuantity()), String
                            .valueOf(getSequenceNumber()), mReport.toString());
        }

    }
}
