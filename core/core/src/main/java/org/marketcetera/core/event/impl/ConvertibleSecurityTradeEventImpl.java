package org.marketcetera.core.event.impl;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.event.ConvertibleSecurityEvent;
import org.marketcetera.core.event.TradeEvent;
import org.marketcetera.core.event.beans.ConvertibleSecurityBean;
import org.marketcetera.core.event.beans.MarketDataBean;
import org.marketcetera.core.trade.*;

/* $License$ */

/**
 * Provides a ConvertibleSecurity implementation of {@link TradeEvent}.
 *
 * @version $Id$
 * @since 2.1.0
 */
@ThreadSafe
final class ConvertibleSecurityTradeEventImpl
        extends AbstractTradeEventImpl
        implements ConvertibleSecurityEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasConvertibleSecurity#getInstrument()
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
        return convertibleSecurity.getParity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return convertibleSecurity.getUnderlyingEquity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getMaturity()
     */
    @Override
    public String getMaturity()
    {
        return convertibleSecurity.getMaturity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getYield()
     */
    @Override
    public BigDecimal getYield()
    {
        return convertibleSecurity.getYield();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getAmountOutstanding()
     */
    @Override
    public BigDecimal getAmountOutstanding()
    {
        return convertibleSecurity.getAmountOutstanding();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getValueDate()
     */
    @Override
    public String getValueDate()
    {
        return convertibleSecurity.getValueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getTraceReportTime()
     */
    @Override
    public String getTraceReportTime()
    {
        return convertibleSecurity.getTraceReportTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getConversionPrice()
     */
    @Override
    public BigDecimal getConversionPrice()
    {
        return convertibleSecurity.getConversionPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getConversionRatio()
     */
    @Override
    public BigDecimal getConversionRatio()
    {
        return convertibleSecurity.getConversionRatio();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getAccruedInterest()
     */
    @Override
    public BigDecimal getAccruedInterest()
    {
        return convertibleSecurity.getAccruedInterest();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIssuePrice()
     */
    @Override
    public BigDecimal getIssuePrice()
    {
        return convertibleSecurity.getIssuePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getConversionPremium()
     */
    @Override
    public BigDecimal getConversionPremium()
    {
        return convertibleSecurity.getConversionPremium();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getTheoreticalDelta()
     */
    @Override
    public BigDecimal getTheoreticalDelta()
    {
        return convertibleSecurity.getTheoreticalDelta();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIssueDate()
     */
    @Override
    public String getIssueDate()
    {
        return convertibleSecurity.getIssueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIssuerDomicile()
     */
    @Override
    public String getIssuerDomicile()
    {
        return convertibleSecurity.getIssuerDomicile();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCurrency()
     */
    @Override
    public String getCurrency()
    {
        return convertibleSecurity.getCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getBondCurrency()
     */
    @Override
    public String getBondCurrency()
    {
        return convertibleSecurity.getBondCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCouponRate()
     */
    @Override
    public BigDecimal getCouponRate()
    {
        return convertibleSecurity.getCouponRate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getPaymentFrequency()
     */
    @Override
    public String getPaymentFrequency()
    {
        return convertibleSecurity.getPaymentFrequency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getExchangeCode()
     */
    @Override
    public String getExchangeCode()
    {
        return convertibleSecurity.getExchangeCode();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCompanyName()
     */
    @Override
    public String getCompanyName()
    {
        return convertibleSecurity.getCompanyName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getRating()
     */
    @Override
    public String getRating()
    {
        return convertibleSecurity.getRating();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getRatingID()
     */
    @Override
    public String getRatingID()
    {
        return convertibleSecurity.getRatingID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getParValue()
     */
    @Override
    public BigDecimal getParValue()
    {
        return convertibleSecurity.getParValue();
    }
    /**
     * Create a new ConvertibleSecurityTradeEventImpl instance.
     *
     * @param inMarketData a <code>MarketDataBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     */
    ConvertibleSecurityTradeEventImpl(MarketDataBean inMarketData,
                         ConvertibleSecurityBean inConvertibleSecurity)
    {
        super(inMarketData);
        convertibleSecurity = inConvertibleSecurity;
        convertibleSecurity.validate();
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
    private static final String description = "Convertible Security Trade"; //$NON-NLS-1$
    /**
     * the convertible security attributes 
     */
    private final ConvertibleSecurityBean convertibleSecurity;
    private static final long serialVersionUID = 1L;
}
