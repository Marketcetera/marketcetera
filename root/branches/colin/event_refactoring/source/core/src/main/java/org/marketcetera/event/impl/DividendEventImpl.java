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
        extends EventImpl
        implements DividendEvent
{
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
     * @throws IllegalArgumentException if {@link #getTimestamp()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getMessageId()} &lt; 0
     * @throws IllegalArgumentException if {@link #equity} is <code>null</code>
     * @throws IllegalArgumentException if {@link #amount} is <code>null</code>
     * @throws IllegalArgumentException if {@link #currency} is <code>null</code>
     * @throws IllegalArgumentException if {@link #declareDate} is <code>null</code>
     * @throws IllegalArgumentException if {@link #executionDate} is <code>null</code>
     * @throws IllegalArgumentException if {@link #paymentDate} is <code>null</code>
     * @throws IllegalArgumentException if {@link #recordDate} is <code>null</code>
     * @throws IllegalArgumentException if {@link #frequency} is <code>null</code>
     * @throws IllegalArgumentException if {@link #status} is <code>null</code>
     * @throws IllegalArgumentException if {@link #type} is <code>null</code>
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
        super(inMessageId,
              inTimestamp);
        dividend.setAmount(inAmount);
        dividend.setCurrency(inCurrency);
        dividend.setDeclareDate(inDeclareDate);
        dividend.setEquity(inEquity);
        dividend.setExecutionDate(inExecutionDate);
        dividend.setFrequency(inFrequency);
        dividend.setPaymentDate(inPaymentDate);
        dividend.setRecordDate(inRecordDate);
        dividend.setStatus(inStatus);
        dividend.setType(inType);
        dividend.validate();
    }
    /**
     * dividend attributes
     */
    private final DividendBean dividend = new DividendBean();
    private static final long serialVersionUID = 1L;
}
