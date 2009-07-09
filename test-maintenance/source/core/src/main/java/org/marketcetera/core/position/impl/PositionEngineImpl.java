package org.marketcetera.core.position.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.IncomingPositionSupport;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.Trade;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcher;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcherFactory;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.FunctionList.AdvancedFunction;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.util.concurrent.Lock;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Position engine implementation.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public final class PositionEngineImpl implements PositionEngine {

    /**
     * Comparator for PositionRows that imposes a default ordering of the data.
     */
    @ClassVersion("$Id$")
    private final static class PositionRowComparator implements Comparator<PositionRow> {

        @Override
        public int compare(PositionRow o1, PositionRow o2) {
            return new CompareToBuilder().append(o1.getTraderId(), o2.getTraderId()).append(
                    o1.getSymbol(), o2.getSymbol()).append(o1.getAccount(), o2.getAccount())
                    .toComparison();
        }
    }
    
    /**
     * Supports grouping of trades by trader id, symbol, and account.
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
     * Supports grouping of positions by a number of grouping criteria.
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

    private final MarketDataSupport mMarketDataSupport;
    private final IncomingPositionSupport mIncomingPositionSupport;
    private final SortedList<Trade> mSorted;
    private final GroupingList<Trade> mGrouped;
    private final EventList<PositionRow> mPositionsBase;
    private final EventList<PositionRow> mFlatView;
    private final Map<PositionKey, PositionRowUpdater> mPositions = Maps.newHashMap();

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
            IncomingPositionSupport incomingPositionSupport, MarketDataSupport marketDataSupport) {
        Validate
                .noNullElements(new Object[] { trades, incomingPositionSupport, marketDataSupport });
        mMarketDataSupport = marketDataSupport;
        mIncomingPositionSupport = incomingPositionSupport;
        mSorted = new SortedList<Trade>(trades, new Comparator<Trade>() {

            @Override
            public int compare(Trade o1, Trade o2) {
                return new Long(o1.getSequenceNumber()).compareTo(new Long(o2.getSequenceNumber()));
            }
        });
        mGrouped = new GroupingList<Trade>(mSorted, new TradeGroupMatcherFactory());
        mPositionsBase = new BasicEventList<PositionRow>(mGrouped.getReadWriteLock());
        for (PositionKey key : mIncomingPositionSupport.getIncomingPositions().keySet()) {
            addPosition(key, null);
        }
        for (EventList<Trade> trade : mGrouped) {
            addPosition(trade);
        }
        mGrouped.addListEventListener(new ListEventListener<EventList<Trade>>() {
            @Override
            public void listChanged(ListEvent<EventList<Trade>> listChanges) {
                while (listChanges.next()) {
                    if (listChanges.getType() == ListEvent.INSERT) {
                        EventList<Trade> newTrades = listChanges.getSourceList().get(
                                listChanges.getIndex());
                        addPosition(newTrades);
                    }
                }
            }
        });
        mFlatView = new ObservableElementList<PositionRow>(new SortedList<PositionRow>(
                mPositionsBase, new PositionRowComparator()), GlazedLists.beanConnector(
                PositionRow.class, true, "positionMetrics")); //$NON-NLS-1$
    }

    private void addPosition(EventList<Trade> trades) {
        PositionKey key = getKey(trades);
        PositionRowUpdater updater = mPositions.get(key);
        if (updater == null) {
            addPosition(key, trades);
        } else {
            updater.connect(trades);
        }
    }

    private PositionKey getKey(EventList<Trade> sourceValue) {
        Trade trade = sourceValue.get(0);
        return new PositionKeyImpl(trade.getSymbol(), trade.getAccount(), trade.getTraderId());
    }

    private void addPosition(PositionKey key, EventList<Trade> trades) {
        PositionRowImpl positionRow = new PositionRowImpl(key.getSymbol(), key.getAccount(),
                key.getTraderId(), mIncomingPositionSupport.getIncomingPositionFor(key));
        PositionRowUpdater updater = new PositionRowUpdater(positionRow, trades,
                mMarketDataSupport);
        mPositions.put(key, updater);
        mPositionsBase.add(positionRow);
    }

    @Override
    public PositionData getFlatData() {
        return new PositionData() {

            @Override
            public EventList<PositionRow> getPositions() {
                return mFlatView;
            }

            @Override
            public void dispose() {
                // nothing to do, the flat view is kept in memory
            }
        };
    }

    @Override
    public PositionData getGroupedData(Grouping... groupings) {
        Validate.noNullElements(groupings);
        if (groupings.length != 2 || groupings[0] == groupings[1]) {
            throw new UnsupportedOperationException();
        }
        Lock lock = mFlatView.getReadWriteLock().readLock();
        lock.lock();
        try {
            final GroupingList<PositionRow> grouped = new GroupingList<PositionRow>(mFlatView,
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
        mPositionsBase.dispose();
        mGrouped.dispose();
        mSorted.dispose();
        mMarketDataSupport.dispose();
    }
}
