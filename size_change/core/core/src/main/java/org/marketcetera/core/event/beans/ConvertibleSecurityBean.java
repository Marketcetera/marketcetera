package org.marketcetera.core.event.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import org.marketcetera.core.event.ConvertibleSecurityEvent;
import org.marketcetera.core.event.Messages;
import org.marketcetera.core.event.util.EventServices;
import org.marketcetera.core.trade.Equity;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Contains the attributes of a convertible security.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleSecurityBean
        implements Serializable
{
    /**
     * Creates a shallow copy of the given <code>ConvertibleSecurityBean</code>.
     *
     * @param inBean a <code>ConvertibleSecurityBean</code> value
     * @return a <code>ConvertibleSecurityBean</code> value
     */
    public static ConvertibleSecurityBean copy(ConvertibleSecurityBean inBean)
    {
        ConvertibleSecurityBean newBean = new ConvertibleSecurityBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Builds a <code>ConvertibleSecurityBean</code> based on the values of
     * the given event.
     *
     * @param inConvertibleSecurityEvent a <code>ConvertibleSecurityEvent</code> value
     * @return a <code>ConvertibleSecurityBean</code> value
     */
    public static ConvertibleSecurityBean getConvertibleSecurityBeanFromEvent(ConvertibleSecurityEvent inConvertibleSecurityEvent)
    {
        ConvertibleSecurityBean bean = new ConvertibleSecurityBean();
        bean.setInstrument(inConvertibleSecurityEvent.getInstrument());
        return bean;
    }
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /**
     * Get the parity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getParity()
    {
        return parity;
    }
    /**
     * Sets the parity value.
     *
     * @param inParity a <code>BigDecimal</code> value
     */
    public void setParity(BigDecimal inParity)
    {
        parity = inParity;
    }
    /**
     * Get the underlyingEquity value.
     *
     * @return an <code>Equity</code> value
     */
    public Equity getUnderlyingEquity()
    {
        return underlyingEquity;
    }
    /**
     * Sets the underlyingEquity value.
     *
     * @param inUnderlyingEquity an <code>Equity</code> value
     */
    public void setUnderlyingEquity(Equity inUnderlyingEquity)
    {
        underlyingEquity = inUnderlyingEquity;
    }
    /**
     * Get the maturity value.
     *
     * @return a <code>String</code> value
     */
    public String getMaturity()
    {
        return maturity;
    }
    /**
     * Sets the maturity value.
     *
     * @param inMaturity a <code>String</code> value
     */
    public void setMaturity(String inMaturity)
    {
        maturity = inMaturity;
    }
    /**
     * Get the yield value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getYield()
    {
        return yield;
    }
    /**
     * Sets the yield value.
     *
     * @param inYield a <code>BigDecimal</code> value
     */
    public void setYield(BigDecimal inYield)
    {
        yield = inYield;
    }
    /**
     * Get the amountOutstanding value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAmountOutstanding()
    {
        return amountOutstanding;
    }
    /**
     * Sets the amountOutstanding value.
     *
     * @param inAmountOutstanding a <code>BigDecimal</code> value
     */
    public void setAmountOutstanding(BigDecimal inAmountOutstanding)
    {
        amountOutstanding = inAmountOutstanding;
    }
    /**
     * Get the valueDate value.
     *
     * @return a <code>String</code> value
     */
    public String getValueDate()
    {
        return valueDate;
    }
    /**
     * Sets the valueDate value.
     *
     * @param inValueDate a <code>String</code> value
     */
    public void setValueDate(String inValueDate)
    {
        valueDate = inValueDate;
    }
    /**
     * Get the traceReportTime value.
     *
     * @return a <code>String</code> value
     */
    public String getTraceReportTime()
    {
        return traceReportTime;
    }
    /**
     * Sets the traceReportTime value.
     *
     * @param inTraceReportTime a <code>String</code> value
     */
    public void setTraceReportTime(String inTraceReportTime)
    {
        traceReportTime = inTraceReportTime;
    }
    /**
     * Get the conversionPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getConversionPrice()
    {
        return conversionPrice;
    }
    /**
     * Sets the conversionPrice value.
     *
     * @param inConversionPrice a <code>BigDecimal</code> value
     */
    public void setConversionPrice(BigDecimal inConversionPrice)
    {
        conversionPrice = inConversionPrice;
    }
    /**
     * Get the conversionRatio value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getConversionRatio()
    {
        return conversionRatio;
    }
    /**
     * Sets the conversionRatio value.
     *
     * @param inConversionRatio a <code>BigDecimal</code> value
     */
    public void setConversionRatio(BigDecimal inConversionRatio)
    {
        conversionRatio = inConversionRatio;
    }
    /**
     * Get the accruedInterest value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAccruedInterest()
    {
        return accruedInterest;
    }
    /**
     * Sets the accruedInterest value.
     *
     * @param inAccruedInterest a <code>BigDecimal</code> value
     */
    public void setAccruedInterest(BigDecimal inAccruedInterest)
    {
        accruedInterest = inAccruedInterest;
    }
    /**
     * Get the issuePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getIssuePrice()
    {
        return issuePrice;
    }
    /**
     * Sets the issuePrice value.
     *
     * @param inIssuePrice a <code>BigDecimal</code> value
     */
    public void setIssuePrice(BigDecimal inIssuePrice)
    {
        issuePrice = inIssuePrice;
    }
    /**
     * Get the conversionPremium value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getConversionPremium()
    {
        return conversionPremium;
    }
    /**
     * Sets the conversionPremium value.
     *
     * @param inConversionPremium a <code>BigDecimal</code> value
     */
    public void setConversionPremium(BigDecimal inConversionPremium)
    {
        conversionPremium = inConversionPremium;
    }
    /**
     * Get the theoreticalDelta value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getTheoreticalDelta()
    {
        return theoreticalDelta;
    }
    /**
     * Sets the theoreticalDelta value.
     *
     * @param inTheoreticalDelta a <code>BigDecimal</code> value
     */
    public void setTheoreticalDelta(BigDecimal inTheoreticalDelta)
    {
        theoreticalDelta = inTheoreticalDelta;
    }
    /**
     * Get the issueDate value.
     *
     * @return a <code>String</code> value
     */
    public String getIssueDate()
    {
        return issueDate;
    }
    /**
     * Sets the issueDate value.
     *
     * @param inIssueDate a <code>String</code> value
     */
    public void setIssueDate(String inIssueDate)
    {
        issueDate = inIssueDate;
    }
    /**
     * Get the issuerDomicile value.
     *
     * @return a <code>String</code> value
     */
    public String getIssuerDomicile()
    {
        return issuerDomicile;
    }
    /**
     * Sets the issuerDomicile value.
     *
     * @param inIssuerDomicile a <code>String</code> value
     */
    public void setIssuerDomicile(String inIssuerDomicile)
    {
        issuerDomicile = inIssuerDomicile;
    }
    /**
     * Get the currency value.
     *
     * @return a <code>String</code> value
     */
    public String getCurrency()
    {
        return currency;
    }
    /**
     * Sets the currency value.
     *
     * @param inCurrency a <code>String</code> value
     */
    public void setCurrency(String inCurrency)
    {
        currency = inCurrency;
    }
    /**
     * Get the bondCurrency value.
     *
     * @return a <code>String</code> value
     */
    public String getBondCurrency()
    {
        return bondCurrency;
    }
    /**
     * Sets the bondCurrency value.
     *
     * @param inBondCurrency a <code>String</code> value
     */
    public void setBondCurrency(String inBondCurrency)
    {
        bondCurrency = inBondCurrency;
    }
    /**
     * Get the couponRate value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCouponRate()
    {
        return couponRate;
    }
    /**
     * Sets the couponRate value.
     *
     * @param inCouponRate a <code>BigDecimal</code> value
     */
    public void setCouponRate(BigDecimal inCouponRate)
    {
        couponRate = inCouponRate;
    }
    /**
     * Get the paymentFrequency value.
     *
     * @return a <code>String</code> value
     */
    public String getPaymentFrequency()
    {
        return paymentFrequency;
    }
    /**
     * Sets the paymentFrequency value.
     *
     * @param inPaymentFrequency a <code>String</code> value
     */
    public void setPaymentFrequency(String inPaymentFrequency)
    {
        paymentFrequency = inPaymentFrequency;
    }
    /**
     * Get the exchangeCode value.
     *
     * @return a <code>String</code> value
     */
    public String getExchangeCode()
    {
        return exchangeCode;
    }
    /**
     * Sets the exchangeCode value.
     *
     * @param inExchangeCode a <code>String</code> value
     */
    public void setExchangeCode(String inExchangeCode)
    {
        exchangeCode = inExchangeCode;
    }
    /**
     * Get the companyName value.
     *
     * @return a <code>String</code> value
     */
    public String getCompanyName()
    {
        return companyName;
    }
    /**
     * Sets the companyName value.
     *
     * @param inCompanyName a <code>String</code> value
     */
    public void setCompanyName(String inCompanyName)
    {
        companyName = inCompanyName;
    }
    /**
     * Get the rating value.
     *
     * @return a <code>String</code> value
     */
    public String getRating()
    {
        return rating;
    }
    /**
     * Sets the rating value.
     *
     * @param inRating a <code>String</code> value
     */
    public void setRating(String inRating)
    {
        rating = inRating;
    }
    /**
     * Get the ratingID value.
     *
     * @return a <code>String</code> value
     */
    public String getRatingID()
    {
        return ratingID;
    }
    /**
     * Sets the ratingID value.
     *
     * @param inRatingID a <code>String</code> value
     */
    public void setRatingID(String inRatingID)
    {
        ratingID = inRatingID;
    }
    /**
     * Get the parValue value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getParValue()
    {
        return parValue;
    }
    /**
     * Sets the parValue value.
     *
     * @param inParValue a <code>BigDecimal</code> value
     */
    public void setParValue(BigDecimal inParValue)
    {
        parValue = inParValue;
    }
    /**
     * Performs validation of the attributes.
     *
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    public void validate()
    {
        if(instrument == null) {
            EventServices.error(Messages.VALIDATION_NULL_INSTRUMENT);
        }
    }
    /**
     * Get the isin value.
     *
     * @return a <code>String</code> value
     */
    public String getIsin()
    {
        return isin;
    }
    /**
     * Sets the isin value.
     *
     * @param inIsin a <code>String</code> value
     */
    public void setIsin(String inIsin)
    {
        isin = inIsin;
    }
    /**
     * Get the cusip value.
     *
     * @return a <code>String</code> value
     */
    public String getCusip()
    {
        return cusip;
    }
    /**
     * Sets the cusip value.
     *
     * @param inCusip a <code>String</code> value
     */
    public void setCusip(String inCusip)
    {
        cusip = inCusip;
    }
    /**
     * Get the estimatedSizeInd value.
     *
     * @return a <code>String</code> value
     */
    public String getEstimatedSizeInd()
    {
        return estimatedSizeInd;
    }
    /**
     * Sets the estimatedSizeInd value.
     *
     * @param inEstimatedSizeInd a <code>String</code> value
     */
    public void setEstimatedSizeInd(String inEstimatedSizeInd)
    {
    	estimatedSizeInd = inEstimatedSizeInd;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ConvertibleSecurityBean [instrument=");
        builder.append(instrument);
        builder.append(", parity=");
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
        builder.append(", estimatedSizeInd=");
        builder.append(estimatedSizeInd);
        builder.append("]");
        return builder.toString();
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor a <code>ConvertibleSecurityBean</code> value
     * @param inRecipient a <code>ConvertibleSecurityBean</code> value
     */
    protected static void copyAttributes(ConvertibleSecurityBean inDonor,
                                         ConvertibleSecurityBean inRecipient)
    {
        inRecipient.setAccruedInterest(inDonor.getAccruedInterest());
        inRecipient.setAmountOutstanding(inDonor.getAmountOutstanding());
        inRecipient.setBondCurrency(inDonor.getBondCurrency());
        inRecipient.setCompanyName(inDonor.getCompanyName());
        inRecipient.setConversionPremium(inDonor.getConversionPremium());
        inRecipient.setConversionPrice(inDonor.getConversionPrice());
        inRecipient.setConversionRatio(inDonor.getConversionRatio());
        inRecipient.setCouponRate(inDonor.getCouponRate());
        inRecipient.setCurrency(inDonor.getCurrency());
        inRecipient.setExchangeCode(inDonor.getExchangeCode());
        inRecipient.setInstrument(inDonor.getInstrument());
        inRecipient.setIssueDate(inDonor.getIssueDate());
        inRecipient.setIssuePrice(inDonor.getIssuePrice());
        inRecipient.setIssuerDomicile(inDonor.getIssuerDomicile());
        inRecipient.setMaturity(inDonor.getMaturity());
        inRecipient.setParity(inDonor.getParity());
        inRecipient.setParValue(inDonor.getParValue());
        inRecipient.setPaymentFrequency(inDonor.getPaymentFrequency());
        inRecipient.setRating(inDonor.getRating());
        inRecipient.setRatingID(inDonor.getRatingID());
        inRecipient.setTheoreticalDelta(inDonor.getTheoreticalDelta());
        inRecipient.setTraceReportTime(inDonor.getTraceReportTime());
        inRecipient.setUnderlyingEquity(inDonor.getUnderlyingEquity());
        inRecipient.setValueDate(inDonor.getValueDate());
        inRecipient.setYield(inDonor.getYield());
        inRecipient.setIsin(inDonor.getIsin());
        inRecipient.setCusip(inDonor.getCusip());
        inRecipient.setEstimatedSizeInd(inDonor.getEstimatedSizeInd());
    }
    /**
     * the instrument value
     */
    private Instrument instrument;
    /**
     * parity value
     */
    private BigDecimal parity;
    /**
     * underlying equity value
     */
    private Equity underlyingEquity;
    /**
     * maturity value
     */
    private String maturity;
    /**
     * yield value
     */
    private BigDecimal yield;
    /**
     * amount outstanding value
     */
    private BigDecimal amountOutstanding;
    /**
     * value date value
     */
    private String valueDate;
    /**
     * trace report time value
     */
    private String traceReportTime;
    /**
     * conversion price value
     */
    private BigDecimal conversionPrice;
    /**
     * conversion ratio value
     */
    private BigDecimal conversionRatio;
    /**
     * accrued interest value
     */
    private BigDecimal accruedInterest;
    /**
     * issue price value
     */
    private BigDecimal issuePrice;
    /**
     * conversion premium value
     */
    private BigDecimal conversionPremium;
    /**
     * theoretical delta value
     */
    private BigDecimal theoreticalDelta;
    /**
     * issue date value
     */
    private String issueDate;
    /**
     * issuer domicile value
     */
    private String issuerDomicile;
    /**
     * currency value
     */
    private String currency;
    /**
     * bond currency value
     */
    private String bondCurrency;
    /**
     * coupon rate value
     */
    private BigDecimal couponRate;
    /**
     * payment frequency value
     */
    private String paymentFrequency;
    /**
     * exchange code value
     */
    private String exchangeCode;
    /**
     * company name value
     */
    private String companyName;
    /**
     * rating value
     */
    private String rating;
    /**
     * rating ID value
     */
    private String ratingID;
    /**
     * par value
     */
    private BigDecimal parValue;
    /**
     * isin value
     */
    private String isin;
    /**
     * cusip value
     */
    private String cusip;
    /**
     * Estimated trade size Indicator
     */
    private String estimatedSizeInd;
    
    private static final long serialVersionUID = -5218198105173604486L;
}
