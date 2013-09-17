package org.marketcetera.event.impl;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.ConvertibleBondEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.ConvertibleBondBean;
import org.marketcetera.event.beans.MarketDataBean;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a ConvertibleBond implementation of {@link TradeEvent}.
 *
 * @version $Id: ConvertibleBondTradeEventImpl.java 16598 2013-06-25 13:27:58Z colin $
 * @since 2.1.0
 */
@ThreadSafe
@ClassVersion("$Id$")
final class ConvertibleBondTradeEventImpl
        extends AbstractTradeEventImpl
        implements ConvertibleBondEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasConvertibleBond#getInstrument()
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
        return convertibleBond.getParity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return convertibleBond.getUnderlyingEquity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getMaturity()
     */
    @Override
    public String getMaturity()
    {
        return convertibleBond.getMaturity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getYield()
     */
    @Override
    public BigDecimal getYield()
    {
        return convertibleBond.getYield();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getAmountOutstanding()
     */
    @Override
    public BigDecimal getAmountOutstanding()
    {
        return convertibleBond.getAmountOutstanding();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getValueDate()
     */
    @Override
    public String getValueDate()
    {
        return convertibleBond.getValueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getTraceReportTime()
     */
    @Override
    public String getTraceReportTime()
    {
        return convertibleBond.getTraceReportTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getConversionPrice()
     */
    @Override
    public BigDecimal getConversionPrice()
    {
        return convertibleBond.getConversionPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getConversionRatio()
     */
    @Override
    public BigDecimal getConversionRatio()
    {
        return convertibleBond.getConversionRatio();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getAccruedInterest()
     */
    @Override
    public BigDecimal getAccruedInterest()
    {
        return convertibleBond.getAccruedInterest();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIssuePrice()
     */
    @Override
    public BigDecimal getIssuePrice()
    {
        return convertibleBond.getIssuePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getConversionPremium()
     */
    @Override
    public BigDecimal getConversionPremium()
    {
        return convertibleBond.getConversionPremium();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getTheoreticalDelta()
     */
    @Override
    public BigDecimal getTheoreticalDelta()
    {
        return convertibleBond.getTheoreticalDelta();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIssueDate()
     */
    @Override
    public String getIssueDate()
    {
        return convertibleBond.getIssueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIssuerDomicile()
     */
    @Override
    public String getIssuerDomicile()
    {
        return convertibleBond.getIssuerDomicile();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCurrency()
     */
    @Override
    public String getCurrency()
    {
        return convertibleBond.getCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getBondCurrency()
     */
    @Override
    public String getBondCurrency()
    {
        return convertibleBond.getBondCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCouponRate()
     */
    @Override
    public BigDecimal getCouponRate()
    {
        return convertibleBond.getCouponRate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getPaymentFrequency()
     */
    @Override
    public String getPaymentFrequency()
    {
        return convertibleBond.getPaymentFrequency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getExchangeCode()
     */
    @Override
    public String getExchangeCode()
    {
        return convertibleBond.getExchangeCode();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCompanyName()
     */
    @Override
    public String getCompanyName()
    {
        return convertibleBond.getCompanyName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getRating()
     */
    @Override
    public String getRating()
    {
        return convertibleBond.getRating();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getRatingID()
     */
    @Override
    public String getRatingID()
    {
        return convertibleBond.getRatingID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getParValue()
     */
    @Override
    public BigDecimal getParValue()
    {
        return convertibleBond.getParValue();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIsin()
     */
    @Override
    public String getIsin()
    {
        return convertibleBond.getIsin();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCusip()
     */
    @Override
    public String getCusip()
    {
        return convertibleBond.getCusip();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getEstimatedSizeInd()
     */
    @Override
    public String getEstimatedSizeInd()
    {
        return convertibleBond.getEstimatedSizeInd();
    }
    /**
     * Create a new ConvertibleBondTradeEventImpl instance.
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
    ConvertibleBondTradeEventImpl(MarketDataBean inMarketData,
                                      ConvertibleBondBean inConvertibleBond)
    {
        super(inMarketData);
        convertibleBond = inConvertibleBond;
        convertibleBond.validate();
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
    private static final String description = "Convertible Bond Trade"; //$NON-NLS-1$
    /**
     * the convertible Bond attributes 
     */
    private final ConvertibleBondBean convertibleBond;
    private static final long serialVersionUID = 1L;
}
