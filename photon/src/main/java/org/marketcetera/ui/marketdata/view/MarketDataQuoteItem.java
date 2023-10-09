package org.marketcetera.ui.marketdata.view;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.marketcetera.core.BigDecimalUtil;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventType;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* $License$ */

/**
 * Provides a display implementation of a market data quote.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataQuoteItem
{
    /**
     * Create a new MarketDataQuoteItem instance.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public MarketDataQuoteItem(Instrument inInstrument)
    {
        instrument = inInstrument;
        symbolProperty = new SimpleStringProperty(instrument.getFullSymbol());
        quantityProperty = new SimpleObjectProperty<>();
        priceProperty = new SimpleObjectProperty<>();
        exchangeProperty = new SimpleStringProperty();
        timestampProperty = new SimpleObjectProperty<>();
    }
    /**
     * Create a new MarketDataQuoteItem instance.
     *
     * @param inQuoteEvent a <code>QuoteEvent</code> value
     */
    public MarketDataQuoteItem(QuoteEvent inQuoteEvent)
    {
        this(inQuoteEvent.getInstrument());
        eventTypeProperty.set(inQuoteEvent.getEventType());
        exchangeProperty.set(inQuoteEvent.getExchange());
        priceProperty.set(renderZeroAsNull(inQuoteEvent.getPrice()));
        quantityProperty.set(renderZeroAsNull(inQuoteEvent.getSize()));
        quoteActionProperty.set(inQuoteEvent.getAction());
        quoteCountProperty.set(inQuoteEvent.getCount());
        quoteLevelProperty.set(inQuoteEvent.getLevel());
        timestampProperty.set(new DateTime(inQuoteEvent.getTimestamp()));
    }
    /**
     * Get the quoteActionProperty value.
     *
     * @return an <code>ObjectProperty&lt;QuoteAction&gt;</code> value
     */
    public ObjectProperty<QuoteAction> quoteActionProperty()
    {
        return quoteActionProperty;
    }
    /**
     * Get the eventTypeProperty value.
     *
     * @return an <code>ObjectProperty&lt;EventType&gt;</code> value
     */
    public ObjectProperty<EventType> eventTypeProperty()
    {
        return eventTypeProperty;
    }
    /**
     * Get the quoteLevelProperty value.
     *
     * @return an <code>IntegerProperty</code> value
     */
    public IntegerProperty quoteLevelProperty()
    {
        return quoteLevelProperty;
    }
    /**
     * Get the quoteCountProperty value.
     *
     * @return an <code>IntegerProperty</code> value
     */
    public IntegerProperty quoteCountProperty()
    {
        return quoteCountProperty;
    }
    /**
     * Get the symbolProperty value.
     *
     * @return a <code>StringProperty</code> value
     */
    public StringProperty symbolProperty()
    {
        return symbolProperty;
    }
    /**
     * Get the timestampProperty value.
     *
     * @return an <code>ObjectProperty&lt;DateTime&gt;</code> value
     */
    public ObjectProperty<DateTime> timestampProperty()
    {
        return timestampProperty;
    }
    /**
     * Get the exchangeProperty value.
     *
     * @return a <code>StringProperty</code> value
     */
    public StringProperty exchangeProperty()
    {
        return exchangeProperty;
    }
    /**
     * Get the quantityProperty value.
     *
     * @return an <code>ObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ObjectProperty<BigDecimal> quantityProperty()
    {
        return quantityProperty;
    }
    /**
     * Get the priceProperty value.
     *
     * @return an <code>ObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ObjectProperty<BigDecimal> priceProperty()
    {
        return priceProperty;
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
        if(inEvent instanceof QuoteEvent) {
            QuoteEvent quoteEvent = (QuoteEvent)inEvent;
            if(quoteEvent.isEmpty()) {
                priceProperty.set(null);
                quantityProperty.set(null);
                exchangeProperty.set(null);
                timestampProperty.set(null);
                eventTypeProperty.set(null);
                quoteActionProperty.set(null);
            } else {
                priceProperty.set(quoteEvent.getPrice());
                quantityProperty.set(quoteEvent.getSize());
                exchangeProperty.set(quoteEvent.getExchange());
                timestampProperty.set(new DateTime(quoteEvent.getQuoteDate()));
                eventTypeProperty.set(quoteEvent.getEventType());
                quoteActionProperty.set(quoteEvent.getAction());
                quoteLevelProperty.set(quoteEvent.getLevel());
                quoteCountProperty.set(quoteEvent.getCount());
            }
        }
    }
    /**
     * Render a zero value as null.
     *
     * @param inValue a <code>BigDecimal</code> value
     * @return a <code>BigDecimal</code> value
     */
    private BigDecimal renderZeroAsNull(BigDecimal inValue)
    {
        return BigDecimalUtil.isNullOrZero(inValue) ? null : inValue;
    }
    /**
     * quote action property value
     */
    private final ObjectProperty<QuoteAction> quoteActionProperty = new SimpleObjectProperty<>();
    /**
     * event type property value
     */
    private final ObjectProperty<EventType> eventTypeProperty = new SimpleObjectProperty<>();
    /**
     * quote level property value
     */
    private final IntegerProperty quoteLevelProperty = new SimpleIntegerProperty();
    /**
     * quote count property value
     */
    private final IntegerProperty quoteCountProperty = new SimpleIntegerProperty();
    /**
     * instrument value
     */
    private final Instrument instrument;
    /**
     * symbol property
     */
    private final StringProperty symbolProperty;
    /**
     * timestamp property
     */
    private final ObjectProperty<DateTime> timestampProperty;
    /**
     * exchange property
     */
    private final StringProperty exchangeProperty;
    /**
     * quantity property
     */
    private final ObjectProperty<BigDecimal> quantityProperty;
    /**
     * price property
     */
    private final ObjectProperty<BigDecimal> priceProperty;
}
