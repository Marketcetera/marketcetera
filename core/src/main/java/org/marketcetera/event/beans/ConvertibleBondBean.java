package org.marketcetera.event.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

import org.marketcetera.event.ConvertibleBondEvent;
import org.marketcetera.event.Messages;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Contains the attributes of a convertible bond.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="convertibleBond")
@ClassVersion("$Id$")
public class ConvertibleBondBean
        implements Serializable
{
 	/**
     * Creates a shallow copy of the given <code>ConvertibleBondBean</code>.
     *
     * @param inBean a <code>ConvertibleBondBean</code> value
     * @return a <code>ConvertibleBondBean</code> value
     */
    public static ConvertibleBondBean copy(ConvertibleBondBean inBean)
    {
        ConvertibleBondBean newBean = new ConvertibleBondBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Builds a <code>ConvertibleBondBean</code> based on the values of
     * the given event.
     *
     * @param inConvertibleBondEvent a <code>ConvertibleBondEvent</code> value
     * @return a <code>ConvertibleBondBean</code> value
     */
    public static ConvertibleBondBean getConvertibleBondBeanFromEvent(ConvertibleBondEvent inConvertibleBondEvent)
    {
        ConvertibleBondBean bean = new ConvertibleBondBean();
        bean.setInstrument(inConvertibleBondEvent.getInstrument());
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
        builder.append("ConvertibleBondBean [instrument=");
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
     * @param inDonor a <code>ConvertibleBondBean</code> value
     * @param inRecipient a <code>ConvertibleBondBean</code> value
     */
    protected static void copyAttributes(ConvertibleBondBean inDonor,
                                         ConvertibleBondBean inRecipient)
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
    @XmlElement
    private Instrument instrument;
    /**
     * parity value
     */
    @XmlAttribute
    private BigDecimal parity;
    /**
     * underlying equity value
     */
    @XmlElement
    private Equity underlyingEquity;
    /**
     * maturity value
     */
    @XmlAttribute
    private String maturity;
    /**
     * yield value
     */
    @XmlAttribute
    private BigDecimal yield;
    /**
     * amount outstanding value
     */
    @XmlAttribute
    private BigDecimal amountOutstanding;
    /**
     * value date value
     */
    @XmlAttribute
    private String valueDate;
    /**
     * trace report time value
     */
    @XmlAttribute
    private String traceReportTime;
    /**
     * conversion price value
     */
    @XmlAttribute
    private BigDecimal conversionPrice;
    /**
     * conversion ratio value
     */
    @XmlAttribute
    private BigDecimal conversionRatio;
    /**
     * accrued interest value
     */
    @XmlAttribute
    private BigDecimal accruedInterest;
    /**
     * issue price value
     */
    @XmlAttribute
    private BigDecimal issuePrice;
    /**
     * conversion premium value
     */
    @XmlAttribute
    private BigDecimal conversionPremium;
    /**
     * theoretical delta value
     */
    @XmlAttribute
    private BigDecimal theoreticalDelta;
    /**
     * issue date value
     */
    @XmlAttribute
    private String issueDate;
    /**
     * issuer domicile value
     */
    @XmlAttribute
    private String issuerDomicile;
    /**
     * currency value
     */
    @XmlAttribute
    private String currency;
    /**
     * bond currency value
     */
    @XmlAttribute
    private String bondCurrency;
    /**
     * coupon rate value
     */
    @XmlAttribute
    private BigDecimal couponRate;
    /**
     * payment frequency value
     */
    @XmlAttribute
    private String paymentFrequency;
    /**
     * exchange code value
     */
    @XmlAttribute
    private String exchangeCode;
    /**
     * company name value
     */
    @XmlAttribute
    private String companyName;
    /**
     * rating value
     */
    @XmlAttribute
    private String rating;
    /**
     * rating ID value
     */
    @XmlAttribute
    private String ratingID;
    /**
     * par value
     */
    @XmlAttribute
    private BigDecimal parValue;
    /**
     * isin value
     */
    @XmlAttribute
    private String isin;
    /**
     * cusip value
     */
    @XmlAttribute
    private String cusip;
    /**
     * Estimated trade size Indicator
     */
    @XmlAttribute
    private String estimatedSizeInd;
    private static final long serialVersionUID = -6504290148975073754L;
}
