package org.marketcetera.core.position.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.math.BigDecimal;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.Trade;
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

    private final ListEventListener<Trade> listChangeListener;
    private final EventList<Trade> trades;
    private final PositionRowImpl mPositionRow;
    private PositionMarketData marketData;
    private PropertyChangeListenerProxy symbolChangeListener;
    private PositionMetricsCalculator calculator;

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
            PositionMarketData marketData) {
        mPositionRow = positionRow;
        this.trades = trades;
        this.marketData = marketData;

        listChangeListener = new ListEventListener<Trade>() {

            @Override
            public void listChanged(ListEvent<Trade> listChanges) {
                PositionRowUpdater.this.listChanged(listChanges);
            }
        };
        symbolChangeListener = new PropertyChangeListenerProxy(mPositionRow.getSymbol(),
                new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent event) {
                        tick((BigDecimal) event.getNewValue());
                    }
                });
        mPositionRow.setPositionMetrics(recalculate());
        connect();
    }

    private void connect() {
        marketData.addSymbolTradeListener(symbolChangeListener.getPropertyName(),
                symbolChangeListener);
        trades.addListEventListener(listChangeListener);
    }

    /**
     * Releases the resources held by this object. After dispose has been called, this object should
     * no longer be used.
     */
    public void dispose() {
        trades.removeListEventListener(listChangeListener);
        marketData.removeSymbolTradeListener(symbolChangeListener.getPropertyName(),
                symbolChangeListener);
    }

    private void tick(BigDecimal tick) {
        mPositionRow.setPositionMetrics(calculator.tick(tick));
    }

    private void listChanged(ListEvent<Trade> listChanges) {
        assert listChanges.getSourceList() == trades;
        while (listChanges.next()) {
            final int changeIndex = listChanges.getIndex();
            final int changeType = listChanges.getType();
            if (changeType == ListEvent.INSERT && trades.size() == changeIndex + 1) {
                Trade trade = trades.get(changeIndex);
                mPositionRow.setPositionMetrics(calculator.trade(trade));
            } else {
                mPositionRow.setPositionMetrics(recalculate());
            }
        }
    }

    private PositionMetrics recalculate() {
        String symbol = mPositionRow.getSymbol();
        calculator = new PositionMetricsCalculatorImpl(mPositionRow.getPositionMetrics()
                .getIncomingPosition(), marketData.getClosingPrice(symbol));
        PositionMetrics metrics = calculator.tick(marketData.getLastTradePrice(symbol));
        for (Trade trade : trades) {
            metrics = calculator.trade(trade);
        }
        return metrics;
    }
}
