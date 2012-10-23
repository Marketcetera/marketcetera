package org.marketcetera.api.dao;

/* $License$ */

/**
 * Provides datastore access to <code>SystemInformation</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SystemInformationDao
{
    /**
     * Gets the <code>SystemInformation</code> value.
     *
     * @return a <code>SystemInformation</code> value
     */
    public SystemInformation get();
}
