package org.marketcetera.admin;

/* $License$ */

/**
 * Creates <code>UserAttribute</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserAttributeFactory.java 84561 2015-03-31 18:18:14Z colin $
 * @since 1.2.0
 */
public interface UserAttributeFactory
{
    /**
     * Create a <code>UserAttribute</code> object with the given attributes.
     *
     * @param inUser a <code>User</code> value
     * @param inType a <code>UserAttributeType</code> value
     * @param inAttribute a <code>String<code> value
     * @return a <code>UserAttribute</code> value
     */
    UserAttribute create(User inUser,
                         UserAttributeType inType,
                         String inAttribute);
    /**
     * Create a <code>UserAttribute</code> object from the given value.
     *
     * @param inUserAttribute a <code>UserAttribute</code> value
     * @return a <code>UserAttribute</code> value
     */
    UserAttribute create(UserAttribute inUserAttribute);
}
