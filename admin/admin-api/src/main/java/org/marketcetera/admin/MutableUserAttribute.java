package org.marketcetera.admin;

/* $License$ */

/**
 * Provides a mutable {@link UserAttribute} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableUserAttribute
        extends UserAttribute
{
    /**
     * Set the user value.
     *
     * @param inUser a <code>User</code> value
     */
    void setUser(User inUser);
    /**
     * Set the user attribute type value.
     *
     * @param inUserAttributeType a <code>UserAttributeType</code> value
     */
    void setAttributeType(UserAttributeType inUserAttributeType);
    /**
     * Set the attribute value.
     *
     * @param inAttribute a <code>String</code> value
     */
    void setAttribute(String inAttribute);
}
