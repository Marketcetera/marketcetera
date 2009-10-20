package org.marketcetera.event.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.util.DividendFrequency;
import org.marketcetera.event.util.DividendStatus;
import org.marketcetera.event.util.DividendType;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Sets the attributes necessary for a {@link DividendEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public final class DividendBean
        implements Serializable
{
    /**
     * Get the equity value.
     *
     * @return a <code>Equity</code> value
     */
    public final Equity getEquity()
    {
        return equity;
    }
    /**
     * Sets the equity value.
     *
     * @param a <code>Equity</code> value
     */
    public final void setEquity(Equity inEquity)
    {
        equity = inEquity;
    }
    /**
     * Get the amount value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getAmount()
    {
        return amount;
    }
    /**
     * Sets the amount value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setAmount(BigDecimal inAmount)
    {
        amount = inAmount;
    }
    /**
     * Get the currency value.
     *
     * @return a <code>String</code> value
     */
    public final String getCurrency()
    {
        return currency;
    }
    /**
     * Sets the currency value.
     *
     * @param a <code>String</code> value
     */
    public final void setCurrency(String inCurrency)
    {
        currency = inCurrency;
    }
    /**
     * Get the declareDate value.
     *
     * @return a <code>String</code> value
     */
    public final String getDeclareDate()
    {
        return declareDate;
    }
    /**
     * Sets the declareDate value.
     *
     * @param a <code>String</code> value
     */
    public final void setDeclareDate(String inDeclareDate)
    {
        declareDate = inDeclareDate;
    }
    /**
     * Get the executionDate value.
     *
     * @return a <code>String</code> value
     */
    public final String getExecutionDate()
    {
        return executionDate;
    }
    /**
     * Sets the executionDate value.
     *
     * @param a <code>String</code> value
     */
    public final void setExecutionDate(String inExecutionDate)
    {
        executionDate = inExecutionDate;
    }
    /**
     * Get the paymentDate value.
     *
     * @return a <code>String</code> value
     */
    public final String getPaymentDate()
    {
        return paymentDate;
    }
    /**
     * Sets the paymentDate value.
     *
     * @param a <code>String</code> value
     */
    public final void setPaymentDate(String inPaymentDate)
    {
        paymentDate = inPaymentDate;
    }
    /**
     * Get the recordDate value.
     *
     * @return a <code>String</code> value
     */
    public final String getRecordDate()
    {
        return recordDate;
    }
    /**
     * Sets the recordDate value.
     *
     * @param a <code>String</code> value
     */
    public final void setRecordDate(String inRecordDate)
    {
        recordDate = inRecordDate;
    }
    /**
     * Get the frequency value.
     *
     * @return a <code>DividendFrequency</code> value
     */
    public final DividendFrequency getFrequency()
    {
        return frequency;
    }
    /**
     * Sets the frequency value.
     *
     * @param a <code>DividendFrequency</code> value
     */
    public final void setFrequency(DividendFrequency inFrequency)
    {
        frequency = inFrequency;
    }
    /**
     * Get the status value.
     *
     * @return a <code>DividendStatus</code> value
     */
    public final DividendStatus getStatus()
    {
        return status;
    }
    /**
     * Sets the status value.
     *
     * @param a <code>DividendStatus</code> value
     */
    public final void setStatus(DividendStatus inStatus)
    {
        status = inStatus;
    }
    /**
     * Get the type value.
     *
     * @return a <code>DividendType</code> value
     */
    public final DividendType getType()
    {
        return type;
    }
    /**
     * Sets the type value.
     *
     * @param a <code>DividendType</code> value
     */
    public final void setType(DividendType inType)
    {
        type = inType;
    }
    /**
     * the equity for which the dividend was or will be issued 
     */
    private volatile Equity equity;
    /**
     * the amount in which the dividend was or will be issued 
     */
    private volatile BigDecimal amount;
    /**
     * the currency in which the dividend was or will be issued 
     */
    private volatile String currency;
    /**
     * the declare date on which the dividend was or will be issued - the format is dependent on the market data provider 
     */
    private volatile String declareDate;
    /**
     * the execution date on which the dividend was or will be issued - the format is dependent on the market data provider 
     */
    private volatile String executionDate;
    /**
     * the payment date on which the dividend was or will be issued - the format is dependent on the market data provider 
     */
    private volatile String paymentDate;
    /**
     * the record date on which the dividend was or will be issued - the format is dependent on the market data provider 
     */
    private volatile String recordDate;
    /**
     * the frequency in which the dividend was or will be issued 
     */
    private volatile DividendFrequency frequency;
    /**
     * the status of the dividend that was or will be issued 
     */
    private volatile DividendStatus status;
    /**
     * the type of the dividend that was or will be issued 
     */
    private volatile DividendType type;
    private static final long serialVersionUID = 1L;
}
