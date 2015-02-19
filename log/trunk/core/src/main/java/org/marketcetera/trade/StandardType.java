package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates the standard type of a <code>Future</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
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
