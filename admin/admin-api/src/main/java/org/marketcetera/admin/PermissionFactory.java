package org.marketcetera.admin;

/* $License$ */

/**
 * Creates <code>Permission</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PermissionFactory.java 84382 2015-01-20 19:43:06Z colin $
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
