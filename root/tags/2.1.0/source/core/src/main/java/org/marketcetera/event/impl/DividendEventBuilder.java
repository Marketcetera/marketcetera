package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.*;
import org.marketcetera.event.beans.DividendBean;
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
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public abstract class DividendEventBuilder
        implements EventBuilder<DividendEvent>
{
    /**
     * Returns a <code>DividendEventBuilder</code> suitable for constructing a new <code>DividendEvent</code> object.
     *
     * @return a <code>DividendEventBuilder</code> value
     */
    public static DividendEventBuilder dividend()
    {
        return new DividendEventBuilder(){
            @Override
            public DividendEvent create()
            {
                return new DividendEventImpl(getDividend());
            }
        };
    }
    /**
     * Sets the message id to use with the new event. 
     *
     * @param inMessageId a <code>long</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withMessageId(long inMessageId)
    {
        dividend.setMessageId(inMessageId);
        return this;
    }
    /**
     * Sets the timestamp value to use with the new event.
     *
     * @param inTimestamp a <code>Date</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withTimestamp(Date inTimestamp)
    {
        dividend.setTimestamp(inTimestamp);
        return this;
    }
    /**
     * Sets the source value to use with the new event.
     *
     * @param inSource an <code>Object</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public DividendEventBuilder withSource(Object inSource)
    {
        dividend.setSource(inSource);
        return this;
    }
    /**
     * Sets the event type.
     *
     * @param inEventType an <code>EventType</code> value
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withEventType(EventType inEventType)
    {
        dividend.setEventType(inEventType);
        return this;
    }
    /**
     * Sets the equity value.
     *
     * @param inEquity an <code>Equity</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withEquity(Equity inEquity)
    {
        dividend.setEquity(inEquity);
        return this;
    }
    /**
     * Sets the amount value.
     *
     * @param inAmount a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withAmount(BigDecimal inAmount)
    {
        dividend.setAmount(inAmount);
        return this;
    }
    /**
     * Sets the currency value.
     *
     * @param inCurrency a <code>String</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withCurrency(String inCurrency)
    {
        dividend.setCurrency(inCurrency);
        return this;
    }
    /**
     * Sets the declareDate value.
     *
     * @param inDeclareDate a <code>String</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withDeclareDate(String inDeclareDate)
    {
        dividend.setDeclareDate(inDeclareDate);
        return this;
    }
    /**
     * Sets the executionDate value.
     *
     * @param inExecutionDate a <code>String</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withExecutionDate(String inExecutionDate)
    {
        dividend.setExecutionDate(inExecutionDate);
        return this;
    }
    /**
     * Sets the paymentDate value.
     *
     * @param inPaymentDate a <code>String</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withPaymentDate(String inPaymentDate)
    {
        dividend.setPaymentDate(inPaymentDate);
        return this;
    }
    /**
     * Sets the recordDate value.
     *
     * @param inRecordDate a <code>String</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withRecordDate(String inRecordDate)
    {
        dividend.setRecordDate(inRecordDate);
        return this;
    }
    /**
     * Sets the dividendFrequency value.
     *
     * @param inDividendFrequency a <code>DividendFrequency</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withFrequency(DividendFrequency inDividendFrequency)
    {
        dividend.setFrequency(inDividendFrequency);
        return this;
    }
    /**
     * Sets the dividendStatus value.
     *
     * @param inDividendStatus a <code>DividendStatus</code> value or <code>null</code>
     * @return a <code>DividendEventBuilder</code> value
     */
    public final DividendEventBuilder withStatus(DividendStatus inDividendStatus)
    {
        dividend.setStatus(inDividendStatus);
        return this;
    }
    /**
     * Sets the dividendType value.
     *
     * @param inDividendType a <code>DividendType</code> value or <code>null</code>
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
        return String.format("DividendEventBuilder [dividend=%s]", //$NON-NLS-1$
                             dividend);
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
