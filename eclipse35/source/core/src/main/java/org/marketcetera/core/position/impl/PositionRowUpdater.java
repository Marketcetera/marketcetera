package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.Trade;
import org.marketcetera.core.position.MarketDataSupport.SymbolChangeEvent;
import org.marketcetera.core.position.MarketDataSupport.SymbolChangeListener;
import org.marketcetera.core.position.MarketDataSupport.SymbolChangeListenerBase;
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
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public final class PositionRowUpdater {

    private final ListEventListener<Trade> mListChangeListener;
    private EventList<Trade> mTrades;
    private final PositionRowImpl mPositionRow;
    private final MarketDataSupport mMarketDataSupport;
    private final SymbolChangeListener mSymbolChangeListener;
    private static final ExecutorService sMarketDataUpdateExecutor = Executors
            .newSingleThreadExecutor();
    private final AtomicBoolean mTickPending = new AtomicBoolean();
    private final AtomicBoolean mClosingPricePending = new AtomicBoolean();
    private volatile PositionMetricsCalculator mCalculator;
    private volatile BigDecimal mClosePrice;
    private volatile BigDecimal mLastTradePrice;

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
     *            event list of trades that make up the position, may be null
     * @param marketData
     *            the market data provider
     */
    public PositionRowUpdater(PositionRowImpl positionRow, EventList<Trade> trades,
            MarketDataSupport marketData) {
        Validate.noNullElements(new Object[] { positionRow, marketData });
        mPositionRow = positionRow;
        mMarketDataSupport = marketData;

        mListChangeListener = new ListEventListener<Trade>() {

            @Override
            public void listChanged(ListEvent<Trade> listChanges) {
                PositionRowUpdater.this.listChanged(listChanges);
            }
        };
        mSymbolChangeListener = new SymbolChangeListenerBase() {

            @Override
            public void symbolTraded(SymbolChangeEvent event) {
                tick(event.getNewPrice());
            }

            @Override
            public void closePriceChanged(SymbolChangeEvent event) {
                PositionRowUpdater.this.closePriceChanged(event.getNewPrice());
            }
        };
        mMarketDataSupport.addSymbolChangeListener(mPositionRow.getSymbol(), mSymbolChangeListener);
        if (trades != null) {
            connect(trades);
        } else {
            mPositionRow.setPositionMetrics(recalculate());
        }
    }

    /**
     * Connects this class to a list of trades. This can only be called on instances that were
     * created with a null trades lists. And it can only be called once.
     * 
     * @param trades
     *            the dynamically updated list of trades
     * @throws IllegalStateException
     *             if this object is already connected to list of trades
     */
    public void connect(EventList<Trade> trades) {
        if (mTrades != null) {
            throw new IllegalStateException();
        }
        mTrades = trades;
        mTrades.addListEventListener(mListChangeListener);
        mPositionRow.setPositionMetrics(recalculate());
    }

    /**
     * Releases the resources held by this object. After dispose has been called, this object should
     * no longer be used.
     */
    public void dispose() {
        if (mTrades != null) {
            mTrades.removeListEventListener(mListChangeListener);
        }
        mMarketDataSupport.removeSymbolChangeListener(mPositionRow.getSymbol(),
                mSymbolChangeListener);
    }

    private void tick(BigDecimal tick) {
        mLastTradePrice = tick;
        // Since this modifies the position and will need a lock, the update happens in 
        // a separate thread.  mTickPending is used to avoid queuing multiple updates
        // since the runnable always uses the latest value.
        if (mTickPending.compareAndSet(false, true)) {
            sMarketDataUpdateExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mTickPending.set(false);
                    mPositionRow.setPositionMetrics(mCalculator.tick(mLastTradePrice));
                }
            });
        }
    }

    private void closePriceChanged(BigDecimal newPrice) {
        BigDecimal oldPrice = mClosePrice;
        // since change close price requires a full recalculation, only do it if necessary
        if (oldPrice == null && newPrice == null) {
            return;
        } else if (oldPrice != null && newPrice != null && oldPrice.compareTo(newPrice) == 0) {
            return;
        }
        mClosePrice = newPrice;
        // Since this modifies the position and will need a lock, the update happens in 
        // a separate thread.  mClosingPricePending is used to avoid queuing multiple updates
        // since the runnable always uses the latest value.
        if (mClosingPricePending.compareAndSet(false, true)) {
            sMarketDataUpdateExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mClosingPricePending.set(false);
                    mPositionRow.setPositionMetrics(recalculate());
                }
            });
        }
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
        mCalculator = new PositionMetricsCalculatorImpl(mPositionRow.getPositionMetrics()
                .getIncomingPosition(), mClosePrice);
        PositionMetrics metrics = mCalculator.tick(mLastTradePrice);
        if (mTrades != null) {
            for (Trade trade : mTrades) {
                metrics = mCalculator.trade(trade);
            }
        }
        return metrics;
    }
}
