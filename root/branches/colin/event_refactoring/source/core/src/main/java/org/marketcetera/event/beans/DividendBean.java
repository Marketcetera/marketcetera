package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.util.DividendFrequency;
import org.marketcetera.event.util.DividendStatus;
import org.marketcetera.event.util.DividendType;
import org.marketcetera.event.util.EventValidationServices;
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
@NotThreadSafe
@ClassVersion("$Id$")
public final class DividendBean
        extends EventBean
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
     * Performs validation of the attributes.
     *
     * <p>Subclasses should override this method to validate
     * their attributes and invoke the parent method.
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
    @Override
    public void validate()
    {
        super.validate();
        if(equity == null) {
            EventValidationServices.error(VALIDATION_NULL_EQUITY);
        }
        if(amount == null) {
            EventValidationServices.error(VALIDATION_NULL_AMOUNT);
        }
        if(currency == null ||
           currency.isEmpty()) {
            EventValidationServices.error(VALIDATION_NULL_CURRENCY);
        }
        if(declareDate == null ||
           declareDate.isEmpty()) {
            EventValidationServices.error(VALIDATION_NULL_DECLARE_DATE);
        }
        if(executionDate == null ||
           executionDate.isEmpty()) {
            EventValidationServices.error(VALIDATION_NULL_EXECUTION_DATE);
        }
        if(paymentDate == null ||
           paymentDate.isEmpty()) {
            EventValidationServices.error(VALIDATION_NULL_PAYMENT_DATE);
        }
        if(recordDate == null ||
           recordDate.isEmpty()) {
            EventValidationServices.error(VALIDATION_NULL_RECORD_DATE);
        }
        if(frequency == null) {
            EventValidationServices.error(VALIDATION_NULL_FREQUENCY);
        }
        if(status == null) {
            EventValidationServices.error(VALIDATION_NULL_STATUS);
        }
        if(type == null) {
            EventValidationServices.error(VALIDATION_NULL_TYPE);
        }
    }
    /**
     * the equity for which the dividend was or will be issued 
     */
    private Equity equity;
    /**
     * the amount in which the dividend was or will be issued 
     */
    private BigDecimal amount;
    /**
     * the currency in which the dividend was or will be issued 
     */
    private String currency;
    /**
     * the declare date on which the dividend was or will be issued - the format is dependent on the market data provider 
     */
    private String declareDate;
    /**
     * the execution date on which the dividend was or will be issued - the format is dependent on the market data provider 
     */
    private String executionDate;
    /**
     * the payment date on which the dividend was or will be issued - the format is dependent on the market data provider 
     */
    private String paymentDate;
    /**
     * the record date on which the dividend was or will be issued - the format is dependent on the market data provider 
     */
    private String recordDate;
    /**
     * the frequency in which the dividend was or will be issued 
     */
    private DividendFrequency frequency;
    /**
     * the status of the dividend that was or will be issued 
     */
    private DividendStatus status;
    /**
     * the type of the dividend that was or will be issued 
     */
    private DividendType type;
    private static final long serialVersionUID = 1L;
}
