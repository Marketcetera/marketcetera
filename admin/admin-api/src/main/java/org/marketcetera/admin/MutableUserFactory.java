package org.marketcetera.admin;

/* $License$ */

/**
 * Creates {@link MutableUser} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableUserFactory
        extends UserFactory
{
    /**
     * Create a <code>MutableUser</code> value
     *
     * @return a <code>MutableUser</code>value
     */
    MutableUser create();
    /**
     * Create user objects.
     *
     * @param inName a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @param inIsActive a <code>boolean</code> value
     * @return a <code>User</code> value
     */
    MutableUser create(String inName,
                       String inPassword,
                       String inDescription,
                       boolean inIsActive);
}
