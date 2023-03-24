package org.marketcetera.ui.marketdata.view;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* $License$ */

/**
 * Represents a single row in the market data view list table.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataItem
{
    /**
     * Create a new MarketDataItem instance.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inMarketDataRequestId a <code>String</code> value
     */
    public MarketDataItem(Instrument inInstrument,
                          String inMarketDataRequestId)
    {
        instrument = inInstrument;
        marketDataRequestIdProperty = new SimpleStringProperty(inMarketDataRequestId);
        symbolProperty = new SimpleStringProperty(instrument.getFullSymbol());
        tradeExchangeProperty = new SimpleStringProperty();
        lastPriceProperty = new SimpleObjectProperty<>();
        lastQuantityProperty = new SimpleObjectProperty<>();
        tradeTimestampProperty = new SimpleObjectProperty<>();
        bidQuantityProperty = new SimpleObjectProperty<>();
        bidPriceProperty = new SimpleObjectProperty<>();
        askPriceProperty = new SimpleObjectProperty<>();
        askQuantityProperty = new SimpleObjectProperty<>();
        previousClosePriceProperty = new SimpleObjectProperty<>();
        openPriceProperty = new SimpleObjectProperty<>();
        highPriceProperty = new SimpleObjectProperty<>();
        lowPriceProperty = new SimpleObjectProperty<>();
        closePriceProperty = new SimpleObjectProperty<>();
        tradeVolumeProperty = new SimpleObjectProperty<>();
        bidExchangeProperty = new SimpleStringProperty();
        bidTimestampProperty = new SimpleObjectProperty<>();
        askExchangeProperty = new SimpleStringProperty();
        askTimestampProperty = new SimpleObjectProperty<>();
    }
    /**
     * Get the market data request id property.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty marketDataRequestIdProperty()
    {
        return marketDataRequestIdProperty;
    }
    /**
     * Get the instrument value.
     *
     * @return a <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Get the symbolProperty value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty symbolProperty()
    {
        return symbolProperty;
    }
    /**
     * Get the tradeExchangeProperty value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty tradeExchangeProperty()
    {
        return tradeExchangeProperty;
    }
    /**
     * Get the lastPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> lastPriceProperty()
    {
        return lastPriceProperty;
    }
    /**
     * Get the lastQuantityProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> lastQuantityProperty()
    {
        return lastQuantityProperty;
    }
    /**
     * Get the tradeTimestampProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<DateTime></code> value
     */
    public ReadOnlyObjectProperty<DateTime> tradeTimestampProperty()
    {
        return tradeTimestampProperty;
    }
    /**
     * Get the bidTimestampProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<DateTime></code> value
     */
    public ReadOnlyObjectProperty<DateTime> bidTimestampProperty()
    {
        return bidTimestampProperty;
    }
    /**
     * Get the askTimestampProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<DateTime></code> value
     */
    public ReadOnlyObjectProperty<DateTime> askTimestampProperty()
    {
        return askTimestampProperty;
    }
    /**
     * Get the bidExchangeProperty value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty bidExchangeProperty()
    {
        return bidExchangeProperty;
    }
    /**
     * Get the askExchangeProperty value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty askExchangeProperty()
    {
        return askExchangeProperty;
    }
    /**
     * Get the bidQuantityProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> bidQuantityProperty()
    {
        return bidQuantityProperty;
    }
    /**
     * Get the bidPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> bidPriceProperty()
    {
        return bidPriceProperty;
    }
    /**
     * Get the askPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> askPriceProperty()
    {
        return askPriceProperty;
    }
    /**
     * Get the askQuantityProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> askQuantityProperty()
    {
        return askQuantityProperty;
    }
    /**
     * Get the previousClosePriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> previousClosePriceProperty()
    {
        return previousClosePriceProperty;
    }
    /**
     * Get the openPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> openPriceProperty()
    {
        return openPriceProperty;
    }
    /**
     * Get the highPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> highPriceProperty()
    {
        return highPriceProperty;
    }
    /**
     * Get the lowPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> lowPriceProperty()
    {
        return lowPriceProperty;
    }
    /**
     * Get the closePriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> closePriceProperty()
    {
        return closePriceProperty;
    }
    /**
     * Get the tradeVolumeProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<BigDecimal></code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> tradeVolumeProperty()
    {
        return tradeVolumeProperty;
    }
    /**
     * Update the item with the given event.
     *
     * @param inEvent an <code>Event</code> value
     */
    public void update(Event inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "Received {}",
                               inEvent);
        if(inEvent instanceof TradeEvent) {
            TradeEvent tradeEvent = (TradeEvent)inEvent;
            lastPriceProperty.set(tradeEvent.getPrice());
            lastQuantityProperty.set(tradeEvent.getSize());
            tradeExchangeProperty.set(tradeEvent.getExchange());
            tradeTimestampProperty.set(new DateTime(tradeEvent.getExchangeTimestamp()));
        }
        if(inEvent instanceof BidEvent) {
            BidEvent bidEvent = (BidEvent)inEvent;
            bidPriceProperty.set(bidEvent.getPrice());
            bidQuantityProperty.set(bidEvent.getSize());
            bidExchangeProperty.set(bidEvent.getExchange());
            bidTimestampProperty.set(new DateTime(bidEvent.getQuoteDate()));
        }
        if(inEvent instanceof AskEvent) {
            AskEvent askEvent = (AskEvent)inEvent;
            askPriceProperty.set(askEvent.getPrice());
            askQuantityProperty.set(askEvent.getSize());
            askExchangeProperty.set(askEvent.getExchange());
            askTimestampProperty.set(new DateTime(askEvent.getQuoteDate()));
        }
        if(inEvent instanceof MarketstatEvent) {
            MarketstatEvent marketstatEvent = (MarketstatEvent)inEvent;
            previousClosePriceProperty.set(marketstatEvent.getPreviousClose());
            openPriceProperty.set(marketstatEvent.getOpen());
            highPriceProperty.set(marketstatEvent.getHigh());
            lowPriceProperty.set(marketstatEvent.getLow());
            closePriceProperty.set(marketstatEvent.getClose());
            tradeVolumeProperty.set(marketstatEvent.getVolume());
        }
    }
    private final Instrument instrument;
    private final StringProperty symbolProperty;
    private final StringProperty tradeExchangeProperty;
    private final ObjectProperty<BigDecimal> lastPriceProperty;
    private final ObjectProperty<BigDecimal> lastQuantityProperty;
    private final ObjectProperty<DateTime> tradeTimestampProperty;
    private final ObjectProperty<DateTime> bidTimestampProperty;
    private final ObjectProperty<DateTime> askTimestampProperty;
    private final StringProperty bidExchangeProperty;
    private final StringProperty askExchangeProperty;
    private final ObjectProperty<BigDecimal> bidQuantityProperty;
    private final ObjectProperty<BigDecimal> bidPriceProperty;
    private final ObjectProperty<BigDecimal> askPriceProperty;
    private final ObjectProperty<BigDecimal> askQuantityProperty;
    private final ObjectProperty<BigDecimal> previousClosePriceProperty;
    private final ObjectProperty<BigDecimal> openPriceProperty;
    private final ObjectProperty<BigDecimal> highPriceProperty;
    private final ObjectProperty<BigDecimal> lowPriceProperty;
    private final ObjectProperty<BigDecimal> closePriceProperty;
    private final ObjectProperty<BigDecimal> tradeVolumeProperty;
    private final StringProperty marketDataRequestIdProperty;
}
