package org.marketcetera.event.impl;

import java.math.BigDecimal;

import org.marketcetera.event.OptionEvent;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.MarketDataRequestException;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the attributes of an option contract. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
final class OptionEventImpl
        implements OptionEvent
{
    @Override
    public Option getOption()
    {
        return option;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Option getInstrument()
    {
        return getOption();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getExpirationType()
     */
    @Override
    public ExpirationType getExpirationType()
    {
        return expirationType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getExpiry()
     */
    @Override
    public String getExpiry()
    {
        return expiry;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getMultiplier()
     */
    @Override
    public int getMultiplier()
    {
        return multiplier;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getOptionType()
     */
    @Override
    public OptionType getOptionType()
    {
        return optionType;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OptionEventImpl [expirationType=").append(expirationType).append(", hasDeliverable=") //$NON-NLS-1$
                .append(hasDeliverable).append(", expiry=").append(expiry).append(", multiplier=") //$NON-NLS-1$
                .append(multiplier).append(", optionType=").append(optionType).append(", strike=").append(strike) //$NON-NLS-1$
                .append(", symbol=").append(option).append(", underlyingSymbol=").append(underlyingEquity).append("]"); //$NON-NLS-1$
        return builder.toString();
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
        result = prime * result + (hasDeliverable ? 1231 : 1237);
        result = prime * result + ((expiry == null) ? 0 : expiry.hashCode());
        result = prime * result + multiplier;
        result = prime * result + ((optionType == null) ? 0 : optionType.hashCode());
        result = prime * result + ((strike == null) ? 0 : strike.hashCode());
        result = prime * result + ((option == null) ? 0 : option.hashCode());
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
        OptionEventImpl other = (OptionEventImpl) obj;
        if (expirationType == null) {
            if (other.expirationType != null)
                return false;
        } else if (!expirationType.equals(other.expirationType))
            return false;
        if (hasDeliverable != other.hasDeliverable)
            return false;
        if (expiry == null) {
            if (other.expiry != null)
                return false;
        } else if (!expiry.equals(other.expiry))
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
        if (option == null) {
            if (other.option != null)
                return false;
        } else if (!option.equals(other.option))
            return false;
        if (underlyingEquity == null) {
            if (other.underlyingEquity != null)
                return false;
        } else if (!underlyingEquity.equals(other.underlyingEquity))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getStrike()
     */
    @Override
    public BigDecimal getStrike()
    {
        return strike;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#hasDeliverable()
     */
    @Override
    public boolean hasDeliverable()
    {
        return hasDeliverable;
    }
    /**
     * Validates the given <code>String</code> as an option expiry value.
     *
     * @param inExpiry a <code>String</code> value
     * @throws IllegalArgumentException is the given value is not a valid expiry
     */
    private static void validateExpiry(String inExpiry)
    {
        try {
            DateUtils.stringToDate(inExpiry,
                                   DateUtils.DAYS);
        } catch (MarketDataRequestException e) {
            try {
                DateUtils.stringToDate(inExpiry,
                                       DateUtils.MONTHS);
            } catch (MarketDataRequestException e2) {
                throw new IllegalArgumentException(e2);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasUnderlyingEquity#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return underlyingEquity;
    }
    /**
     * Create a new OptionEventImpl instance.
     *
     * @param inOption an <code>Option</code> value containing the option symbol
     * @param inUnderlyingEquity an <code>Equity</code> value containing the underlying equity symbol
     * @param inStrike a <code>BigDecimal</code> value containing the option strike
     * @param inOptionType an <code>OptionType</code> value containing the option type
     * @param inExpiry a <code>String</code> value containing the option expiry in either "YYYYMM" or "YYYYMMDD" format
     * @param inHasDeliverable a <code>boolean</code> value indicating whether the option includes deliverables or not
     * @param inMultiplier an <code>int</code> value containing the option multiplier
     * @param inExpirationType an <code>ExpirationType</code> value containing the option expiration type
     * @throws IllegalArgumentException if <code>inExpiry</code> is not a valid maturity or is empty
     */
    OptionEventImpl(Option inOption,
                    Equity inUnderlyingEquity,
                    BigDecimal inStrike,
                    OptionType inOptionType,
                    String inExpiry,
                    boolean inHasDeliverable,
                    int inMultiplier,
                    ExpirationType inExpirationType)
    {
        if(inOption == null) {
            throw new NullPointerException();
        }
        if(inUnderlyingEquity == null) {
            throw new NullPointerException();
        }
        if(inStrike == null) {
            throw new NullPointerException();
        }
        if(inOptionType == null) {
            throw new NullPointerException();
        }
        if(inExpiry.isEmpty()) {
            throw new IllegalArgumentException();
        }
        validateExpiry(inExpiry);
        if(inExpirationType == null) {
            throw new NullPointerException();
        }
        option = inOption;
        underlyingEquity = inUnderlyingEquity;
        strike = inStrike;
        optionType = inOptionType;
        expiry = inExpiry;
        hasDeliverable = inHasDeliverable;
        multiplier = inMultiplier;
        expirationType = inExpirationType;
    }
    /**
     * the option instrument 
     */
    private final Option option;
    /**
     * the symbol of the underlying equity
     */
    private final Equity underlyingEquity;
    /**
     * the option type
     */
    private final OptionType optionType;
    /**
     * the option expiry
     */
    private final String expiry;
    /**
     * the option strike
     */
    private final BigDecimal strike;
    /**
     * indicates if the option includes a deliverable or not
     */
    private final boolean hasDeliverable;
    /**
     * the option expiration type
     */
    private final ExpirationType expirationType;
    /**
     * the option multiplier
     */
    private final int multiplier;
}
