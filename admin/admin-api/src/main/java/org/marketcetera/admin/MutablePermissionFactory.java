package org.marketcetera.admin;

/* $License$ */

/**
 * Creates {@link MutablePermission} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutablePermissionFactory
        extends PermissionFactory
{
    /**
     * Creates a <code>Permission</code> with the given attributes.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @return a <code>Permission</code> value
     */
    MutablePermission create(String inName,
                             String inDescription);
    /**
     * Create a new {@link MutablePermission} object.
     *
     * @return a <code>MutablePermission</code> value
     */
    MutablePermission create();
}
