package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.*;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Sets the attributes necessary for a {@link DividendEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public final class DividendBean
        extends EventBean
{
    /**
     * Creates a shallow copy of the given <code>DividendBean</code>.
     *
     * @param inBean a <code>DividendBean</code> value
     * @return a <code>DividendBean</code> value
     */
    public static DividendBean copy(DividendBean inBean)
    {
        DividendBean newBean = new DividendBean();
        DividendBean.copyAttributes(inBean,
                                    newBean);
        return newBean;
    }
    /**
     * Get the equity value.
     *
     * @return a <code>Equity</code> value
     */
    public Equity getEquity()
    {
        return equity;
    }
    /**
     * Gets the instrument value as a <code>String</code>.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getInstrumentAsString()
    {
        if(equity == null) {
            return null;
        }
        return equity.getSymbol();
    }
    /**
     * Sets the equity value.
     *
     * @param inEquity an <code>Equity</code> value
     */
    public void setEquity(Equity inEquity)
    {
        equity = inEquity;
    }
    /**
     * Returns the type of the event.
     *
     * @return an <code>EventType</code> value
     */
    public final EventType getEventType()
    {
        return eventType;
    }
    /**
     * Sets the type of the event.
     *
     * @param inEventType
     */
    public final void setEventType(EventType inEventType)
    {
        eventType = inEventType;
    }
    /**
     * Get the amount value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAmount()
    {
        return amount;
    }
    /**
     * Sets the amount value.
     *
     * @param inAmount a <code>BigDecimal</code> value
     */
    public void setAmount(BigDecimal inAmount)
    {
        amount = inAmount;
    }
    /**
     * Get the currency value.
     *
     * @return a <code>String</code> value
     */
    public String getCurrency()
    {
        return currency;
    }
    /**
     * Sets the currency value.
     *
     * @param inCurrency a <code>String</code> value
     */
    public void setCurrency(String inCurrency)
    {
        currency = inCurrency;
    }
    /**
     * Get the declareDate value.
     *
     * @return a <code>String</code> value
     */
    public String getDeclareDate()
    {
        return declareDate;
    }
    /**
     * Sets the declareDate value.
     *
     * @param inDeclareDate a <code>String</code> value
     */
    public void setDeclareDate(String inDeclareDate)
    {
        declareDate = inDeclareDate;
    }
    /**
     * Get the executionDate value.
     *
     * @return a <code>String</code> value
     */
    public String getExecutionDate()
    {
        return executionDate;
    }
    /**
     * Sets the executionDate value.
     *
     * @param inExecutionDate a <code>String</code> value
     */
    public void setExecutionDate(String inExecutionDate)
    {
        executionDate = inExecutionDate;
    }
    /**
     * Get the paymentDate value.
     *
     * @return a <code>String</code> value
     */
    public String getPaymentDate()
    {
        return paymentDate;
    }
    /**
     * Sets the paymentDate value.
     *
     * @param inPaymentDate a <code>String</code> value
     */
    public void setPaymentDate(String inPaymentDate)
    {
        paymentDate = inPaymentDate;
    }
    /**
     * Get the recordDate value.
     *
     * @return a <code>String</code> value
     */
    public String getRecordDate()
    {
        return recordDate;
    }
    /**
     * Sets the recordDate value.
     *
     * @param inRecordDate a <code>String</code> value
     */
    public void setRecordDate(String inRecordDate)
    {
        recordDate = inRecordDate;
    }
    /**
     * Get the frequency value.
     *
     * @return a <code>DividendFrequency</code> value
     */
    public DividendFrequency getFrequency()
    {
        return frequency;
    }
    /**
     * Sets the frequency value.
     *
     * @param inFrequency a <code>DividendFrequency</code> value
     */
    public void setFrequency(DividendFrequency inFrequency)
    {
        frequency = inFrequency;
    }
    /**
     * Get the status value.
     *
     * @return a <code>DividendStatus</code> value
     */
    public DividendStatus getStatus()
    {
        return status;
    }
    /**
     * Sets the status value.
     *
     * @param inStatus a <code>DividendStatus</code> value
     */
    public void setStatus(DividendStatus inStatus)
    {
        status = inStatus;
    }
    /**
     * Get the type value.
     *
     * @return a <code>DividendType</code> value
     */
    public DividendType getType()
    {
        return type;
    }
    /**
     * Sets the type value.
     *
     * @param inType a <code>DividendType</code> value
     */
    public void setType(DividendType inType)
    {
        type = inType;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((declareDate == null) ? 0 : declareDate.hashCode());
        result = prime * result + ((equity == null) ? 0 : equity.hashCode());
        result = prime * result + ((executionDate == null) ? 0 : executionDate.hashCode());
        result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
        result = prime * result + ((paymentDate == null) ? 0 : paymentDate.hashCode());
        result = prime * result + ((recordDate == null) ? 0 : recordDate.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof DividendBean)) {
            return false;
        }
        DividendBean other = (DividendBean) obj;
        if (amount == null) {
            if (other.amount != null) {
                return false;
            }
        } else if (!amount.equals(other.amount)) {
            return false;
        }
        if (currency == null) {
            if (other.currency != null) {
                return false;
            }
        } else if (!currency.equals(other.currency)) {
            return false;
        }
        if (declareDate == null) {
            if (other.declareDate != null) {
                return false;
            }
        } else if (!declareDate.equals(other.declareDate)) {
            return false;
        }
        if (equity == null) {
            if (other.equity != null) {
                return false;
            }
        } else if (!equity.equals(other.equity)) {
            return false;
        }
        if (executionDate == null) {
            if (other.executionDate != null) {
                return false;
            }
        } else if (!executionDate.equals(other.executionDate)) {
            return false;
        }
        if (frequency == null) {
            if (other.frequency != null) {
                return false;
            }
        } else if (!frequency.equals(other.frequency)) {
            return false;
        }
        if (paymentDate == null) {
            if (other.paymentDate != null) {
                return false;
            }
        } else if (!paymentDate.equals(other.paymentDate)) {
            return false;
        }
        if (recordDate == null) {
            if (other.recordDate != null) {
                return false;
            }
        } else if (!recordDate.equals(other.recordDate)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (eventType == null) {
            if (other.eventType != null) {
                return false;
            }
        } else if (!eventType.equals(other.eventType)) {
            return false;
        }
        return true;
    }
    /**
     * Performs validation of the attributes.
     *
     * <p>Subclasses should override this method to validate
     * their attributes and invoke the parent method.
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Equity</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Amount</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Currency</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExecutionDate</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>Frequency</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Status</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Type</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>MetaType</code> is <code>null</code>
     */
    @Override
    public void validate()
    {
        super.validate();
        if(equity == null) {
            EventServices.error(VALIDATION_NULL_EQUITY);
        }
        if(amount == null) {
            EventServices.error(VALIDATION_NULL_AMOUNT);
        }
        if(currency == null ||
           currency.isEmpty()) {
            EventServices.error(VALIDATION_NULL_CURRENCY);
        }
        if(executionDate == null ||
           executionDate.isEmpty()) {
            EventServices.error(VALIDATION_NULL_EXECUTION_DATE);
        }
        if(frequency == null) {
            EventServices.error(VALIDATION_NULL_FREQUENCY);
        }
        if(status == null) {
            EventServices.error(VALIDATION_NULL_STATUS);
        }
        if(type == null) {
            EventServices.error(VALIDATION_NULL_TYPE);
        }
        if(eventType == null) {
            EventServices.error(VALIDATION_NULL_META_TYPE);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s %s %s %s Dividend %s %s(%s) executionDate=%s declareDate=%s paymentDate=%s recordDate=%s [%s with source %s at %s]]", //$NON-NLS-1$
                             eventType,
                             type,
                             status,
                             frequency,
                             equity,
                             amount,
                             currency,
                             executionDate,
                             declareDate,
                             paymentDate,
                             recordDate,
                             getMessageId(),
                             getSource(),
                             getTimestamp());
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor a <code>DividendBean</code> value
     * @param inRecipient a <code>DividendBean</code> value
     */
    protected static void copyAttributes(DividendBean inDonor,
                                         DividendBean inRecipient)
    {
        EventBean.copyAttributes(inDonor,
                                 inRecipient);
        inRecipient.setEventType(inDonor.getEventType());
        inRecipient.setAmount(inDonor.getAmount());
        inRecipient.setCurrency(inDonor.getCurrency());
        inRecipient.setDeclareDate(inDonor.getDeclareDate());
        inRecipient.setEquity(inDonor.getEquity());
        inRecipient.setExecutionDate(inDonor.getExecutionDate());
        inRecipient.setFrequency(inDonor.getFrequency());
        inRecipient.setPaymentDate(inDonor.getPaymentDate());
        inRecipient.setRecordDate(inDonor.getRecordDate());
        inRecipient.setStatus(inDonor.getStatus());
        inRecipient.setType(inDonor.getType());
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
    /**
     * the event meta-type
     */
    private EventType eventType = EventType.UNKNOWN;
    private static final long serialVersionUID = 1L;
}
