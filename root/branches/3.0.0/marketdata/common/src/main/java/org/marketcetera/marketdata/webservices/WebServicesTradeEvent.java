package org.marketcetera.marketdata.webservices;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.event.EquityEvent;
import org.marketcetera.core.event.EventType;
import org.marketcetera.core.event.HasInstrument;
import org.marketcetera.core.event.TradeEvent;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="tradeEvent")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesTradeEvent
        extends WebServicesEvent
        implements HasInstrument, TradeEvent
{
    public WebServicesTradeEvent(TradeEvent inEvent)
    {
        super(inEvent);
        Validate.isTrue(inEvent instanceof EquityEvent);
        instrument = inEvent.getInstrument();
        exchange = inEvent.getExchange();
        eventType = inEvent.getEventType();
        exchangeTimestamp = inEvent.getExchangeTimestamp();
        price = inEvent.getPrice();
        size = inEvent.getSize();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.HasEquity#getInstrument()
     */
    @Override
    public Instrument getInstrument()
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
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.TradeEvent#getTradeDate()
     */
    @Override
    public String getTradeDate()
    {
        return tradeDate;
    }
    @SuppressWarnings("unused")
    private WebServicesTradeEvent() {}
    @XmlElement
    private Instrument instrument;
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
    @XmlAttribute
    private String tradeDate;
    private static final long serialVersionUID = 1L;
}
