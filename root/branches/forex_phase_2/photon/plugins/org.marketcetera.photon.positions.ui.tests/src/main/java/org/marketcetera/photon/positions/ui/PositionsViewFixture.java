package org.marketcetera.photon.positions.ui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.instruments.MockUnderlyingSymbolSupport;
import org.marketcetera.core.instruments.UnderlyingSymbolSupport;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.ImmutablePositionSupport;
import org.marketcetera.core.position.IncomingPositionSupport;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.MockTrade;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionEngineFactory;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.Trade;
import org.marketcetera.photon.internal.positions.ui.PositionsView;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.OSGITestUtil;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.osgi.framework.ServiceRegistration;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.util.concurrent.Lock;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;

public class PositionsViewFixture {
    private final SWTWorkbenchBot mBot = new SWTWorkbenchBot();
    private final SWTBotView mView;
    private final EventList<Trade<?>> mTrades = new BasicEventList<Trade<?>>();
    private final MockMarketDataSupport mMarketDataSupport = new MockMarketDataSupport();
    private final IncomingPositionSupport mIncomingPositionSupport = new ImmutablePositionSupport(
            ImmutableMap.<PositionKey<?>, BigDecimal> of());
    private final UnderlyingSymbolSupport mUnderlyingSymbolSupport = new MockUnderlyingSymbolSupport();
    private ServiceRegistration mMockService;
    private PositionsView mRealView;

    public PositionsViewFixture() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mRealView = (PositionsView) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage().showView(
                                PositionsUI.POSITIONS_VIEW_ID);
            }
        });
        mView = mBot.viewById(PositionsUI.POSITIONS_VIEW_ID);
    }

    public SWTBotView getView() {
        return mView;
    }

    public void setFlatView() {
        new SWTBot().toolbarButtonWithTooltip("Set Flat Layout").click();
    }

    public void groupBy(final Grouping... groupings) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mRealView.showHierarchicalPage(groupings);
            }
        });
    }

    public void assertEmptyPage() {
        mView.bot().label("Position data is not available.");
    }

    public void assertFlatTable() {
        SWTBotTable table = mView.bot().table();
        assertThat(table.columns(), is(Arrays.asList(ObjectArrays.concat(
                new String[] { "Underlying", "Account", "Trader Id" },
                getCommonColumns(), String.class))));
    }

    public void assertGroupTree() {
        SWTBotTree tree = mView.bot().tree();
        assertThat(tree.columns(), is(Arrays.asList(ObjectArrays.concat(
                "Grouping", getCommonColumns()))));
    }

    private String[] getCommonColumns() {
        return new String[] { "Instrument", "Root", "Expiry", "Contract Type",
                "Strike", "Position", "Incoming", "Position PL", "Trading PL",
                "Realized PL", "Unrealized PL", "Total PL" };
    }

    public void assertFlatPositionsCount(int i) {
        assertThat(mView.bot().table().rowCount(), is(i));
    }

    public void assertGroupedPositionsCount(int i) {
        assertThat(mView.bot().tree().rowCount(), is(i));
    }

    public void assertFlatEquityPosition(String symbol, String account,
            String traderId, String position, String incoming,
            String positionPL, String tradingPL, String realizedPL,
            String unrealizedPL, String totalPL) {
        assertFlatPosition(symbol, account, traderId, "EQ", "NA", "NA", "NA",
                "NA", position, incoming, positionPL, tradingPL, realizedPL,
                unrealizedPL, totalPL);
    }

    public void assertFlatOptionPosition(String underlying, String account,
            String traderId, String root, String expiry, String type,
            String strike, String position, String incoming, String positionPL,
            String tradingPL, String realizedPL, String unrealizedPL,
            String totalPL) {
        assertFlatPosition(underlying, account, traderId, "OPT", root, expiry,
                type, strike, position, incoming, positionPL, tradingPL,
                realizedPL, unrealizedPL, totalPL);
    }

    public PositionTreeValidator assertGroupedPosition(String grouping,
            String position, String incoming, String positionPL,
            String tradingPL, String realizedPL, String unrealizedPL,
            String totalPL) {
        SWTBotTreeItem item = assertGroupedPosition(grouping, "", "", "", "",
                "", position, incoming, positionPL, tradingPL, realizedPL,
                unrealizedPL, totalPL);
        return new PositionTreeValidator(item, null);
    }

    public SWTBotTreeItem assertGroupedPosition(String grouping,
            String... otherColumnValues) {
        SWTBotTree tree = mView.bot().tree();
        assertThat("Wrong number of column values", otherColumnValues.length,
                is(tree.columnCount() - 1));
        for (int i = 0; i < tree.rowCount(); i++) {
            if (tree.cell(i, "Grouping").equals(grouping)) {
                for (int j = 0; j < otherColumnValues.length; j++) {
                    assertThat(tree.cell(i, j + 1), is(otherColumnValues[j]));
                }
                return tree.getAllItems()[i];
            }
        }
        fail(MessageFormat
                .format("Row with grouping [{0}] not found", grouping));
        return null; // unreachable
    }

    public void assertFlatPosition(String underlying, String account,
            String trader, String instrument, String... otherColumnValues) {
        SWTBotTable table = mView.bot().table();
        assertThat("Wrong number of column values", otherColumnValues.length,
                is(table.columnCount() - 4));
        for (int i = 0; i < table.rowCount(); i++) {
            if (table.cell(i, "Underlying").equals(underlying)
                    && table.cell(i, "Account").equals(account)
                    && table.cell(i, "Trader Id").equals(trader)
                    && table.cell(i, "Instrument").equals(instrument)) {
                for (int j = 0; j < otherColumnValues.length; j++) {
                    assertThat(table.cell(i, j + 4), is(otherColumnValues[j]));
                }
                return;
            }
        }
        fail(MessageFormat
                .format(
                        "Row with underlying [{0}], account [{1}], and trader [{2}] not found",
                        underlying, account, trader));

    }

    public void addTrade(MockTrade<?> trade) {
        Lock lock = mTrades.getReadWriteLock().writeLock();
        lock.lock();
        try {
            mTrades.add(trade);
        } finally {
            lock.unlock();
        }
    }

    public void fireTrade(Instrument instrument, String newPrice) {
        mMarketDataSupport.fireTrade(instrument, newPrice == null ? null
                : new BigDecimal(newPrice));
    }

    public void fireClosingPrice(Instrument instrument, String newPrice) {
        mMarketDataSupport.fireClosingPrice(instrument, newPrice == null ? null
                : new BigDecimal(newPrice));
    }

    public void fireOptionMultiplier(Instrument instrument,
            BigDecimal newMultiplier) {
        mMarketDataSupport.fireOptionMultiplier(instrument, newMultiplier);
    }

    public void filter(final String string) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mRealView.setFilterText(string);
            }
        });
    }

    public void registerModel() {
        mMockService = OSGITestUtil.registerMockService(PositionEngine.class,
                PositionEngineFactory.create(mTrades, mIncomingPositionSupport,
                        mMarketDataSupport, mUnderlyingSymbolSupport));
    }

    public void unregisterModel() {
        if (mMockService != null) {
            mMockService.unregister();
            mMockService = null;
        }
    }

    public void dispose() throws Exception {
        filter("");
        unregisterModel();
    }

    private class MockMarketDataSupport implements MarketDataSupport {

        private final Multimap<Instrument, InstrumentMarketDataListener> mListeners = HashMultimap
                .create();

        @Override
        public BigDecimal getLastTradePrice(Instrument instrument) {
            return null;
        }

        @Override
        public BigDecimal getClosingPrice(Instrument instrument) {
            return null;
        }

        @Override
        public BigDecimal getOptionMultiplier(Option option) {
            return null;
        }

        @Override
        public void addInstrumentMarketDataListener(Instrument instrument,
                InstrumentMarketDataListener listener) {
            mListeners.put(instrument, listener);
        }

        @Override
        public void removeInstrumentMarketDataListener(Instrument instrument,
                InstrumentMarketDataListener listener) {
            mListeners.remove(instrument, listener);
        }

        public void fireTrade(Instrument instrument, BigDecimal newPrice) {
            InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(
                    this, newPrice);
            for (InstrumentMarketDataListener listener : mListeners
                    .get(instrument)) {
                listener.symbolTraded(event);
            }
        }

        public void fireClosingPrice(Instrument instrument, BigDecimal newPrice) {
            InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(
                    this, newPrice);
            for (InstrumentMarketDataListener listener : mListeners
                    .get(instrument)) {
                listener.closePriceChanged(event);
            }
        }

        public void fireOptionMultiplier(Instrument instrument,
                BigDecimal newMultiplier) {
            InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(
                    this, newMultiplier);
            for (InstrumentMarketDataListener listener : mListeners
                    .get(instrument)) {
                listener.optionMultiplierChanged(event);
            }
        }

        @Override
        public void dispose() {
        }

    }

    public class PositionTreeValidator {

        private final SWTBotTreeItem mItem;
        private final PositionTreeValidator mParent;

        public PositionTreeValidator(SWTBotTreeItem item, PositionTreeValidator parent) {
            mItem = item;
            mParent = parent;
        }

        public PositionTreeValidator withChild(String grouping,
                String position, String incoming, String positionPL,
                String tradingPL, String realizedPL, String unrealizedPL,
                String totalPL) {
            SWTBotTreeItem item = assertGroupedPosition(grouping, "", "", "",
                    "", "", position, incoming, positionPL, tradingPL,
                    realizedPL, unrealizedPL, totalPL);
            return new PositionTreeValidator(item, this);
        }

        public SWTBotTreeItem assertGroupedPosition(String grouping,
                String... otherColumnValues) {
            for (int i = 0; i < mItem.rowCount(); i++) {
                if (mItem.cell(i, 0).equals(grouping)) {
                    for (int j = 0; j < otherColumnValues.length; j++) {
                        assertThat(mItem.cell(i, j + 1),
                                is(otherColumnValues[j]));
                    }
                    return mItem.getItems()[i];
                }
            }
            fail(MessageFormat.format("Row with grouping [{0}] not found",
                    grouping));
            return null; // unreachable
        }

        public PositionTreeValidator withEquityPosition(String grouping, String position,
                String incoming, String positionPL, String tradingPL,
                String realizedPL, String unrealizedPL, String totalPL) {
            SWTBotTreeItem item = assertGroupedPosition(grouping, "EQ", "NA",
                    "NA", "NA", "NA", position, incoming, positionPL,
                    tradingPL, realizedPL, unrealizedPL, totalPL);
            assertThat(item.getItems().length, is(0));
            return this;
        }

        public PositionTreeValidator withOptionPosition(String grouping, String root,
                String expiry, String type, String strike, String position,
                String incoming, String positionPL, String tradingPL,
                String realizedPL, String unrealizedPL, String totalPL) {
            SWTBotTreeItem item = assertGroupedPosition(grouping, "OPT", root,
                    expiry, type, strike, position, incoming, positionPL,
                    tradingPL, realizedPL, unrealizedPL, totalPL);
            assertThat(item.getItems().length, is(0));
            return this;
        }
        
        public PositionTreeValidator up() {
            return mParent;
        }

    }
}
