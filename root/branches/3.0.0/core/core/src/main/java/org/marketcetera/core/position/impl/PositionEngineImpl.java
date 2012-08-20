package org.marketcetera.core.position.impl;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Map;

import ca.odell.glazedlists.impl.beans.BeanConnector;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.Matchers;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.instruments.UnderlyingSymbolSupport;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.IncomingPositionSupport;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.Trade;
import org.marketcetera.fork.glazed.GroupingList;
import org.marketcetera.fork.glazed.GroupingList.GroupMatcher;
import org.marketcetera.fork.glazed.GroupingList.GroupMatcherFactory;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.api.attributes.ClassVersion;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FunctionList;
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
 * @version $Id: PositionEngineImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
@ClassVersion("$Id: PositionEngineImpl.java 16063 2012-01-31 18:21:55Z colin $")
public final class PositionEngineImpl implements PositionEngine {

    /**
     * Comparator for PositionRows that imposes a default ordering of the data.
     */
    @ClassVersion("$Id: PositionEngineImpl.java 16063 2012-01-31 18:21:55Z colin $")
    private final static class PositionRowComparator implements
            Comparator<PositionRow> {

        @Override
        public int compare(PositionRow o1, PositionRow o2) {
            return new CompareToBuilder().append(o1.getTraderId(),
                    o2.getTraderId()).append(o1.getUnderlying(),
                    o2.getUnderlying())
                    .append(o1.getAccount(), o2.getAccount()).toComparison();
        }
    }

    /**
     * Supports grouping of trades by trader id, symbol, and account.
     */
    @ClassVersion("$Id: PositionEngineImpl.java 16063 2012-01-31 18:21:55Z colin $")
    private final static class TradeGroupMatcher implements
            GroupMatcher<Trade<?>> {

        private final PositionKey<?> mKey;

        public TradeGroupMatcher(Trade<?> trade) {
            mKey = trade.getPositionKey();
        }

        @Override
        public boolean matches(Trade<?> item) {
            return internalCompare(item.getPositionKey()) == 0;
        }

        @Override
        public int compareTo(GroupMatcher<Trade<?>> o) {
            TradeGroupMatcher rhs = (TradeGroupMatcher) o;
            return internalCompare(rhs.mKey);
        }

        private int internalCompare(PositionKey<?> key) {
            return PositionKeyComparator.INSTANCE.compare(mKey, key);
        }
    }

    /**
     * Creates group matchers from trades. Used by {@link org.marketcetera.fork.glazed.GroupingList}.
     */
    @ClassVersion("$Id: PositionEngineImpl.java 16063 2012-01-31 18:21:55Z colin $")
    private final static class TradeGroupMatcherFactory implements
            GroupMatcherFactory<Trade<?>, GroupMatcher<Trade<?>>> {

        @Override
        public TradeGroupMatcher createGroupMatcher(final Trade<?> element) {
            return new TradeGroupMatcher(element);
        }
    };

    /**
     * Supports grouping of positions by a number of grouping criteria.
     */
    @ClassVersion("$Id: PositionEngineImpl.java 16063 2012-01-31 18:21:55Z colin $")
    private final static class GroupingMatcher implements
            GroupMatcher<PositionRow> {

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
            return new CompareToBuilder().append(mValues, other.mValues)
                    .toComparison();
        }
    }

    /**
     * Creates group matchers from position rows.
     */
    @ClassVersion("$Id: PositionEngineImpl.java 16063 2012-01-31 18:21:55Z colin $")
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
     * Converts an {@link EventList} of positions into a dynamically updated
     * summary {@link PositionRow}. Used by {@link FunctionList}.
     */
    @ClassVersion("$Id: PositionEngineImpl.java 16063 2012-01-31 18:21:55Z colin $")
    private final static class SummarizeFunction implements
            AdvancedFunction<EventList<PositionRow>, PositionRow> {

        private final Map<EventList<PositionRow>, SummaryRowUpdater> map = new IdentityHashMap<EventList<PositionRow>, SummaryRowUpdater>();
        private final Grouping[] mGrouping;

        public SummarizeFunction(Grouping... grouping) {
            mGrouping = grouping;
        }

        @Override
        public void dispose(EventList<PositionRow> sourceValue,
                PositionRow transformedValue) {
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
            // use the key data from the first row...it won't exactly match all
            // rows, but it's sufficient for further grouping
            PositionRowImpl summary = new PositionRowImpl(row.getInstrument(),
                    row.getUnderlying(), row.getAccount(), row.getTraderId(), mGrouping, sourceValue);
            SummaryRowUpdater calculator = new SummaryRowUpdater(summary);
            map.put(sourceValue, calculator);
            return calculator.getSummary();
        }
    }

    private final MarketDataSupport mMarketDataSupport;
    private final IncomingPositionSupport mIncomingPositionSupport;
    private final UnderlyingSymbolSupport mUnderlyingSymbolSupport;
    private final SortedList<Trade<?>> mSorted;
    private final GroupingList<Trade<?>> mGrouped;
    private final EventList<PositionRow> mPositionsBase;
    private final EventList<PositionRow> mFlatView;
    private final Map<PositionKey<?>, PositionRowUpdater> mPositions = Maps
            .newHashMap();

    /**
     * Constructor.
     * 
     * @param trades
     *            base list of reports to drive the positions lists, cannot be
     *            null
     * @param incomingPositionSupport
     *            support for incoming positions, cannot be null
     * @param marketDataSupport
     *            support for market data, cannot be null
     * @param underlyingSymbolSupport
     *            support for underlying symbol, cannot be null
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public PositionEngineImpl(EventList<Trade<?>> trades,
            IncomingPositionSupport incomingPositionSupport,
            MarketDataSupport marketDataSupport,
            UnderlyingSymbolSupport underlyingSymbolSupport) {
        Validate.noNullElements(new Object[] { trades, incomingPositionSupport,
                marketDataSupport, underlyingSymbolSupport });
        mMarketDataSupport = marketDataSupport;
        mIncomingPositionSupport = incomingPositionSupport;
        mUnderlyingSymbolSupport = underlyingSymbolSupport;
        mSorted = new SortedList<Trade<?>>(trades, new Comparator<Trade<?>>() {

            @Override
            public int compare(Trade<?> o1, Trade<?> o2) {
                return new Long(o1.getSequenceNumber()).compareTo(new Long(o2
                        .getSequenceNumber()));
            }
        });
        mGrouped = new GroupingList<Trade<?>>(mSorted,
                new TradeGroupMatcherFactory());
        mPositionsBase = new BasicEventList<PositionRow>(mGrouped
                .getReadWriteLock());
        for (PositionKey<?> key : mIncomingPositionSupport
                .getIncomingPositions().keySet()) {
            addPosition(key, null);
        }
        for (EventList<Trade<?>> trade : mGrouped) {
            addPosition(trade);
        }
        mGrouped
                .addListEventListener(new ListEventListener<EventList<Trade<?>>>() {
                    @Override
                    public void listChanged(
                            ListEvent<EventList<Trade<?>>> listChanges) {
                        while (listChanges.next()) {
                            if (listChanges.getType() == ListEvent.INSERT) {
                                EventList<Trade<?>> newTrades = listChanges
                                        .getSourceList().get(
                                                listChanges.getIndex());
                                addPosition(newTrades);
                            }
                        }
                    }
                });
        final Matcher<PropertyChangeEvent> byNameMatcher = Matchers.propertyEventNameMatcher(true, "positionMetrics");
        mFlatView = new ObservableElementList<PositionRow>(
                new SortedList<PositionRow>(mPositionsBase,
                        new PositionRowComparator()), new BeanConnector<PositionRow>(PositionRow.class, byNameMatcher)); //$NON-NLS-1$
    }

    private void addPosition(EventList<Trade<?>> trades) {
        PositionKey<?> key = getKey(trades);
        PositionRowUpdater updater = mPositions.get(key);
        if (updater == null) {
            addPosition(key, trades);
        } else {
            updater.connect(trades);
        }
    }

    private PositionKey<?> getKey(EventList<Trade<?>> sourceValue) {
        Trade<?> trade = sourceValue.get(0);
        return trade.getPositionKey();
    }

    private void addPosition(PositionKey<?> key, EventList<Trade<?>> trades) {
        Instrument instrument = key.getInstrument();
        String underlying = mUnderlyingSymbolSupport.getUnderlying(instrument);
        PositionRowImpl positionRow = new PositionRowImpl(instrument,
                underlying,
                key.getAccount(), key.getTraderId(), mIncomingPositionSupport
                        .getIncomingPositionFor(key));
        PositionRowUpdater updater = new PositionRowUpdater(positionRow,
                trades, mMarketDataSupport);
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
        /*
         * The API accepts two groupings, i.e. Underlying, then Account. We
         * compute the other implied one because it is needed to do the
         * matching.
         */
        EnumSet<Grouping> complements = EnumSet.complementOf(EnumSet.of(
                groupings[0], groupings[1]));
        if (complements.size() != 1) {
            throw new IllegalStateException();
        }
        Grouping complement = complements.iterator().next();
        /*
         * This builds up a chain of lists off of mFlatView, each grouping and
         * summarizing its children. 
         * 
         * We are essentially builds a tree from the
         * bottom up. The leaf nodes are the positions, i.e. the elements in
         * mFlatView. Intermediary nodes are summaries of their children.
         */
        Lock lock = mFlatView.getReadWriteLock().readLock();
        lock.lock();
        try {
            /*
             * The first grouping is on all Grouping values, i.e. partition the
             * positions into groups that match on everything.
             */
            final GroupingList<PositionRow> grouped1 = new GroupingList<PositionRow>(
                    mFlatView, new GroupingMatcherFactory(groupings[0], groupings[1], complement));
            /*
             * Now make a new list that has one element for each group that
             * summarizes the position values.  These form the next level of tree nodes.
             */
            final FunctionList<EventList<PositionRow>, PositionRow> summarized1 = new FunctionList<EventList<PositionRow>, PositionRow>(
                    grouped1, new SummarizeFunction(groupings[0], groupings[1], complement));
            /*
             * Partition the above summary list into groups that match on the
             * two provided values.
             */
            final GroupingList<PositionRow> grouped2 = new GroupingList<PositionRow>(
                    summarized1, new GroupingMatcherFactory(groupings[0],
                            groupings[1]));
            /*
             * Summarize the new groups to make the next level of tree nodes.
             */
            final FunctionList<EventList<PositionRow>, PositionRow> summarized2 = new FunctionList<EventList<PositionRow>, PositionRow>(
                    grouped2, new SummarizeFunction(groupings[0], groupings[1]));
            /*
             * Partition the above summary list into groups that match on the
             * final grouping.
             */
            final GroupingList<PositionRow> grouped3 = new GroupingList<PositionRow>(
                    summarized2, new GroupingMatcherFactory(groupings[0]));
            /*
             * Summarize the final groups for the top level nodes.
             */
            final FunctionList<EventList<PositionRow>, PositionRow> summarized3 = new FunctionList<EventList<PositionRow>, PositionRow>(
                    grouped3, new SummarizeFunction(groupings[0]));

            return new PositionData() {

                @Override
                public EventList<PositionRow> getPositions() {
                    return summarized3;
                }

                @Override
                public void dispose() {
                    summarized3.dispose();
                    grouped3.dispose();
                    summarized2.dispose();
                    grouped2.dispose();
                    summarized1.dispose();
                    grouped1.dispose();
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
