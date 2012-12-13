package org.marketcetera.marketdata.webservices;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

import org.marketcetera.core.event.*;
import org.marketcetera.core.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="event")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesDividendEvent
        extends WebServicesEvent
        implements DividendEvent
{
    /**
     * Create a new WebServicesDividendEvent instance.
     *
     * @param inEvent a <code>DividendEvent</code> value
     */
    public WebServicesDividendEvent(DividendEvent inEvent)
    {
        instrument = inEvent.getInstrument();
        amount = inEvent.getAmount();
        currency = inEvent.getCurrency();
        declareDate = inEvent.getDeclareDate();
        dividendFrequency = inEvent.getFrequency();
        dividendStatus = inEvent.getStatus();
        dividendType = inEvent.getType();
        eventType = inEvent.getEventType();
        executionDate = inEvent.getExecutionDate();
        paymentDate = inEvent.getPaymentDate();
        recordDate = inEvent.getRecordDate();
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
     * @see org.marketcetera.core.event.DividendEvent#getAmount()
     */
    @Override
    public BigDecimal getAmount()
    {
        return amount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getCurrency()
     */
    @Override
    public String getCurrency()
    {
        return currency;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getDeclareDate()
     */
    @Override
    public String getDeclareDate()
    {
        return declareDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getExecutionDate()
     */
    @Override
    public String getExecutionDate()
    {
        return executionDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getFrequency()
     */
    @Override
    public DividendFrequency getFrequency()
    {
        return dividendFrequency;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getEquity()
     */
    @Override
    public Equity getEquity()
    {
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getPaymentDate()
     */
    @Override
    public String getPaymentDate()
    {
        return paymentDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getRecordDate()
     */
    @Override
    public String getRecordDate()
    {
        return recordDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getStatus()
     */
    @Override
    public DividendStatus getStatus()
    {
        return dividendStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getType()
     */
    @Override
    public DividendType getType()
    {
        return dividendType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#getEventType()
     */
    @Override
    public EventType getEventType()
    {
        return eventType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.DividendEvent#setEventType(org.marketcetera.core.event.EventType)
     */
    @Override
    public void setEventType(EventType inEventType)
    {
        eventType = inEventType;
    }
    @SuppressWarnings("unused")
    private WebServicesDividendEvent() {}
    @XmlElement
    private Equity instrument;
    @XmlAttribute
    private BigDecimal amount;
    @XmlAttribute
    private String currency;
    @XmlAttribute
    private String declareDate;
    @XmlAttribute
    private String executionDate;
    @XmlAttribute
    private DividendFrequency dividendFrequency;
    @XmlAttribute
    private String paymentDate;
    @XmlAttribute
    private String recordDate;
    @XmlAttribute
    private DividendStatus dividendStatus;
    @XmlAttribute
    private DividendType dividendType;
    @XmlAttribute
    private EventType eventType;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
}
