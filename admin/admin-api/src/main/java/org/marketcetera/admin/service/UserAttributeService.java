package org.marketcetera.admin.service;

import java.util.Collection;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;

/* $License$ */

/**
 * Provides services for <code>UserAttribute</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
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
    /**
     * Remove the given user attribute.
     *
     * @param inUserAttribute a <code>UserAttribute</code> value
     */
    void delete(UserAttribute inUserAttribute);
    /**
     * Gets the <code>UserAttribute</code> values for the given user.
     *
     * @param inUser a <code>User</code> value
     * @return a <code>Collection&lt;UserAttribute&gt;</code> value
     */
    Collection<UserAttribute> getAllByUser(User inUser);
}
