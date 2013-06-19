package org.marketcetera.marketdata.webservices;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

import org.marketcetera.core.event.ConvertibleSecurityEvent;
import org.marketcetera.core.event.MarketstatEvent;
import org.marketcetera.core.trade.ConvertibleSecurity;
import org.marketcetera.core.trade.Equity;

/* $License$ */

/**
 * Provides a specialized marketstat event for convertible securities.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="convertibleSecurityMarketstatEvent")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesConvertibleSecurityMarketstatEvent
        extends WebServicesMarketstatEvent
        implements ConvertibleSecurityEvent
{
    /**
     * Create a new WebServicesConvertibleSecurityMarketstatEvent instance.
     *
     * @param inEvent a <code>MarketstatEvent</code> value
     */
    public WebServicesConvertibleSecurityMarketstatEvent(MarketstatEvent inEvent)
    {
        super(inEvent);
        if(!(inEvent instanceof ConvertibleSecurityEvent)) {
            throw new IllegalArgumentException();
        }
        ConvertibleSecurityEvent event = (ConvertibleSecurityEvent)inEvent;
        accruedInterest = event.getAccruedInterest();
        amountOutstanding = event.getAmountOutstanding();
        bondCurrency = event.getBondCurrency();
        companyName = event.getCompanyName();
        conversionPremium = event.getConversionPremium();
        conversionPrice = event.getConversionPrice();
        conversionRatio = event.getConversionRatio();
        couponRate = event.getCouponRate();
        currency = event.getCurrency();
        exchangeCode = event.getExchangeCode();
        issueDate = event.getIssueDate();
        issuePrice = event.getIssuePrice();
        issuerDomicile = event.getIssuerDomicile();
        maturity = event.getMaturity();
        parity = event.getParity();
        parValue = event.getParValue();
        paymentFrequency = event.getPaymentFrequency();
        rating = event.getRating();
        ratingID = event.getRatingID();
        theoreticalDelta = event.getTheoreticalDelta();
        traceReportTime = event.getTraceReportTime();
        underlyingEquity = event.getUnderlyingEquity();
        valueDate = event.getValueDate();
        yield = event.getYield();
        isin = event.getIsin();
        cusip = event.getCusip();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.WebServicesMarketstatEvent#getInstrument()
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
        return parity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return underlyingEquity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getMaturity()
     */
    @Override
    public String getMaturity()
    {
        return maturity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getYield()
     */
    @Override
    public BigDecimal getYield()
    {
        return yield;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getAmountOutstanding()
     */
    @Override
    public BigDecimal getAmountOutstanding()
    {
        return amountOutstanding;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getValueDate()
     */
    @Override
    public String getValueDate()
    {
        return valueDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getTraceReportTime()
     */
    @Override
    public String getTraceReportTime()
    {
        return traceReportTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getConversionPrice()
     */
    @Override
    public BigDecimal getConversionPrice()
    {
        return conversionPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getConversionRatio()
     */
    @Override
    public BigDecimal getConversionRatio()
    {
        return conversionRatio;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getAccruedInterest()
     */
    @Override
    public BigDecimal getAccruedInterest()
    {
        return accruedInterest;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIssuePrice()
     */
    @Override
    public BigDecimal getIssuePrice()
    {
        return issuePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getConversionPremium()
     */
    @Override
    public BigDecimal getConversionPremium()
    {
        return conversionPremium;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getTheoreticalDelta()
     */
    @Override
    public BigDecimal getTheoreticalDelta()
    {
        return theoreticalDelta;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIssueDate()
     */
    @Override
    public String getIssueDate()
    {
        return issueDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIssuerDomicile()
     */
    @Override
    public String getIssuerDomicile()
    {
        return issuerDomicile;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCurrency()
     */
    @Override
    public String getCurrency()
    {
        return currency;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getBondCurrency()
     */
    @Override
    public String getBondCurrency()
    {
        return bondCurrency;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCouponRate()
     */
    @Override
    public BigDecimal getCouponRate()
    {
        return couponRate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getPaymentFrequency()
     */
    @Override
    public String getPaymentFrequency()
    {
        return paymentFrequency;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getExchangeCode()
     */
    @Override
    public String getExchangeCode()
    {
        return exchangeCode;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCompanyName()
     */
    @Override
    public String getCompanyName()
    {
        return companyName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getRating()
     */
    @Override
    public String getRating()
    {
        return rating;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getRatingID()
     */
    @Override
    public String getRatingID()
    {
        return ratingID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getParValue()
     */
    @Override
    public BigDecimal getParValue()
    {
        return parValue;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getIsin()
     */
    @Override
    public String getIsin()
    {
        return isin;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.ConvertibleSecurityEvent#getCusip()
     */
    @Override
    public String getCusip()
    {
        return cusip;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesConvertibleSecurityMarketstatEvent [parity=");
        builder.append(parity);
        builder.append(", underlyingEquity=");
        builder.append(underlyingEquity);
        builder.append(", maturity=");
        builder.append(maturity);
        builder.append(", yield=");
        builder.append(yield);
        builder.append(", amountOutstanding=");
        builder.append(amountOutstanding);
        builder.append(", valueDate=");
        builder.append(valueDate);
        builder.append(", traceReportTime=");
        builder.append(traceReportTime);
        builder.append(", conversionPrice=");
        builder.append(conversionPrice);
        builder.append(", conversionRatio=");
        builder.append(conversionRatio);
        builder.append(", accruedInterest=");
        builder.append(accruedInterest);
        builder.append(", issuePrice=");
        builder.append(issuePrice);
        builder.append(", conversionPremium=");
        builder.append(conversionPremium);
        builder.append(", theoreticalDelta=");
        builder.append(theoreticalDelta);
        builder.append(", issueDate=");
        builder.append(issueDate);
        builder.append(", issuerDomicile=");
        builder.append(issuerDomicile);
        builder.append(", currency=");
        builder.append(currency);
        builder.append(", bondCurrency=");
        builder.append(bondCurrency);
        builder.append(", couponRate=");
        builder.append(couponRate);
        builder.append(", paymentFrequency=");
        builder.append(paymentFrequency);
        builder.append(", exchangeCode=");
        builder.append(exchangeCode);
        builder.append(", companyName=");
        builder.append(companyName);
        builder.append(", rating=");
        builder.append(rating);
        builder.append(", ratingID=");
        builder.append(ratingID);
        builder.append(", parValue=");
        builder.append(parValue);
        builder.append(", isin=");
        builder.append(isin);
        builder.append(", cusip=");
        builder.append(cusip);
        builder.append("]");
        return builder.toString();
    }
    @SuppressWarnings("unused")
    private WebServicesConvertibleSecurityMarketstatEvent() {}
    @XmlAttribute
    private BigDecimal parity;
    @XmlElement
    private Equity underlyingEquity;
    @XmlAttribute
    private String maturity;
    @XmlAttribute
    private BigDecimal yield;
    @XmlAttribute
    private BigDecimal amountOutstanding;
    @XmlAttribute
    private String valueDate;
    @XmlAttribute
    private String traceReportTime;
    @XmlAttribute
    private BigDecimal conversionPrice;
    @XmlAttribute
    private BigDecimal conversionRatio;
    @XmlAttribute
    private BigDecimal accruedInterest;
    @XmlAttribute
    private BigDecimal issuePrice;
    @XmlAttribute
    private BigDecimal conversionPremium;
    @XmlAttribute
    private BigDecimal theoreticalDelta;
    @XmlAttribute
    private String issueDate;
    @XmlAttribute
    private String issuerDomicile;
    @XmlAttribute
    private String currency;
    @XmlAttribute
    private String bondCurrency;
    @XmlAttribute
    private BigDecimal couponRate;
    @XmlAttribute
    private String paymentFrequency;
    @XmlAttribute
    private String exchangeCode;
    @XmlAttribute
    private String companyName;
    @XmlAttribute
    private String rating;
    @XmlAttribute
    private String ratingID;
    @XmlAttribute
    private BigDecimal parValue;
    @XmlAttribute
    private String isin;
    @XmlAttribute
    private String cusip;
    private static final long serialVersionUID = 1544218578371211958L;
}
