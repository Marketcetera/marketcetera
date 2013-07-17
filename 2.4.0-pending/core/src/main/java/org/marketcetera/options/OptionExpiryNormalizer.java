package org.marketcetera.options;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Normalizes the option expiry strings that lack expiration day.
 * <p>
 * This interface should be implemented to customize the standard option
 * expiry normalization carried out by
 * {@link OptionUtils#normalizeUSEquityOptionExpiry(String)}. 
 * <p>
 * This class is used by {@link OptionUtils#normalizeEquityOptionExpiry(String)}
 * to enable customization of option expiry normalization.
 * <p>
 * To provide a custom expiry normalizer, create a class that implements
 * this interface. Package it in a jar that has a file
 * {@code META-INF/services/org.marketcetera.options.OptionExpiryNormalizer}
 * that contains the absolute class name of that class. And place that jar
 * in the lib directory for every platform application. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface OptionExpiryNormalizer {
    /**
     * Normalizes the supplied expiry date with a day if it doesn't
     * include the day, ie. if it's in YYYYMM format. If the supplied
     * expiry doesn't need to be normalized or cannot be normalized, a
     * null value is returned back.
     * <p>
     *
     * @param inExpiry the option expiry string.
     *
     * @return the expiry in YYYYMMDD, if the supplied expiry was normalized,
     * null if it wasn't.
     */
    public String normalizeEquityOptionExpiry(String inExpiry);
}
