package org.marketcetera.core.position.impl;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.Trade;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcher;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcherFactory;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.FunctionList.AdvancedFunction;

/* $License$ */

/**
 * Position engine that works off an {@link EventList} of {@link ReportHolder}
 * (what {@link TradeReportsHistory} provides).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class PositionEngineImpl implements PositionEngine {

    /**
     * Supports grouping of trades by trader id, symbol, and account.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private final static class TradeGroupMatcher implements GroupMatcher<Trade> {

        private final String traderId;
        private final String symbol;
        private final String account;

        public TradeGroupMatcher(Trade trade) {
            this.traderId = trade.getTraderId();
            this.symbol = trade.getSymbol();
            this.account = defaultString(trade.getAccount());
        }

        @Override
        public boolean matches(Trade item) {
            return internalCompare(item.getTraderId(), item.getSymbol(), defaultString(item
                    .getAccount())) == 0;
        }

        @Override
        public int compareTo(GroupMatcher<Trade> o) {
            TradeGroupMatcher other = (TradeGroupMatcher) o;
            return internalCompare(other.traderId, other.symbol, defaultString(other.account));
        }

        private static String defaultString(String string) {
            return (String) ObjectUtils.defaultIfNull(string, ""); //$NON-NLS-1$
        }

        private int internalCompare(String traderId, String symbol, String account) {
            int result = this.traderId.compareTo(traderId);
            if (result == 0) {
                result = this.symbol.compareTo(symbol);
                if (result == 0) {
                    result = this.account.compareTo(account);
                }
            }
            return result;
        }

    }

    /**
     * Creates group matchers from trades. Used by {@link GroupingList}.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private final static class TradeGroupMatcherFactory implements
            GroupMatcherFactory<Trade, GroupMatcher<Trade>> {

        @Override
        public TradeGroupMatcher createGroupMatcher(final Trade element) {
            return new TradeGroupMatcher(element);
        }
    };

    /**
     * Converts an {@link EventList} of trades into a dynamically updated
     * {@link PositionRow}. Used by {@link FunctionList}.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private final static class PositionFunction implements
            AdvancedFunction<EventList<Trade>, PositionRow> {

        private Map<EventList<Trade>, PositionRowUpdater> map = new IdentityHashMap<EventList<Trade>, PositionRowUpdater>();
        private final PositionMarketData marketData;

        public PositionFunction(PositionMarketData marketData) {
            this.marketData = marketData;
        }

        @Override
        public void dispose(EventList<Trade> sourceValue, PositionRow transformedValue) {
            PositionRowUpdater calc = map.remove(sourceValue);
            calc.dispose();
        }

        @Override
        public PositionRow reevaluate(EventList<Trade> sourceValue, PositionRow transformedValue) {
            return map.get(sourceValue).getPosition();
        }

        @Override
        public PositionRow evaluate(EventList<Trade> sourceValue) {
            PositionRowUpdater calculator = new PositionRowUpdater(sourceValue, marketData);
            map.put(sourceValue, calculator);
            return calculator.getPosition();
        }

    }

    private final FunctionList<EventList<Trade>, PositionRow> flat;
    private final PositionMarketData marketData;

    /**
     * Constructor.
     * 
     * @param base
     *            base list of reports to drive the positions lists
     */
    public PositionEngineImpl(EventList<Trade> trades) {
        marketData = new PositionMarketDataImpl();
        SortedList<Trade> sorted = new SortedList<Trade>(trades, new Comparator<Trade>() {

            @Override
            public int compare(Trade o1, Trade o2) {
                long val1 = o1.getSequenceNumber();
                long val2 = o2.getSequenceNumber();
                return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
            }
        });
        GroupingList<Trade> grouped = new GroupingList<Trade>(sorted,
                new TradeGroupMatcherFactory());
        flat = new FunctionList<EventList<Trade>, PositionRow>(grouped, new PositionFunction(
                marketData));
    }

    @Override
    public FlatPositionData getFlatData() {
        return new FlatPositionData() {

            @Override
            public EventList<PositionRow> getPositions() {
                return flat;
            }

            @Override
            public void dispose() {
                // TODO: nothing for now, later this should cleanup market data
                // when possible
            }
        };
    }

    @Override
    public GroupedPositionData getGroupedData(Grouping... groupings) {
        throw new UnsupportedOperationException();
    }

}
