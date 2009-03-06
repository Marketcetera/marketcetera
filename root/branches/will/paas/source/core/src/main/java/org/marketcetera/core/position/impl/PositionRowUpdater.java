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
 * Responsible for updating a PositionRow when trade or market data events
 * occur.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class PositionRowUpdater {

    private final ListEventListener<Trade> listChangeListener;
    private final EventList<Trade> trades;
    private PositionRowImpl positionRow;
    private PositionMarketData marketData;
    private PropertyChangeListenerProxy symbolChangeListener;
    private PositionMetricsCalculator calculator;

    /**
     * Returns the PositionRow being managed by this class.
     * 
     * @return the dynamically updated position
     */
    public PositionRow getPosition() {
        return positionRow;
    }

    /**
     * Constructor.
     * 
     * {@link #dispose()} must be called when the instance is no longer in use.
     * 
     * @param trades
     *            event list of trades that make up the position
     * @param marketData
     *            the market data provider
     */
    public PositionRowUpdater(EventList<Trade> trades, PositionMarketData marketData) {
        this.trades = trades;
        this.marketData = marketData;
        Trade trade = trades.get(0);
        positionRow = new PositionRowImpl(trade.getAccount(), trade.getSymbol(), trade
                .getTraderId(), BigDecimal.ZERO);
        listChangeListener = new ListEventListener<Trade>() {

            @Override
            public void listChanged(ListEvent<Trade> listChanges) {
                PositionRowUpdater.this.listChanged(listChanges);
            }
        };
        symbolChangeListener = new PropertyChangeListenerProxy(trade.getSymbol(),
                new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent event) {
                        tick((BigDecimal) event.getNewValue());
                    }
                });
        positionRow.setPositionMetrics(recalculate());
        connect();
    }

    private void connect() {
        marketData.addSymbolTradeListener(symbolChangeListener.getPropertyName(),
                symbolChangeListener);
        trades.addListEventListener(listChangeListener);
    }

    /**
     * Releases the resources held by this object. After dispose has been
     * called, this object should no longer be used.
     */
    public void dispose() {
        trades.removeListEventListener(listChangeListener);
        marketData.removeSymbolTradeListener(symbolChangeListener.getPropertyName(),
                symbolChangeListener);
    }

    private void tick(BigDecimal tick) {
        positionRow.setPositionMetrics(calculator.tick(tick));
    }

    private void listChanged(ListEvent<Trade> listChanges) {
        assert listChanges.getSourceList() == trades;
        while (listChanges.next()) {
            final int changeIndex = listChanges.getIndex();
            final int changeType = listChanges.getType();
            if (changeType == ListEvent.INSERT && trades.size() == changeIndex + 1) {
                Trade trade = trades.get(changeIndex);
                positionRow.setPositionMetrics(calculator.trade(trade));
            } else {
                positionRow.setPositionMetrics(recalculate());
            }
        }
    }

    private PositionMetrics recalculate() {
        // TODO: provide real incoming position and closing price
        PositionMetrics metrics = new PositionMetricsImpl(null, null, null, null, null, null);
        calculator = new PositionMetricsCalculatorImpl(marketData.getLastTradePrice(positionRow.getSymbol()));
        for (Trade trade : trades) {
            metrics = calculator.trade(trade);
        }
        return metrics;
    }

}
