package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.DividendFrequency;
import org.marketcetera.event.DividendStatus;
import org.marketcetera.event.DividendType;
import org.marketcetera.event.beans.DividendBean;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.event.util.EventServices;
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
        implements DividendEvent, HasEventBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.HasEventBean#getEventBean()
     */
    @Override
    public EventBean getEventBean()
    {
        return dividend;
    }
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
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return dividend.getInstrumentAsString();
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
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        return EventServices.eventHashCode(this);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj)
    {
        return EventServices.eventEquals(this,
                                         obj);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s", //$NON-NLS-1$
                             dividend);
    }
    /**
     * Create a new DividendEventImpl instance.
     *
     * @param inDividend a <code>DividendBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Equity</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Amount</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Currency</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>DeclareDate</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExecutionDate</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>PaymentDate</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>RecordDate</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>Frequency</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Status</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Type</code> is <code>null</code>
     */
    DividendEventImpl(DividendBean inDividend)
    {
        dividend = DividendBean.copy(inDividend);
        dividend.setDefaults();
        dividend.validate();
    }
    /**
     * dividend attributes
     */
    private final DividendBean dividend;
    private static final long serialVersionUID = 1L;
}
