package org.marketcetera.persist;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides Derby specific implementation of DB Vendor.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DerbyDBVendor
        extends DBVendor
{
    /**
     * Create a new DerbyDBVendor instance.
     *
     * @throws PersistSetupException
     */
    private DerbyDBVendor()
            throws PersistSetupException
    {
        super();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.DBVendor#validateText(java.lang.CharSequence)
     */
    @Override
    void validateText(CharSequence inS)
            throws ValidationException
    {
        // nothing to do
    }
}
