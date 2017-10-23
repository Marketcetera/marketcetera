package org.marketcetera.admin.impl;

import org.marketcetera.admin.MutableUser;
import org.marketcetera.admin.MutableUserFactory;

/* $License$ */

/**
 * Creates simple user objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleUserFactory
        implements MutableUserFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserFactory#create(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public MutableUser create(String inName,
                              String inPassword,
                              String inDescription,
                              boolean inIsActive)
    {
        return new SimpleUser(inName,
                              inDescription,
                              inPassword,
                              inIsActive);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.MutableUserFactory#create()
     */
    @Override
    public MutableUser create()
    {
        return new SimpleUser();
    }
}
