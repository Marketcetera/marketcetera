package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.DividendFrequency;
import org.marketcetera.event.DividendStatus;
import org.marketcetera.event.DividendType;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.log.I18NBoundMessage1P;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
final class DividendEventImpl
        implements DividendEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getAmount()
     */
    @Override
    public BigDecimal getAmount()
    {
        return amount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getCurrency()
     */
    @Override
    public String getCurrency()
    {
        return currency;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getDeclareDate()
     */
    @Override
    public String getDeclareDate()
    {
        return declareDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getEquity()
     */
    @Override
    public Equity getEquity()
    {
        return equity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getInstrument()
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
        return executionDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getFrequency()
     */
    @Override
    public DividendFrequency getFrequency()
    {
        return frequency;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getPaymentDate()
     */
    @Override
    public String getPaymentDate()
    {
        return paymentDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getRecordDate()
     */
    @Override
    public String getRecordDate()
    {
        return recordDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getStatus()
     */
    @Override
    public DividendStatus getStatus()
    {
        return status;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DividendEvent#getType()
     */
    @Override
    public DividendType getType()
    {
        return type;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return event.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return event.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return event.getTimeMillis();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return event.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        event.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DividendEventImpl other = (DividendEventImpl) obj;
        if (event == null) {
            if (other.event != null)
                return false;
        } else if (!event.equals(other.event))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DividendEventImpl [amount=");
        builder.append(amount);
        builder.append(", currency=");
        builder.append(currency);
        builder.append(", declareDate=");
        builder.append(declareDate);
        builder.append(", event=");
        builder.append(event);
        builder.append(", executionDate=");
        builder.append(executionDate);
        builder.append(", frequency=");
        builder.append(frequency);
        builder.append(", paymentDate=");
        builder.append(paymentDate);
        builder.append(", recordDate=");
        builder.append(recordDate);
        builder.append(", status=");
        builder.append(status);
        builder.append(", type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }
    /**
     * Create a new DividendEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inEquity
     * @param inAmount
     * @param inCurrency
     * @param inDeclareDate
     * @param inExecutionDate
     * @param inPaymentDate
     * @param inRecordDate
     * @param inDividendFrequency
     * @param inDividendStatus
     * @param inDividendType
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
                      DividendFrequency inDividendFrequency,
                      DividendStatus inDividendStatus,
                      DividendType inDividendType)
    {
        event = new EventImpl(inMessageId,
                              inTimestamp);
        equity = inEquity;
        amount = inAmount;
        currency = inCurrency;
        declareDate = inDeclareDate;
        executionDate = inExecutionDate;
        paymentDate = inPaymentDate;
        recordDate = inRecordDate;
        frequency = inDividendFrequency;
        status = inDividendStatus;
        type = inDividendType;
        validate();
    }
    /**
     * 
     *
     *
     * @throws EventValidationException
     */
    void validate()
    {
        event.validate();
        // TODO null equity
        if(amount == null) {
            EventValidationException.error(VALIDATION_NULL_AMOUNT);
        }
        if(currency == null ||
           currency.isEmpty()) {
            EventValidationException.error(VALIDATION_NULL_CURRENCY);
        }
        if(declareDate == null ||
           declareDate.isEmpty()) {
            EventValidationException.error(VALIDATION_NULL_DECLARE_DATE);
        }
        EventValidationServices.validateDate(declareDate,
                                             new I18NBoundMessage1P(VALIDATION_FORMAT_DECLARE_DATE,
                                                                    declareDate));
        if(executionDate == null ||
           executionDate.isEmpty()) {
            EventValidationException.error(VALIDATION_NULL_EXECUTION_DATE);
        }
        EventValidationServices.validateDate(declareDate,
                                             new I18NBoundMessage1P(VALIDATION_FORMAT_EXECUTION_DATE,
                                                                    executionDate));
        if(paymentDate == null ||
           paymentDate.isEmpty()) {
            EventValidationException.error(VALIDATION_NULL_PAYMENT_DATE);
        }
        EventValidationServices.validateDate(paymentDate,
                                             new I18NBoundMessage1P(VALIDATION_FORMAT_PAYMENT_DATE,
                                                                    paymentDate));
        if(recordDate == null ||
           recordDate.isEmpty()) {
            EventValidationException.error(VALIDATION_NULL_RECORD_DATE);
        }
        EventValidationServices.validateDate(recordDate,
                                             new I18NBoundMessage1P(VALIDATION_FORMAT_RECORD_DATE,
                                                                    paymentDate));
        if(frequency == null) {
            EventValidationException.error(VALIDATION_NULL_FREQUENCY);
        }
        if(status == null) {
            EventValidationException.error(VALIDATION_NULL_STATUS);
        }
        if(type == null) {
            EventValidationException.error(VALIDATION_NULL_TYPE);
        }
    }
    /**
     * 
     */
    private final EventImpl event;
    /**
     * 
     */
    private final Equity equity;
    /**
     * 
     */
    private final BigDecimal amount;
    /**
     * 
     */
    private final String currency;
    /**
     * 
     */
    private final String declareDate;
    /**
     * 
     */
    private final String executionDate;
    /**
     * 
     */
    private final String paymentDate;
    /**
     * 
     */
    private final String recordDate;
    /**
     * 
     */
    private final DividendFrequency frequency;
    /**
     * 
     */
    private final DividendStatus status;
    /**
     * 
     */
    private final DividendType type;
    private static final long serialVersionUID = 1L;
}
