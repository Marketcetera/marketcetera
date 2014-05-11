package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.event.beans.HasConvertibleBond;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the implementing class represents a convertible bond event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ConvertibleSecurityEvent.java 16598 2013-06-25 13:27:58Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface ConvertibleBondEvent
        extends HasConvertibleBond,Event
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
    public String getEstimatedSizeInd();
}
