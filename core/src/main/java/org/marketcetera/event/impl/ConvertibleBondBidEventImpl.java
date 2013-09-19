package org.marketcetera.event.impl;

import java.math.BigDecimal;

import org.marketcetera.event.BidEvent;
import org.marketcetera.event.ConvertibleBondEvent;
import org.marketcetera.event.beans.ConvertibleBondBean;
import org.marketcetera.event.beans.QuoteBean;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a bid event for a convertible Bond.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ConvertibleBondBidEventImpl.java 16598 2013-06-25 13:27:58Z colin $
 * @since $Release$
 */
@ClassVersion("$Id$")
class ConvertibleBondBidEventImpl
        extends AbstractQuoteEventImpl
        implements ConvertibleBondEvent, BidEvent
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
        return Bond.getParity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return Bond.getUnderlyingEquity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getMaturity()
     */
    @Override
    public String getMaturity()
    {
        return Bond.getMaturity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getYield()
     */
    @Override
    public BigDecimal getYield()
    {
        return Bond.getYield();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getAmountOutstanding()
     */
    @Override
    public BigDecimal getAmountOutstanding()
    {
        return Bond.getAmountOutstanding();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getValueDate()
     */
    @Override
    public String getValueDate()
    {
        return Bond.getValueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getTraceReportTime()
     */
    @Override
    public String getTraceReportTime()
    {
        return Bond.getTraceReportTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getConversionPrice()
     */
    @Override
    public BigDecimal getConversionPrice()
    {
        return Bond.getConversionPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getConversionRatio()
     */
    @Override
    public BigDecimal getConversionRatio()
    {
        return Bond.getConversionRatio();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getAccruedInterest()
     */
    @Override
    public BigDecimal getAccruedInterest()
    {
        return Bond.getAccruedInterest();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIssuePrice()
     */
    @Override
    public BigDecimal getIssuePrice()
    {
        return Bond.getIssuePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getConversionPremium()
     */
    @Override
    public BigDecimal getConversionPremium()
    {
        return Bond.getConversionPremium();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getTheoreticalDelta()
     */
    @Override
    public BigDecimal getTheoreticalDelta()
    {
        return Bond.getTheoreticalDelta();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIssueDate()
     */
    @Override
    public String getIssueDate()
    {
        return Bond.getIssueDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIssuerDomicile()
     */
    @Override
    public String getIssuerDomicile()
    {
        return Bond.getIssuerDomicile();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCurrency()
     */
    @Override
    public String getCurrency()
    {
        return Bond.getCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getBondCurrency()
     */
    @Override
    public String getBondCurrency()
    {
        return Bond.getBondCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCouponRate()
     */
    @Override
    public BigDecimal getCouponRate()
    {
        return Bond.getCouponRate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getPaymentFrequency()
     */
    @Override
    public String getPaymentFrequency()
    {
        return Bond.getPaymentFrequency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getExchangeCode()
     */
    @Override
    public String getExchangeCode()
    {
        return Bond.getExchangeCode();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCompanyName()
     */
    @Override
    public String getCompanyName()
    {
        return Bond.getCompanyName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getRating()
     */
    @Override
    public String getRating()
    {
        return Bond.getRating();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getRatingID()
     */
    @Override
    public String getRatingID()
    {
        return Bond.getRatingID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getParValue()
     */
    @Override
    public BigDecimal getParValue()
    {
        return Bond.getParValue();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getIsin()
     */
    @Override
    public String getIsin()
    {
        return Bond.getIsin();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getCusip()
     */
    @Override
    public String getCusip()
    {
        return Bond.getCusip();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleBondEvent#getEstimatedSizeInd()
     */
	@Override
	public String getEstimatedSizeInd() 
	{
		return Bond.getEstimatedSizeInd();
	}
    /**
     * Create a new ConvertibleBondBidEventImpl instance.
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
    ConvertibleBondBidEventImpl(QuoteBean inQuote,
                                ConvertibleBondBean inConvertibleBond)
    {
        super(inQuote);
        Bond = ConvertibleBondBean.copy(inConvertibleBond);
        Bond.validate();
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
    private static final String description = "Convertible Bond Bid"; //$NON-NLS-1$
    /**
     * the convertible Bond attributes 
     */
    private final ConvertibleBondBean Bond;
    private static final long serialVersionUID = 1L;
}
