package org.marketcetera.dao.domain;

import org.marketcetera.api.dao.SystemInformation;
import org.marketcetera.api.dao.SystemInformationFactory;

/* $License$ */

/**
 * Creates <code>SystemInformation</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentSystemInformationFactory
        implements SystemInformationFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.SystemInformationFactory#create()
     */
    @Override
    public SystemInformation create()
    {
        return new PersistentSystemInformation();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.SystemInformationFactory#create(org.marketcetera.api.dao.SystemInformation)
     */
    @Override
    public SystemInformation create(SystemInformation inData)
    {
        return new PersistentSystemInformation(inData);
    }
}
