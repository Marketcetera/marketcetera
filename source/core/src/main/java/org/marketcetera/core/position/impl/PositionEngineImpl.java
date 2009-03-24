package org.marketcetera.core.position.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.IncomingPositionSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.Trade;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcher;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcherFactory;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.FunctionList.AdvancedFunction;
import ca.odell.glazedlists.util.concurrent.Lock;

/* $License$ */

/**
 * Position engine implementation.
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

        private final String mTraderId;
        private final String mSymbol;
        private final String mAccount;

        public TradeGroupMatcher(Trade trade) {
            this.mTraderId = trade.getTraderId();
            this.mSymbol = trade.getSymbol();
            this.mAccount = trade.getAccount();
        }

        @Override
        public boolean matches(Trade item) {
            return internalCompare(item.getTraderId(), item.getSymbol(), item.getAccount()) == 0;
        }

        @Override
        public int compareTo(GroupMatcher<Trade> o) {
            TradeGroupMatcher rhs = (TradeGroupMatcher) o;
            return internalCompare(rhs.mTraderId, rhs.mSymbol, rhs.mAccount);
        }

        private int internalCompare(String traderId, String symbol, String account) {
            return new CompareToBuilder().append(mTraderId, traderId).append(mSymbol, symbol)
                    .append(mAccount, account).toComparison();
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
     * Converts an {@link EventList} of trades into a dynamically updated {@link PositionRow}. Used
     * by {@link FunctionList}.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private final class PositionFunction implements AdvancedFunction<EventList<Trade>, PositionRow> {

        private Map<EventList<Trade>, PositionRowUpdater> map = new IdentityHashMap<EventList<Trade>, PositionRowUpdater>();

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
            PositionKey key = getKey(sourceValue);
            PositionRowImpl positionRow = new PositionRowImpl(key.getSymbol(), key.getAccount(),
                    key.getTraderId(), mIncomingPositionSupport.getIncomingPositionFor(key));
            PositionRowUpdater calculator = new PositionRowUpdater(positionRow, sourceValue,
                    mMarketData);
            map.put(sourceValue, calculator);
            return calculator.getPosition();
        }

        private PositionKey getKey(EventList<Trade> sourceValue) {
            Trade trade = sourceValue.get(0);
            return new PositionKeyImpl(trade.getSymbol(), trade.getAccount(), trade.getTraderId());
        }
    }

    /**
     * Supports grouping of positions by a number of grouping criteria.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private final static class GroupingMatcher implements GroupMatcher<PositionRow> {

        private final String[] mValues;
        private final Grouping[] mGrouping;

        public GroupingMatcher(PositionRow row, Grouping... grouping) {
            mGrouping = grouping;
            mValues = getValues(row);
        }

        private String[] getValues(PositionRow item) {
            String[] values = new String[mGrouping.length];
            for (int i = 0; i < mGrouping.length; i++) {
                values[i] = mGrouping[i].get(item);
            }
            return values;
        }

        @Override
        public boolean matches(PositionRow item) {
            return Arrays.equals(mValues, getValues(item));
        }

        @Override
        public int compareTo(GroupMatcher<PositionRow> o) {
            GroupingMatcher other = (GroupingMatcher) o;
            return new CompareToBuilder().append(mValues, other.mValues).toComparison();
        }
    }

    /**
     * Creates group matchers from position rows.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private final static class GroupingMatcherFactory implements
            GroupMatcherFactory<PositionRow, GroupMatcher<PositionRow>> {

        private final Grouping[] mGroupings;

        public GroupingMatcherFactory(Grouping... groupings) {
            mGroupings = groupings;
        }

        @Override
        public GroupingMatcher createGroupMatcher(final PositionRow element) {
            return new GroupingMatcher(element, mGroupings);
        }
    }

    /**
     * Converts an {@link EventList} of positions into a dynamically updated summary
     * {@link PositionRow}. Used by {@link FunctionList}.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private final static class SummarizeFunction implements
            AdvancedFunction<EventList<PositionRow>, PositionRow> {

        private final Map<EventList<PositionRow>, SummaryRowUpdater> map = new IdentityHashMap<EventList<PositionRow>, SummaryRowUpdater>();
        private final Grouping[] mGrouping;

        public SummarizeFunction(Grouping... grouping) {
            mGrouping = grouping;
        }

        @Override
        public void dispose(EventList<PositionRow> sourceValue, PositionRow transformedValue) {
            SummaryRowUpdater calc = map.remove(sourceValue);
            calc.dispose();
        }

        @Override
        public PositionRow reevaluate(EventList<PositionRow> sourceValue,
                PositionRow transformedValue) {
            return map.get(sourceValue).getSummary();
        }

        @Override
        public PositionRow evaluate(EventList<PositionRow> sourceValue) {
            PositionRow row = sourceValue.get(0);
            // use the key data from the first row...it won't exactly match all rows, but it's
            // sufficient for further grouping
            PositionRowImpl summary = new PositionRowImpl(row.getSymbol(), row.getAccount(), row.getTraderId(),
                    mGrouping, sourceValue);
            SummaryRowUpdater calculator = new SummaryRowUpdater(summary);
            map.put(sourceValue, calculator);
            return calculator.getSummary();
        }
    }

    private final PositionMarketData mMarketData;
    private final IncomingPositionSupport mIncomingPositionSupport;
    private final SortedList<Trade> mSorted;
    private final GroupingList<Trade> mGrouped;
    private final FunctionList<EventList<Trade>, PositionRow> mFlat;

    /**
     * Constructor.
     * 
     * @param trades
     *            base list of reports to drive the positions lists, cannot be null
     * @param incomingPositionSupport
     *            support for incoming positions, cannot be null
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public PositionEngineImpl(EventList<Trade> trades,
            IncomingPositionSupport incomingPositionSupport) {
        Validate.noNullElements(new Object[] { trades, incomingPositionSupport });
        mMarketData = new PositionMarketDataImpl();
        mIncomingPositionSupport = incomingPositionSupport;
        mSorted = new SortedList<Trade>(trades, new Comparator<Trade>() {

            @Override
            public int compare(Trade o1, Trade o2) {
                return new Long(o1.getSequenceNumber()).compareTo(new Long(o2.getSequenceNumber()));
            }
        });
        mGrouped = new GroupingList<Trade>(mSorted, new TradeGroupMatcherFactory());
        mFlat = new FunctionList<EventList<Trade>, PositionRow>(mGrouped, new PositionFunction());
    }

    @Override
    public PositionData getFlatData() {
        return new PositionData() {

            @Override
            public EventList<PositionRow> getPositions() {
                return mFlat;
            }

            @Override
            public void dispose() {
                // TODO: cleanup market data when possible
            }
        };
    }

    @Override
    public PositionData getGroupedData(Grouping... groupings) {
        Validate.noNullElements(groupings);
        if (groupings.length != 2 || groupings[0] == groupings[1]) {
            throw new UnsupportedOperationException();
        }
        Lock lock = mFlat.getReadWriteLock().readLock();
        lock.lock();
        try {
            final GroupingList<PositionRow> grouped = new GroupingList<PositionRow>(mFlat,
                    new GroupingMatcherFactory(groupings));
            final FunctionList<EventList<PositionRow>, PositionRow> summarized = new FunctionList<EventList<PositionRow>, PositionRow>(
                    grouped, new SummarizeFunction(groupings));
            final GroupingList<PositionRow> groupedAgain = new GroupingList<PositionRow>(
                    summarized, new GroupingMatcherFactory(groupings[0]));
            final FunctionList<EventList<PositionRow>, PositionRow> summarizedAgain = new FunctionList<EventList<PositionRow>, PositionRow>(
                    groupedAgain, new SummarizeFunction(groupings[0]));

            return new PositionData() {

                @Override
                public EventList<PositionRow> getPositions() {
                    return summarizedAgain;
                }

                @Override
                public void dispose() {
                    // TODO: cleanup market data when possible
                    summarizedAgain.dispose();
                    groupedAgain.dispose();
                    summarized.dispose();
                    grouped.dispose();
                }
            };
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void dispose() {
        mFlat.dispose();
        mGrouped.dispose();
        mSorted.dispose();
    }
}
