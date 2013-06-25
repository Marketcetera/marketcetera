package org.marketcetera.core.event.impl;

import java.math.BigDecimal;

import org.marketcetera.core.event.AskEvent;
import org.marketcetera.core.event.ConvertibleSecurityEvent;
import org.marketcetera.core.event.beans.ConvertibleSecurityBean;
import org.marketcetera.core.event.beans.QuoteBean;
import org.marketcetera.core.trade.*;

/* $License$ */

/**
 * Represents an ask event for a convertible security.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class ConvertibleSecurityAskEventImpl
        extends AbstractQuoteEventImpl
        implements ConvertibleSecurityEvent, AskEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getInstrument()
     */
    @Override
    public ConvertibleSecurity getInstrument()
    {
        return (ConvertibleSecurity)super.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getParity()
     */
    @Override
    public BigDecimal getParity()
    {
        return security.getParity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return security.getUnderlyingEquity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getMaturity()
     */
    @Override
    public String getMaturity()
    {
        return security.getMaturity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getYield()
     */
    @Override
    public BigDecimal getYield()
    {
        return security.getYield();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getAmountOutstanding()
     */
    @Override
    public BigDecimal getAmountOutstanding()
    {
        return security.getAmountOutstanding();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getValueDate()
     */
    @Override
    public String getValueDate()
    {
        return security.getValueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getTraceReportTime()
     */
    @Override
    public String getTraceReportTime()
    {
        return security.getTraceReportTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getConversionPrice()
     */
    @Override
    public BigDecimal getConversionPrice()
    {
        return security.getConversionPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getConversionRatio()
     */
    @Override
    public BigDecimal getConversionRatio()
    {
        return security.getConversionRatio();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getAccruedInterest()
     */
    @Override
    public BigDecimal getAccruedInterest()
    {
        return security.getAccruedInterest();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIssuePrice()
     */
    @Override
    public BigDecimal getIssuePrice()
    {
        return security.getIssuePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getConversionPremium()
     */
    @Override
    public BigDecimal getConversionPremium()
    {
        return security.getConversionPremium();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getTheoreticalDelta()
     */
    @Override
    public BigDecimal getTheoreticalDelta()
    {
        return security.getTheoreticalDelta();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIssueDate()
     */
    @Override
    public String getIssueDate()
    {
        return security.getIssueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIssuerDomicile()
     */
    @Override
    public String getIssuerDomicile()
    {
        return security.getIssuerDomicile();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCurrency()
     */
    @Override
    public String getCurrency()
    {
        return security.getCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getBondCurrency()
     */
    @Override
    public String getBondCurrency()
    {
        return security.getBondCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCouponRate()
     */
    @Override
    public BigDecimal getCouponRate()
    {
        return security.getCouponRate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getPaymentFrequency()
     */
    @Override
    public String getPaymentFrequency()
    {
        return security.getPaymentFrequency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getExchangeCode()
     */
    @Override
    public String getExchangeCode()
    {
        return security.getExchangeCode();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCompanyName()
     */
    @Override
    public String getCompanyName()
    {
        return security.getCompanyName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getRating()
     */
    @Override
    public String getRating()
    {
        return security.getRating();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getRatingID()
     */
    @Override
    public String getRatingID()
    {
        return security.getRatingID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getParValue()
     */
    @Override
    public BigDecimal getParValue()
    {
        return security.getParValue();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIsin()
     */
    @Override
    public String getIsin()
    {
        return security.getIsin();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCusip()
     */
    @Override
    public String getCusip()
    {
        return security.getCusip();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getEstimatedSizeInd()
     */
	@Override
	public String getEstimatedSizeInd() 
	{
		return security.getEstimatedSizeInd();
	}
    
    /**
     * Create a new ConvertibleSecurityAskEventImpl instance.
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
    ConvertibleSecurityAskEventImpl(QuoteBean inQuote,
                                    ConvertibleSecurityBean inConvertibleSecurity)
    {
        super(inQuote);
        security = ConvertibleSecurityBean.copy(inConvertibleSecurity);
        security.validate();
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
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Convertible Security Ask"; //$NON-NLS-1$
    /**
     * the convertible security attributes 
     */
    private final ConvertibleSecurityBean security;
    private static final long serialVersionUID = 1L;

}
