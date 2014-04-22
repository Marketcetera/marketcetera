package org.marketcetera.event.impl;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.ConvertibleBondEvent;
import org.marketcetera.event.beans.ConvertibleBondBean;
import org.marketcetera.event.beans.QuoteBean;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents an ask event for a convertible bond.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ConvertibleBondAskEventImpl.java 16598 2013-06-25 13:27:58Z colin $
 * @since $Release$
 */
@XmlRootElement(name="convertibleBondAsk")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class ConvertibleBondAskEventImpl
        extends AbstractQuoteEventImpl
        implements ConvertibleBondEvent, AskEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getInstrument()
     */
    @Override
    public ConvertibleBond getInstrument()
    {
        return (ConvertibleBond)super.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getParity()
     */
    @Override
    public BigDecimal getParity()
    {
        return bond.getParity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return bond.getUnderlyingEquity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getMaturity()
     */
    @Override
    public String getMaturity()
    {
        return bond.getMaturity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getYield()
     */
    @Override
    public BigDecimal getYield()
    {
        return bond.getYield();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getAmountOutstanding()
     */
    @Override
    public BigDecimal getAmountOutstanding()
    {
        return bond.getAmountOutstanding();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getValueDate()
     */
    @Override
    public String getValueDate()
    {
        return bond.getValueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getTraceReportTime()
     */
    @Override
    public String getTraceReportTime()
    {
        return bond.getTraceReportTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getConversionPrice()
     */
    @Override
    public BigDecimal getConversionPrice()
    {
        return bond.getConversionPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getConversionRatio()
     */
    @Override
    public BigDecimal getConversionRatio()
    {
        return bond.getConversionRatio();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getAccruedInterest()
     */
    @Override
    public BigDecimal getAccruedInterest()
    {
        return bond.getAccruedInterest();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIssuePrice()
     */
    @Override
    public BigDecimal getIssuePrice()
    {
        return bond.getIssuePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getConversionPremium()
     */
    @Override
    public BigDecimal getConversionPremium()
    {
        return bond.getConversionPremium();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getTheoreticalDelta()
     */
    @Override
    public BigDecimal getTheoreticalDelta()
    {
        return bond.getTheoreticalDelta();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIssueDate()
     */
    @Override
    public String getIssueDate()
    {
        return bond.getIssueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIssuerDomicile()
     */
    @Override
    public String getIssuerDomicile()
    {
        return bond.getIssuerDomicile();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCurrency()
     */
    @Override
    public String getCurrency()
    {
        return bond.getCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getBondCurrency()
     */
    @Override
    public String getBondCurrency()
    {
        return bond.getBondCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCouponRate()
     */
    @Override
    public BigDecimal getCouponRate()
    {
        return bond.getCouponRate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getPaymentFrequency()
     */
    @Override
    public String getPaymentFrequency()
    {
        return bond.getPaymentFrequency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getExchangeCode()
     */
    @Override
    public String getExchangeCode()
    {
        return bond.getExchangeCode();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCompanyName()
     */
    @Override
    public String getCompanyName()
    {
        return bond.getCompanyName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getRating()
     */
    @Override
    public String getRating()
    {
        return bond.getRating();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getRatingID()
     */
    @Override
    public String getRatingID()
    {
        return bond.getRatingID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getParValue()
     */
    @Override
    public BigDecimal getParValue()
    {
        return bond.getParValue();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIsin()
     */
    @Override
    public String getIsin()
    {
        return bond.getIsin();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCusip()
     */
    @Override
    public String getCusip()
    {
        return bond.getCusip();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getEstimatedSizeInd()
     */
	@Override
	public String getEstimatedSizeInd() 
	{
		return bond.getEstimatedSizeInd();
	}
    
    /**
     * Create a new ConvertibleBondAskEventImpl instance.
     *
     * @param inQuote a <code>QuoteBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>Action</code> is <code>null</code>
     */
    ConvertibleBondAskEventImpl(QuoteBean inQuote,
                                ConvertibleBondBean inConvertibleBond)
    {
        super(inQuote);
        bond = ConvertibleBondBean.copy(inConvertibleBond);
        bond.validate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractQuoteEventImpl#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return description;
    }
    /**
     * Create a new ConvertibleBondAskEventImpl instance.
     *
     * <p>This constructor is intended to be used by JAXB only.
     */
    @SuppressWarnings("unused")
    private ConvertibleBondAskEventImpl()
    {
        bond = new ConvertibleBondBean();
    }
    /**
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Convertible Bond Ask"; //$NON-NLS-1$
    /**
     * the convertible bond attributes 
     */
    @XmlElement
    private final ConvertibleBondBean bond;
    private static final long serialVersionUID = 1L;

}
