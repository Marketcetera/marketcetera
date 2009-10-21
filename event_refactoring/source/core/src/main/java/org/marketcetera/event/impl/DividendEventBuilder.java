package org.marketcetera.event.impl;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.beans.DividendBean;
import org.marketcetera.event.util.DividendFrequency;
import org.marketcetera.event.util.DividendStatus;
import org.marketcetera.event.util.DividendType;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link DividendEvent} objects.
 * 
 * <p>Construct a <code>DividendEvent</code> by getting a <code>DividendEventBuilder</code>,
 * setting the appropriate attributes on the builder, and calling {@link #create()}.  Note that
 * the builder does no validation.  The object does its own validation with {@link #create()} is
 * called.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public abstract class DividendEventBuilder
        extends AbstractEventBuilderImpl
        implements EventBuilder<DividendEvent>
{
    /**
     * Returns a <code>DividendEventBuilder</code> suitable for constructing a new <code>DividendEvent</code> object.
     *
     * @return a <code>DividendEventBuilder</code> value
     */
    public static DividendEventBuilder newDividend()
    {
        return new DividendEventBuilder(){
            @Override
            public DividendEvent create()
            {
                return new DividendEventImpl(getMessageId(),
                                             getTimestamp(),
                                             getDividend().getEquity(),
                                             getDividend().getAmount(),
                                             getDividend().getCurrency(),
                                             getDividend().getDeclareDate(),
                                             getDividend().getExecutionDate(),
                                             getDividend().getPaymentDate(),
                                             getDividend().getRecordDate(),
                                             getDividend().getFrequency(),
                                             getDividend().getStatus(),
                                             getDividend().getType());
            }
        };
    }
    /**
     * Sets the equity value.
     *
     * @param a <code>Equity</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withEquity(Equity inEquity)
    {
        dividend.setEquity(inEquity);
        return this;
    }
    /**
     * Sets the amount value.
     *
     * @param a <code>BigDecimal</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withAmount(BigDecimal inAmount)
    {
        dividend.setAmount(inAmount);
        return this;
    }
    /**
     * Sets the currency value.
     *
     * @param a <code>String</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withCurrency(String inCurrency)
    {
        dividend.setCurrency(inCurrency);
        return this;
    }
    /**
     * Sets the declareDate value.
     *
     * @param a <code>String</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withDeclareDate(String inDeclareDate)
    {
        dividend.setDeclareDate(inDeclareDate);
        return this;
    }
    /**
     * Sets the executionDate value.
     *
     * @param a <code>String</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withExecutionDate(String inExecutionDate)
    {
        dividend.setExecutionDate(inExecutionDate);
        return this;
    }
    /**
     * Sets the paymentDate value.
     *
     * @param a <code>String</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withPaymentDate(String inPaymentDate)
    {
        dividend.setPaymentDate(inPaymentDate);
        return this;
    }
    /**
     * Sets the recordDate value.
     *
     * @param a <code>String</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withRecordDate(String inRecordDate)
    {
        dividend.setRecordDate(inRecordDate);
        return this;
    }
    /**
     * Sets the dividendFrequency value.
     *
     * @param a <code>DividendFrequency</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withFrequency(DividendFrequency inDividendFrequency)
    {
        dividend.setFrequency(inDividendFrequency);
        return this;
    }
    /**
     * Sets the dividendStatus value.
     *
     * @param a <code>DividendStatus</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withStatus(DividendStatus inDividendStatus)
    {
        dividend.setStatus(inDividendStatus);
        return this;
    }
    /**
     * Sets the dividendType value.
     *
     * @param a <code>DividendType</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withType(DividendType inDividendType)
    {
        dividend.setType(inDividendType);
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DividendEventBuilder [dividend=").append(dividend).append(", getMessageId()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getMessageId()).append(", getTimestamp()=").append(getTimestamp()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        return builder.toString();
    }
    /**
     * Get the dividend value.
     *
     * @return a <code>DividendBean</code> value
     */
    protected final DividendBean getDividend()
    {
        return dividend;
    }
    /**
     * stores the dividend attributes 
     */
    private final DividendBean dividend = new DividendBean();
}
