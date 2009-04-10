package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.Trade;
import org.marketcetera.core.position.MarketDataSupport.SymbolChangeEvent;
import org.marketcetera.core.position.MarketDataSupport.SymbolChangeListener;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/* $License$ */

/**
 * Responsible for updating a PositionRow when trade or market data events occur.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class PositionRowUpdater {

    private final ListEventListener<Trade> mListChangeListener;
    private final EventList<Trade> mTrades;
    private final PositionRowImpl mPositionRow;
    private MarketDataSupport mMarketDataSupport;
    private SymbolChangeListener mSymbolChangeListener;
    private PositionMetricsCalculator mCalculator;

    /**
     * Returns the PositionRow being managed by this class.
     * 
     * @return the dynamically updated position
     */
    public PositionRow getPosition() {
        return mPositionRow;
    }

    /**
     * Constructor.
     * 
     * {@link #dispose()} must be called when the instance is no longer in use.
     * 
     * @param positionRow
     *            the position to update
     * @param trades
     *            event list of trades that make up the position
     * @param marketData
     *            the market data provider
     */
    public PositionRowUpdater(PositionRowImpl positionRow, EventList<Trade> trades,
            MarketDataSupport marketData) {
        mPositionRow = positionRow;
        this.mTrades = trades;
        this.mMarketDataSupport = marketData;

        mListChangeListener = new ListEventListener<Trade>() {

            @Override
            public void listChanged(ListEvent<Trade> listChanges) {
                PositionRowUpdater.this.listChanged(listChanges);
            }
        };
        mSymbolChangeListener = new SymbolChangeListener() {

            @Override
            public void symbolChanged(SymbolChangeEvent event) {
                tick(event.getNewPrice());
            }
        };
        mPositionRow.setPositionMetrics(recalculate());
        connect();
    }

    private void connect() {
        mMarketDataSupport.addSymbolChangeListener(mPositionRow.getSymbol(), mSymbolChangeListener);
        mTrades.addListEventListener(mListChangeListener);
    }

    /**
     * Releases the resources held by this object. After dispose has been called, this object should
     * no longer be used.
     */
    public void dispose() {
        mTrades.removeListEventListener(mListChangeListener);
        mMarketDataSupport.removeSymbolChangeListener(mPositionRow.getSymbol(), mSymbolChangeListener);
    }

    private void tick(BigDecimal tick) {
        mPositionRow.setPositionMetrics(mCalculator.tick(tick));
    }

    private void listChanged(ListEvent<Trade> listChanges) {
        assert listChanges.getSourceList() == mTrades;
        while (listChanges.next()) {
            final int changeIndex = listChanges.getIndex();
            final int changeType = listChanges.getType();
            if (changeType == ListEvent.INSERT && mTrades.size() == changeIndex + 1) {
                Trade trade = mTrades.get(changeIndex);
                mPositionRow.setPositionMetrics(mCalculator.trade(trade));
            } else {
                mPositionRow.setPositionMetrics(recalculate());
            }
        }
    }

    private PositionMetrics recalculate() {
        String symbol = mPositionRow.getSymbol();
        mCalculator = new PositionMetricsCalculatorImpl(mPositionRow.getPositionMetrics()
                .getIncomingPosition(), mMarketDataSupport.getClosingPrice(symbol));
        PositionMetrics metrics = mCalculator.tick(mMarketDataSupport.getLastTradePrice(symbol));
        for (Trade trade : mTrades) {
            metrics = mCalculator.trade(trade);
        }
        return metrics;
    }
}
