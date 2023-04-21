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
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Get the symbolProperty value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty symbolProperty()
    {
        return symbolProperty;
    }
    /**
     * Get the tradeExchangeProperty value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty tradeExchangeProperty()
    {
        return tradeExchangeProperty;
    }
    /**
     * Get the lastPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> lastPriceProperty()
    {
        return lastPriceProperty;
    }
    /**
     * Get the lastQuantityProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> lastQuantityProperty()
    {
        return lastQuantityProperty;
    }
    /**
     * Get the tradeTimestampProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;DateTime&gt;</code> value
     */
    public ReadOnlyObjectProperty<DateTime> tradeTimestampProperty()
    {
        return tradeTimestampProperty;
    }
    /**
     * Get the bidTimestampProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;DateTime&gt;</code> value
     */
    public ReadOnlyObjectProperty<DateTime> bidTimestampProperty()
    {
        return bidTimestampProperty;
    }
    /**
     * Get the askTimestampProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;DateTime&gt;</code> value
     */
    public ReadOnlyObjectProperty<DateTime> askTimestampProperty()
    {
        return askTimestampProperty;
    }
    /**
     * Get the bidExchangeProperty value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty bidExchangeProperty()
    {
        return bidExchangeProperty;
    }
    /**
     * Get the askExchangeProperty value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty askExchangeProperty()
    {
        return askExchangeProperty;
    }
    /**
     * Get the bidQuantityProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> bidQuantityProperty()
    {
        return bidQuantityProperty;
    }
    /**
     * Get the bidPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> bidPriceProperty()
    {
        return bidPriceProperty;
    }
    /**
     * Get the askPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> askPriceProperty()
    {
        return askPriceProperty;
    }
    /**
     * Get the askQuantityProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> askQuantityProperty()
    {
        return askQuantityProperty;
    }
    /**
     * Get the previousClosePriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> previousClosePriceProperty()
    {
        return previousClosePriceProperty;
    }
    /**
     * Get the openPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> openPriceProperty()
    {
        return openPriceProperty;
    }
    /**
     * Get the highPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> highPriceProperty()
    {
        return highPriceProperty;
    }
    /**
     * Get the lowPriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> lowPriceProperty()
    {
        return lowPriceProperty;
    }
    /**
     * Get the closePriceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> closePriceProperty()
    {
        return closePriceProperty;
    }
    /**
     * Get the tradeVolumeProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> tradeVolumeProperty()
    {
        return tradeVolumeProperty;
    }
    /**
     * Get the provider property value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty providerProperty()
    {
        return providerProperty;
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
        providerProperty.set(inEvent.getProvider());
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
    /**
     * underlying instrument value
     */
    private final Instrument instrument;
    /**
     * symbol property value
     */
    private final StringProperty symbolProperty;
    /**
     * trade exchange property value
     */
    private final StringProperty tradeExchangeProperty;
    /**
     * last trade price property value
     */
    private final ObjectProperty<BigDecimal> lastPriceProperty;
    /**
     * last quantity property value
     */
    private final ObjectProperty<BigDecimal> lastQuantityProperty;
    /**
     * trade timestamp property value
     */
    private final ObjectProperty<DateTime> tradeTimestampProperty;
    /**
     * bid timestamp property value
     */
    private final ObjectProperty<DateTime> bidTimestampProperty;
    /**
     * ask timestamp property value
     */
    private final ObjectProperty<DateTime> askTimestampProperty;
    /**
     * bid exchange property value
     */
    private final StringProperty bidExchangeProperty;
    /**
     * ask exchange property value
     */
    private final StringProperty askExchangeProperty;
    /**
     * bid quantity property value
     */
    private final ObjectProperty<BigDecimal> bidQuantityProperty;
    /**
     * bid price property value
     */
    private final ObjectProperty<BigDecimal> bidPriceProperty;
    /**
     * ask price property value
     */
    private final ObjectProperty<BigDecimal> askPriceProperty;
    /**
     * ask quantity property value
     */
    private final ObjectProperty<BigDecimal> askQuantityProperty;
    /**
     * previous close price property value
     */
    private final ObjectProperty<BigDecimal> previousClosePriceProperty;
    /**
     * open price property value
     */
    private final ObjectProperty<BigDecimal> openPriceProperty;
    /**
     * high price property value
     */
    private final ObjectProperty<BigDecimal> highPriceProperty;
    /**
     * low price property value
     */
    private final ObjectProperty<BigDecimal> lowPriceProperty;
    /**
     * close price property value
     */
    private final ObjectProperty<BigDecimal> closePriceProperty;
    /**
     * trade volume property value
     */
    private final ObjectProperty<BigDecimal> tradeVolumeProperty;
    /**
     * market data request id property value
     */
    private final StringProperty marketDataRequestIdProperty;
    /**
     * holds the provider of the market data
     */
    private final StringProperty providerProperty = new SimpleStringProperty();
}
