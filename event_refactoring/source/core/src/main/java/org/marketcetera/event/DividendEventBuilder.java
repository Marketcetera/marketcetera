package org.marketcetera.event;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public class DividendEventBuilder
        extends EventBuilderImpl
        implements EventBuilder<DividendEvent>
{
    /**
     * Sets the equity value.
     *
     * @param a <code>Equity</code> value
     */
    public DividendEventBuilder withEquity(Equity inEquity)
    {
        equity = inEquity;
        return this;
    }
    /**
     * Sets the amount value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public DividendEventBuilder withAmount(BigDecimal inAmount)
    {
        amount = inAmount;
        return this;
    }
    /**
     * Sets the currency value.
     *
     * @param a <code>String</code> value
     */
    public DividendEventBuilder withCurrency(String inCurrency)
    {
        currency = inCurrency;
        return this;
    }
    /**
     * Sets the declareDate value.
     *
     * @param a <code>String</code> value
     */
    public DividendEventBuilder withDeclareDate(String inDeclareDate)
    {
        declareDate = inDeclareDate;
        return this;
    }
    /**
     * Sets the executionDate value.
     *
     * @param a <code>String</code> value
     */
    public DividendEventBuilder withExecutionDate(String inExecutionDate)
    {
        executionDate = inExecutionDate;
        return this;
    }
    /**
     * Sets the paymentDate value.
     *
     * @param a <code>String</code> value
     */
    public DividendEventBuilder withPaymentDate(String inPaymentDate)
    {
        paymentDate = inPaymentDate;
        return this;
    }
    /**
     * Sets the recordDate value.
     *
     * @param a <code>String</code> value
     */
    public DividendEventBuilder withRecordDate(String inRecordDate)
    {
        recordDate = inRecordDate;
        return this;
    }
    /**
     * Sets the dividendFrequency value.
     *
     * @param a <code>DividendFrequency</code> value
     */
    public DividendEventBuilder ofFrequency(DividendFrequency inDividendFrequency)
    {
        frequency = inDividendFrequency;
        return this;
    }
    /**
     * Sets the dividendStatus value.
     *
     * @param a <code>DividendStatus</code> value
     */
    public DividendEventBuilder ofStatus(DividendStatus inDividendStatus)
    {
        status = inDividendStatus;
        return this;
    }
    /**
     * Sets the dividendType value.
     *
     * @param a <code>DividendType</code> value
     */
    public DividendEventBuilder ofType(DividendType inDividendType)
    {
        type = inDividendType;
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.EventBuilder#create()
     */
    @Override
    public DividendEvent create()
    {
        setDefaults();
        return new DividendEventImpl(getMessageId(),
                                     getTimestamp(),
                                     equity,
                                     amount,
                                     currency,
                                     declareDate,
                                     executionDate,
                                     paymentDate,
                                     recordDate,
                                     frequency,
                                     status,
                                     type);
    }    
    /**
     * 
     *
     *
     */
    protected void setDefaults()
    {
        super.setDefaults();
    }
    /**
     * 
     */
    private Equity equity;
    /**
     * 
     */
    private BigDecimal amount;
    /**
     * 
     */
    private String currency;
    /**
     * 
     */
    private String declareDate;
    /**
     * 
     */
    private String executionDate;
    /**
     * 
     */
    private String paymentDate;
    /**
     * 
     */
    private String recordDate;
    /**
     * 
     */
    private DividendFrequency frequency;
    /**
     * 
     */
    private DividendStatus status;
    /**
     * 
     */
    private DividendType type;
}
