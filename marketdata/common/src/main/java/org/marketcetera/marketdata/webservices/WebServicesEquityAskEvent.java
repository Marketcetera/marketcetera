package org.marketcetera.marketdata.webservices;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.event.AskEvent;
import org.marketcetera.core.event.EquityEvent;
import org.marketcetera.core.event.EventType;
import org.marketcetera.core.event.QuoteAction;
import org.marketcetera.core.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="equityAskEvent")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesEquityAskEvent
        extends WebServicesEvent
        implements EquityEvent, AskEvent
{
    public WebServicesEquityAskEvent(AskEvent inEvent)
    {
        Validate.isTrue(inEvent instanceof EquityEvent);
        EquityEvent equityEvent = (EquityEvent)inEvent;
        action = inEvent.getAction();
        eventType = inEvent.getEventType();
        exchange = inEvent.getExchange();
        exchangeTimestamp = inEvent.getExchangeTimestamp();
        instrument = equityEvent.getInstrument();
        price = inEvent.getPrice();
        quoteDate = inEvent.getQuoteDate();
        size = inEvent.getSize();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.HasEquity#getInstrument()
     */
    @Override
    public Equity getInstrument()
    {
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return instrument == null ? null : instrument.getSymbol();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.QuoteEvent#getQuoteDate()
     */
    @Override
    public String getQuoteDate()
    {
        return quoteDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.QuoteEvent#getAction()
     */
    @Override
    public QuoteAction getAction()
    {
        return action;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketDataEvent#getExchange()
     */
    @Override
    public String getExchange()
    {
        return exchange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketDataEvent#getPrice()
     */
    @Override
    public BigDecimal getPrice()
    {
        return price;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketDataEvent#getSize()
     */
    @Override
    public BigDecimal getSize()
    {
        return size;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketDataEvent#getExchangeTimestamp()
     */
    @Override
    public String getExchangeTimestamp()
    {
        return exchangeTimestamp;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketDataEvent#getEventType()
     */
    @Override
    public EventType getEventType()
    {
        return eventType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketDataEvent#setEventType(org.marketcetera.core.event.EventType)
     */
    @Override
    public void setEventType(EventType inEventType)
    {
        eventType = inEventType;
    }
    @SuppressWarnings("unused")
    private WebServicesEquityAskEvent() {}
    @XmlElement
    private Equity instrument;
    @XmlAttribute
    private String quoteDate;
    @XmlAttribute
    private QuoteAction action;
    @XmlAttribute
    private String exchange;
    @XmlAttribute
    private BigDecimal price;
    @XmlAttribute
    private BigDecimal size;
    @XmlAttribute
    private String exchangeTimestamp;
    @XmlAttribute
    private EventType eventType;
    private static final long serialVersionUID = 1L;
}
