package org.marketcetera.options;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the expiration type of an option.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public enum ExpirationType
{
    /**
     * European expiration options may be exercised only at contract expiration
     */
    EUROPEAN,
    /**
     * American expiration options may be exercised at any point up to and including contract expiration
     */
    AMERICAN,
    /**
     * unknown expiration type
     */
    UNKNOWN;
    /**
     * Gets the <code>ExpirationType</code> associated with the given
     * <code>char</code>.
     *
     * @param inCode a <code>char</code> value
     * @return an <code>ExpirationType</code> value
     */
    public static ExpirationType getExpirationTypeForChar(char inCode)
    {
        switch(inCode) {
            case 'A' :
                return AMERICAN;
            case 'E' :
                return EUROPEAN;
            default:
                return UNKNOWN;
        }
    }
}
