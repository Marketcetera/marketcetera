package org.marketcetera.api.systemmodel.instruments;

import java.math.BigDecimal;

/* $License$ */

/**
 * Identifies an option.
 * <p>
 * Note that if the option expiry specifies the expiry date in the format
 * YYYYMM, an augmented form of the expiry is also stored as an attribute
 * of the option. The {@link #getAugmentedExpiry() augmented expiry} is used
 * for {@link #hashCode()} and {@link #equals(Object)} methods in preference
 * to the specified {@link #getExpiry() expiry}.
 * <p>
 * See {@link org.marketcetera.core.options.OptionUtils#normalizeEquityOptionExpiry(String)} for details
 * on how the expiry value is augmented. 
 * 
 * @version $Id$
 * @since 2.0.0
 */
public interface Option
        extends Instrument
{
    /**
     * Returns the option expiry.
     * 
     * @return the option expiry, never null
     */
    public String getExpiry();
    /**
     * Returns the augmented option expiry.
     * <p>
     * The augmented expiry is available if the supplied option
     * expiry doesn't include the expiry day, otherwise it is null.
     *
     * @return the augmented expiry, if the expiry was augmented, null otherwise.
     * 
     * @see OptionUtils#normalizeEquityOptionExpiry(String) 
     */
    public String getAugmentedExpiry();
    /**
     * Returns the option strike price.
     * 
     * @return the option strike price, never null
     */
    public BigDecimal getStrikePrice();
    /**
     * Returns the option type.
     * 
     * @return the option type, never null
     */
    public OptionType getType();
}
