package org.marketcetera.admin;

/* $License$ */

/**
 * Creates {@link MutableUserAttribute} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableUserAttributeFactory
        extends UserAttributeFactory
{
    /**
     * Create a <code>MutableUserAttribute</code> object with the given attributes.
     *
     * @param inUser a <code>User</code> value
     * @param inType a <code>UserAttributeType</code> value
     * @param inAttribute a <code>String<code> value
     * @return a <code>UserAttribute</code> value
     */
    MutableUserAttribute create(User inUser,
                                UserAttributeType inType,
                                String inAttribute);
    /**
     * Creates a <code>UserAttribute</code> object from the given value.
     *
     * @param inUserAttribute a <code>UserAttribute</code> value
     * @return a <code>UserAttribute</code> value
     */
    MutableUserAttribute create(UserAttribute inUserAttribute);
}
