package org.marketcetera.marketdata.webservices;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.event.EquityEvent;
import org.marketcetera.core.event.EventType;
import org.marketcetera.core.event.TradeEvent;
import org.marketcetera.core.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="equityTradeEvent")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesEquityTradeEvent
        extends WebServicesEvent
        implements EquityEvent, TradeEvent
{
    public WebServicesEquityTradeEvent(TradeEvent inEvent)
    {
        super(inEvent);
        Validate.isTrue(inEvent instanceof EquityEvent);
        EquityEvent equityEvent = (EquityEvent)inEvent;
//        instrument = equityEvent.getInstrument();
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
    public Equity getInstrument()
    {
//        return instrument;
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
//        return instrument == null ? null : instrument.getSymbol();
        return null;
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
    private WebServicesEquityTradeEvent() {}
//    @XmlElement
//    private Equity instrument;
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
