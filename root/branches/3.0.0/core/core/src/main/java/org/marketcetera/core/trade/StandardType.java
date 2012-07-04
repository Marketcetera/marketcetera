package org.marketcetera.core.trade;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Indicates the standard type of a <code>Future</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: StandardType.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
@ClassVersion("$Id: StandardType.java 16063 2012-01-31 18:21:55Z colin $")
public enum StandardType
        implements HasCFICode
{
    /**
     * standard type
     */
    STANDARD('S'),
    /**
     * non-standard type
     */
    NON_STANDARD('N');
    /* (non-Javadoc)
     * @see org.marketcetera.trade.CfiType#getCfiCode()
     */
    @Override
    public char getCfiCode()
    {
        return cfiCode;
    }
    /**
     * Create a new StandardType instance.
     *
     * @param inCfiCode
     */
    private StandardType(char inCfiCode)
    {
        cfiCode = inCfiCode;
    }
    /**
     * the cfi code associated with the standard type 
     */
    private final char cfiCode;
}
