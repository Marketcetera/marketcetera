package org.marketcetera.event.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.Messages;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.util.EventValidationServices;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link OptionEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public final class OptionBean
        implements Serializable, Messages
{
    /**
     * Gets the instrument.
     *
     * @return an <code>Option</code> value
     */
    public Option getInstrument()
    {
        return (Option)instrument.getInstrument();
    }
    /**
     * Sets the instrument.
     *
     * @param inOption an <code>Option</code> value
     */
    public void setInstrument(Option inOption)
    {
        instrument.setInstrument(inOption);
    }
    /**
     * Get the underlyingEquity value.
     *
     * @return a <code>Equity</code> value
     */
    public Equity getUnderlyingEquity()
    {
        return underlyingEquity;
    }
    /**
     * Sets the underlyingEquity value.
     *
     * @param a <code>Equity</code> value
     */
    public void setUnderlyingEquity(Equity inUnderlyingEquity)
    {
        underlyingEquity = inUnderlyingEquity;
    }
    /**
     * Get the expiry value.
     *
     * @return a <code>String</code> value
     */
    public String getExpiry()
    {
        return expiry;
    }
    /**
     * Sets the expiry value.
     *
     * @param a <code>String</code> value
     */
    public void setExpiry(String inExpiry)
    {
        expiry = inExpiry;
    }
    /**
     * Get the strike value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getStrike()
    {
        return strike;
    }
    /**
     * Sets the strike value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setStrike(BigDecimal inStrike)
    {
        strike = inStrike;
    }
    /**
     * Get the optionType value.
     *
     * @return a <code>OptionType</code> value
     */
    public OptionType getOptionType()
    {
        return optionType;
    }
    /**
     * Sets the optionType value.
     *
     * @param a <code>OptionType</code> value
     */
    public void setOptionType(OptionType inOptionType)
    {
        optionType = inOptionType;
    }
    /**
     * Get the expirationType value.
     *
     * @return a <code>ExpirationType</code> value
     */
    public ExpirationType getExpirationType()
    {
        return expirationType;
    }
    /**
     * Sets the expirationType value.
     *
     * @param a <code>ExpirationType</code> value
     */
    public void setExpirationType(ExpirationType inExpirationType)
    {
        expirationType = inExpirationType;
    }
    /**
     * Get the multiplier value.
     *
     * @return a <code>int</code> value
     */
    public int getMultiplier()
    {
        return multiplier;
    }
    /**
     * Sets the multiplier value.
     *
     * @param a <code>int</code> value
     */
    public void setMultiplier(int inMultiplier)
    {
        multiplier = inMultiplier;
    }
    /**
     * Get the hasDeliverable value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean hasDeliverable()
    {
        return hasDeliverable;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param a <code>boolean</code> value
     */
    public void setHasDeliverable(boolean inHasDeliverable)
    {
        hasDeliverable = inHasDeliverable;
    }
    /**
     * Performs validation of the attributes.
     *
     * @throws IllegalArgumentException if {@link #instrument} is <code>null</code>
     * @throws IllegalArgumentException if {@link #underlyingEquity} is <code>null</code>
     * @throws IllegalArgumentException if {@link #expiry} is <code>null</code>
     * @throws IllegalArgumentException if {@link #strike} is <code>null</code>
     * @throws IllegalArgumentException if {@link #optionType} is <code>null</code>
     * @throws IllegalArgumentException if {@link #expirationType} is <code>null</code>
     */
    public void validate()
    {
        instrument.validate();
        if(underlyingEquity == null) {
            EventValidationServices.error(VALIDATION_NULL_UNDERLYING_EQUITY);
        }
        if(expiry == null ||
           expiry.isEmpty()) {
            EventValidationServices.error(VALIDATION_NULL_EXPIRY);
        }
        if(strike == null) {
            EventValidationServices.error(VALIDATION_NULL_STRIKE);
        }
        if(optionType == null) {
            EventValidationServices.error(VALIDATION_NULL_OPTION_TYPE);
        }
        if(expirationType == null) {
            EventValidationServices.error(VALIDATION_NULL_EXPIRATION_TYPE);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expirationType == null) ? 0 : expirationType.hashCode());
        result = prime * result + ((expiry == null) ? 0 : expiry.hashCode());
        result = prime * result + (hasDeliverable ? 1231 : 1237);
        result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
        result = prime * result + multiplier;
        result = prime * result + ((optionType == null) ? 0 : optionType.hashCode());
        result = prime * result + ((strike == null) ? 0 : strike.hashCode());
        result = prime * result + ((underlyingEquity == null) ? 0 : underlyingEquity.hashCode());
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
        OptionBean other = (OptionBean) obj;
        if (expirationType == null) {
            if (other.expirationType != null)
                return false;
        } else if (!expirationType.equals(other.expirationType))
            return false;
        if (expiry == null) {
            if (other.expiry != null)
                return false;
        } else if (!expiry.equals(other.expiry))
            return false;
        if (hasDeliverable != other.hasDeliverable)
            return false;
        if (instrument == null) {
            if (other.instrument != null)
                return false;
        } else if (!instrument.equals(other.instrument))
            return false;
        if (multiplier != other.multiplier)
            return false;
        if (optionType == null) {
            if (other.optionType != null)
                return false;
        } else if (!optionType.equals(other.optionType))
            return false;
        if (strike == null) {
            if (other.strike != null)
                return false;
        } else if (!strike.equals(other.strike))
            return false;
        if (underlyingEquity == null) {
            if (other.underlyingEquity != null)
                return false;
        } else if (!underlyingEquity.equals(other.underlyingEquity))
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
        builder.append("OptionBean [expirationType=").append(expirationType).append(", expiry=").append(expiry) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", hasDeliverable=").append(hasDeliverable).append(", instrument=").append(instrument) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", multiplier=").append(multiplier).append(", optionType=").append(optionType) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", strike=").append(strike).append(", underlyingEquity=").append(underlyingEquity).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }
    /**
     * the underlying equity for the option
     */
    private Equity underlyingEquity;
    /**
     * the expiry for the option - format is dependent on the market data provider
     */
    private String expiry;
    /**
     * the strike of the option
     */
    private BigDecimal strike;
    /**
     * the type of the option
     */
    private OptionType optionType;
    /**
     * the expiration type of the option
     */
    private ExpirationType expirationType;
    /**
     * the multiplier of the option
     */
    private int multiplier;
    /**
     * indicates if the option includes deliverables
     */
    private boolean hasDeliverable;
    /**
     * the instrument of the option
     */
    private final InstrumentBean instrument = new InstrumentBean();
    private final static long serialVersionUID = 1L;
}
