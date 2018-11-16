package org.marketcetera.admin;

/* $License$ */

/**
 * Creates {@link Role} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: RoleFactory.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
public interface RoleFactory
{
    /**
     * Creates a <code>Role</code> object with the given attributes.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @return a <code>Role</code> value
     */
    Role create(String inName,
                String inDescription);
}
