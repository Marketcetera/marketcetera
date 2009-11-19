package org.marketcetera.options;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * A test custom expiry normalizer that always appends 01 to an expiry
 * of length 6.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class CustomExpiryNormalizer implements OptionExpiryNormalizer {
    @Override
    public String normalizeEquityOptionExpiry(String inExpiry) {
        if(inExpiry.length() == 6) {
            return inExpiry + "01";
        }
        return inExpiry;
    }
}
