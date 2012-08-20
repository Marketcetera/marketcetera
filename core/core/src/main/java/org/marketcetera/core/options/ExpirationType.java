package org.marketcetera.core.options;

/* $License$ */

/**
 * Represents the expiration type of an option.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExpirationType.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
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
