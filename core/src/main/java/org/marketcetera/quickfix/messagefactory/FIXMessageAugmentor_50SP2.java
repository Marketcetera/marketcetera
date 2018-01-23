package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.quickfix.FIXVersion;

/* $License$ */

/**
 * Augments messages to and from FIX5.0SP1 to FIX5.0SP2.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FIXMessageAugmentor_50SP2
        extends FIXMessageAugmentor_50SP1
{
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.messagefactory.NoOpFIXMessageAugmentor#getFixVersion()
     */
    @Override
    protected FIXVersion getFixVersion()
    {
        return FIXVersion.FIX50SP2;
    }
}
