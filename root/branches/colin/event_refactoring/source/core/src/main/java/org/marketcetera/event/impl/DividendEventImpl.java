package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.beans.DividendBean;
import org.marketcetera.event.util.DividendFrequency;
import org.marketcetera.event.util.DividendStatus;
import org.marketcetera.event.util.DividendType;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation of {@link DividendEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
final class DividendEventImpl
        implements DividendEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return dividend.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return dividend.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return dividend.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        dividend.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return dividend.getTimeMillis();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getAmount()
     */
    @Override
    public BigDecimal getAmount()
    {
        return dividend.getAmount();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getCurrency()
     */
    @Override
    public String getCurrency()
    {
        return dividend.getCurrency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getDeclareDate()
     */
    @Override
    public String getDeclareDate()
    {
        return dividend.getDeclareDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getEquity()
     */
    @Override
    public Equity getEquity()
    {
        return dividend.getEquity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Equity getInstrument()
    {
        return getEquity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getExecutionDate()
     */
    @Override
    public String getExecutionDate()
    {
        return dividend.getExecutionDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getFrequency()
     */
    @Override
    public DividendFrequency getFrequency()
    {
        return dividend.getFrequency();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getPaymentDate()
     */
    @Override
    public String getPaymentDate()
    {
        return dividend.getPaymentDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getRecordDate()
     */
    @Override
    public String getRecordDate()
    {
        return dividend.getRecordDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getStatus()
     */
    @Override
    public DividendStatus getStatus()
    {
        return dividend.getStatus();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getType()
     */
    @Override
    public DividendType getType()
    {
        return dividend.getType();
    }
    /**
     * Create a new DividendEventImpl instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inEquity an <code>Equity</code> value
     * @param inAmount a <code>BigDecimal</code> value
     * @param inCurrency a <code>String</code> value
     * @param inDeclareDate a <code>String</code> value
     * @param inExecutionDate a <code>String</code> value
     * @param inPaymentDate a <code>String</code> value
     * @param inRecordDate a <code>String</code> value
     * @param inFrequency a <code>DividendFrequency</code> value
     * @param inStatus a <code>DividendStatus</code> value
     * @param inType a <code>DividendType</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inEquity</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inAmount</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inCurrency</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inDeclareDate</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inExecutionDate</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inPaymentDate</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inRecordDate</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inFrequency</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inStatus</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inType</code> is <code>null</code>
     */
    DividendEventImpl(long inMessageId,
                      Date inTimestamp,
                      Equity inEquity,
                      BigDecimal inAmount,
                      String inCurrency,
                      String inDeclareDate,
                      String inExecutionDate,
                      String inPaymentDate,
                      String inRecordDate,
                      DividendFrequency inFrequency,
                      DividendStatus inStatus,
                      DividendType inType)
    {
        dividend.setMessageId(inMessageId);
        dividend.setTimestamp(inTimestamp);
        dividend.setEquity(inEquity);
        dividend.setAmount(inAmount);
        dividend.setCurrency(inCurrency);
        dividend.setDeclareDate(inDeclareDate);
        dividend.setPaymentDate(inPaymentDate);
        dividend.setExecutionDate(inExecutionDate);
        dividend.setRecordDate(inRecordDate);
        dividend.setFrequency(inFrequency);
        dividend.setStatus(inStatus);
        dividend.setType(inType);
        dividend.setDefaults();
        dividend.validate();
    }
    /**
     * dividend attributes
     */
    private final DividendBean dividend = new DividendBean();
    private static final long serialVersionUID = 1L;
}
