package com.marketcetera.admin.impl;

import com.marketcetera.admin.User;
import com.marketcetera.admin.UserAttribute;
import com.marketcetera.admin.UserAttributeFactory;
import com.marketcetera.admin.UserAttributeType;

/* $License$ */

/**
 * Constructs {@link SimpleUserAttribute} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleUserAttributeFactory
        implements UserAttributeFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserAttributeFactory#create(com.marketcetera.admin.User, com.marketcetera.admin.UserAttributeType, java.lang.String)
     */
    @Override
    public SimpleUserAttribute create(User inUser,
                                      UserAttributeType inType,
                                      String inAttribute)
    {
        return new SimpleUserAttribute(inUser,
                                       inType,
                                       inAttribute);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserAttributeFactory#create(com.marketcetera.admin.UserAttribute)
     */
    @Override
    public SimpleUserAttribute create(UserAttribute inUserAttribute)
    {
        return new SimpleUserAttribute(inUserAttribute);
    }
}
