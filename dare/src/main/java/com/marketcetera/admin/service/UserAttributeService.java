package com.marketcetera.admin.service;

import com.marketcetera.admin.User;
import com.marketcetera.admin.UserAttribute;
import com.marketcetera.admin.UserAttributeType;

/* $License$ */

/**
 * Provides services for <code>UserAttribute</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserAttributeService.java 84561 2015-03-31 18:18:14Z colin $
 * @since 1.2.0
 */
public interface UserAttributeService
{
    /**
     * Saves the given object.
     *
     * @param inUserAttribute a <code>UserAttribute</code> value
     * @return a <code>UserAttribute</code> value
     */
    UserAttribute save(UserAttribute inUserAttribute);
    /**
     * Gets the <code>UserAttribute</code> with the given key values.
     *
     * @param inUser a <code>User</code> value
     * @param inUserAttributeType a <code>UserAttributeType</code> value
     * @return a <code>UserAttribute</code> value or <code>null</code>
     */
    UserAttribute getUserAttribute(User inUser,
                                   UserAttributeType inUserAttributeType);
}
