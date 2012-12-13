package org.marketcetera.marketdata.webservices;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.event.EventType;
import org.marketcetera.core.event.HasInstrument;
import org.marketcetera.core.event.MarketstatEvent;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="marketstatEvent")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesMarketstatEvent
        extends WebServicesEvent
        implements HasInstrument, MarketstatEvent
{
    /**
     * Create a new WebServicesMarketstatEvent instance.
     *
     * @param inEvent a <code>MarketstatEvent</code> value
     */
    public WebServicesMarketstatEvent(MarketstatEvent inEvent)
    {
        super(inEvent);
        open = inEvent.getOpen();
        high = inEvent.getHigh();
        low = inEvent.getLow();
        close = inEvent.getClose();
        previousClose = inEvent.getPreviousClose();
        volume = inEvent.getVolume();
        value = inEvent.getValue();
        closeDate = inEvent.getCloseDate();
        previousCloseDate = inEvent.getPreviousCloseDate();
        tradeHighTime = inEvent.getTradeHighTime();
        tradeLowTime = inEvent.getTradeLowTime();
        openExchange = inEvent.getOpenExchange();
        highExchange = inEvent.getHighExchange();
        lowExchange = inEvent.getLowExchange();
        closeExchange = inEvent.getCloseExchange();
        eventType = inEvent.getEventType();
        instrument = inEvent.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getOpen()
     */
    @Override
    public BigDecimal getOpen()
    {
        return open;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getHigh()
     */
    @Override
    public BigDecimal getHigh()
    {
        return high;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getLow()
     */
    @Override
    public BigDecimal getLow()
    {
        return low;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getClose()
     */
    @Override
    public BigDecimal getClose()
    {
        return close;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getPreviousClose()
     */
    @Override
    public BigDecimal getPreviousClose()
    {
        return previousClose;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getVolume()
     */
    @Override
    public BigDecimal getVolume()
    {
        return volume;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getValue()
     */
    @Override
    public BigDecimal getValue()
    {
        return value;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getCloseDate()
     */
    @Override
    public String getCloseDate()
    {
        return closeDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getPreviousCloseDate()
     */
    @Override
    public String getPreviousCloseDate()
    {
        return previousCloseDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getTradeHighTime()
     */
    @Override
    public String getTradeHighTime()
    {
        return tradeHighTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getTradeLowTime()
     */
    @Override
    public String getTradeLowTime()
    {
        return tradeLowTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getOpenExchange()
     */
    @Override
    public String getOpenExchange()
    {
        return openExchange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getHighExchange()
     */
    @Override
    public String getHighExchange()
    {
        return highExchange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getLowExchange()
     */
    @Override
    public String getLowExchange()
    {
        return lowExchange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getCloseExchange()
     */
    @Override
    public String getCloseExchange()
    {
        return closeExchange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#getEventType()
     */
    @Override
    public EventType getEventType()
    {
        return eventType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.MarketstatEvent#setEventType(org.marketcetera.core.event.EventType)
     */
    @Override
    public void setEventType(EventType inEventType)
    {
        eventType = inEventType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.HasInstrument#getInstrument()
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
        return instrument == null ? null : instrument.getFullSymbol();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.WebServicesEvent#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE).append(instrument).append(open).append(high).append(low).append(close)
                .append(previousClose).append(volume).append(value).append(closeDate).append(previousCloseDate).append(tradeHighTime)
                .append(tradeLowTime).append(openExchange).append(highExchange).append(lowExchange).append(closeExchange).append(eventType).toString();
    }
    /**
     * Create a new WebServicesMarketstatEvent instance.
     */
    @SuppressWarnings("unused")
    private WebServicesMarketstatEvent() {}
    @XmlAttribute
    private BigDecimal open;
    @XmlAttribute
    private BigDecimal high;
    @XmlAttribute
    private BigDecimal low;
    @XmlAttribute
    private BigDecimal close;
    @XmlAttribute
    private BigDecimal previousClose;
    @XmlAttribute
    private BigDecimal volume;
    @XmlAttribute
    private BigDecimal value;
    @XmlAttribute
    private String closeDate;
    @XmlAttribute
    private String previousCloseDate;
    @XmlAttribute
    private String tradeHighTime;
    @XmlAttribute
    private String tradeLowTime;
    @XmlAttribute
    private String openExchange;
    @XmlAttribute
    private String highExchange;
    @XmlAttribute
    private String lowExchange;
    @XmlAttribute
    private String closeExchange;
    @XmlAttribute
    private EventType eventType;
    @XmlElement
    private Instrument instrument;
    private static final long serialVersionUID = 1L;
}
