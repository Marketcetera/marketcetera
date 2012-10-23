package org.marketcetera.core.options;

/* $License$ */
/**
 * A test custom expiry normalizer that always appends 01 to an expiry
 * of length 6.
 *
 * @version $Id: CustomExpiryNormalizer.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class CustomExpiryNormalizer implements OptionExpiryNormalizer {
    @Override
    public String normalizeEquityOptionExpiry(String inExpiry) {
        if(inExpiry.length() == 6) {
            return inExpiry + "01";
        }
        return inExpiry;
    }
}
