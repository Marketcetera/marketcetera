package org.marketcetera.admin;

/* $License$ */

/**
 * Creates <code>Permission</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
public interface PermissionFactory
{
    /**
     * Creates a <code>Permission</code> with the given attributes.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @return a <code>Permission</code> value
     */
    Permission create(String inName,
                      String inDescription);
}
