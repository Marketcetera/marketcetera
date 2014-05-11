package org.marketcetera.event.impl;

import java.math.BigDecimal;

import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Builds events for convertible bond events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface ConvertibleBondEventBuilder<Clazz>
{
    /**
     * Sets the parity value.
     *
     * @param inParity a <code>BigDecimal</code> value
     * @return a <code>Clazz</code> value
     */
    public Clazz withParity(BigDecimal inParity);
    public Clazz withUnderlyingEquity(Equity inEquity);
    public Clazz withMaturity(String inMaturity);
    public Clazz withYield(BigDecimal inYield);
    public Clazz withAmountOutstanding(BigDecimal inAmountOutstanding);
    public Clazz withValueDate(String inValueDate);
    public Clazz withTraceReportTime(String inTraceReportTime);
    public Clazz withConversionPrice(BigDecimal inConversionPrice);
    public Clazz withConversionRatio(BigDecimal inConversionRatio);
    public Clazz withAccruedInterest(BigDecimal inAccruedInterest);
    public Clazz withIssuePrice(BigDecimal inIssuePrice);
    public Clazz withConversionPremium(BigDecimal inConversionPremium);
    public Clazz withTheoreticalDelta(BigDecimal inTheoreticalDelta);
    public Clazz withIssueDate(String inIssueDate);
    public Clazz withIssuerDomicile(String inIssuerDomicile);
    public Clazz withCurrency(String inCurrency);
    public Clazz withBondCurrency(String inBondCurrency);
    public Clazz withCouponRate(BigDecimal inCouponRate);
    public Clazz withPaymentFrequency(String inPaymentFrequency);
    public Clazz withExchangeCode(String inExchangeCode);
    public Clazz withCompanyName(String inCompanyName);
    public Clazz withRating(String inRating);
    public Clazz withRatingID(String inRatingID);
    public Clazz withParValue(BigDecimal inParValue);
    public Clazz withIsin(String inIsin);
    public Clazz withCusip(String inCusip);
    public Clazz withEstimatedSizeInd(String inEstimatedSizeInd);
}
