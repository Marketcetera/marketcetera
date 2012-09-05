package org.marketcetera.webservices.systemmodel;

/* $License$ */

import org.marketcetera.api.dao.Permission;

/**
 * Constructs {@link org.marketcetera.api.dao.Permission} objects.
 *
 * @version $Id: PermissionFactory.java 82315 2012-03-17 01:58:54Z colin $
 * @since $Release$
 */
public interface PermissionFactory
{
    /**
     * Creates an <code>Permission</code> with the given name.
     *
     * @param inPermissionName a <code>String</code> value
     * @return an <code>Permission</code> value
     */
    public Permission create(String inPermissionName);
    /**
     * Creates an <code>Permission</code> object.
     *
     * @return an <code>Permission</code> value
     */
    public Permission create();
}
