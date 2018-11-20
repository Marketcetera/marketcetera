package org.marketcetera.admin.impl;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserAttributeType;

/* $License$ */

/**
 * Creates <code>UserAttribute</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.2.0
 */
public class PersistentUserAttributeFactory
        implements UserAttributeFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserAttributeFactory#create(com.marketcetera.admin.User, com.marketcetera.admin.UserAttributeType, java.lang.String)
     */
    @Override
    public UserAttribute create(User inUser,
                                UserAttributeType inType,
                                String inAttribute)
    {
        return new PersistentUserAttribute(inUser,
                                           inType,
                                           inAttribute);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.systemmodel.UserAttributeFactory#create(com.marketcetera.tiaacref.systemmodel.UserAttribute)
     */
    @Override
    public PersistentUserAttribute create(UserAttribute inUserAttribute)
    {
        return new PersistentUserAttribute(inUserAttribute);
    }
}
