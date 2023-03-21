package org.marketcetera.ui.marketdata.view;

import java.math.BigDecimal;

import org.joda.time.DateTime;
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
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataQuoteItem
{
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
     * @param inQuoteEvent
     */
    public MarketDataQuoteItem(QuoteEvent inQuoteEvent)
    {
        this(inQuoteEvent.getInstrument());
        eventTypeProperty.set(inQuoteEvent.getEventType());
        exchangeProperty.set(inQuoteEvent.getExchange());
        priceProperty.set(inQuoteEvent.getPrice());
        quantityProperty.set(inQuoteEvent.getSize());
        quoteActionProperty.set(inQuoteEvent.getAction());
        quoteCountProperty.set(inQuoteEvent.getCount());
        quoteLevelProperty.set(inQuoteEvent.getLevel());
        timestampProperty.set(new DateTime(inQuoteEvent.getTimestamp()));
    }
    /**
     * Get the quoteActionProperty value.
     *
     * @return a <code>ObjectProperty<QuoteAction></code> value
     */
    public ObjectProperty<QuoteAction> quoteActionProperty()
    {
        return quoteActionProperty;
    }
    /**
     * Get the eventTypeProperty value.
     *
     * @return a <code>ObjectProperty<EventType></code> value
     */
    public ObjectProperty<EventType> eventTypeProperty()
    {
        return eventTypeProperty;
    }
    /**
     * Get the quoteLevelProperty value.
     *
     * @return a <code>IntegerProperty</code> value
     */
    public IntegerProperty quoteLevelProperty()
    {
        return quoteLevelProperty;
    }
    /**
     * Get the quoteCountProperty value.
     *
     * @return a <code>IntegerProperty</code> value
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
     * @return a <code>ObjectProperty<DateTime></code> value
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
     * @return a <code>ObjectProperty<BigDecimal></code> value
     */
    public ObjectProperty<BigDecimal> quantityProperty()
    {
        return quantityProperty;
    }
    /**
     * Get the priceProperty value.
     *
     * @return a <code>ObjectProperty<BigDecimal></code> value
     */
    public ObjectProperty<BigDecimal> priceProperty()
    {
        return priceProperty;
    }
    public void update(Event inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "Received {}",
                               inEvent);
        if(inEvent instanceof QuoteEvent) {
            QuoteEvent quoteEvent = (QuoteEvent)inEvent;
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
    private final ObjectProperty<QuoteAction> quoteActionProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<EventType> eventTypeProperty = new SimpleObjectProperty<>();
    private final IntegerProperty quoteLevelProperty = new SimpleIntegerProperty();
    private final IntegerProperty quoteCountProperty = new SimpleIntegerProperty();
    private final Instrument instrument;
    private final StringProperty symbolProperty;
    private final ObjectProperty<DateTime> timestampProperty;
    private final StringProperty exchangeProperty;
    private final ObjectProperty<BigDecimal> quantityProperty;
    private final ObjectProperty<BigDecimal> priceProperty;
}
