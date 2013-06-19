package org.marketcetera.core.event;

import java.math.BigDecimal;

import org.marketcetera.core.trade.Equity;

/* $License$ */

/**
 * Indicates that the implementing class represents a convertible bond event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ConvertibleSecurityEvent
        extends HasConvertibleSecurity,Event
{
    public BigDecimal getParity();
    public Equity getUnderlyingEquity();
    public String getMaturity();
    public BigDecimal getYield();
    public BigDecimal getAmountOutstanding();
    public String getValueDate();
    public String getTraceReportTime();
    public BigDecimal getConversionPrice();
    public BigDecimal getConversionRatio();
    public BigDecimal getAccruedInterest();
    public BigDecimal getIssuePrice();
    public BigDecimal getConversionPremium();
    public BigDecimal getTheoreticalDelta();
    public String getIssueDate();
    public String getIssuerDomicile();
    public String getCurrency();
    public String getBondCurrency();
    public BigDecimal getCouponRate();
    public String getPaymentFrequency();
    public String getExchangeCode();
    public String getCompanyName();
    public String getRating();
    public String getRatingID();
    public BigDecimal getParValue();
    public String getIsin();
    public String getCusip();
}
