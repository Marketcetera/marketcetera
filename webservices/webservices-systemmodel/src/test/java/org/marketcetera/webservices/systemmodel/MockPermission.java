package org.marketcetera.webservices.systemmodel;

import java.util.Set;

import org.marketcetera.api.dao.MutablePermission;
import org.marketcetera.api.dao.PermissionAttribute;

/* $License$ */

/**
 * Provides a test <code>Permission</code> implementation.
 *
 * @version $Id: MockPermission.java 16253 2012-09-04 18:35:21Z topping $
 * @since $Release$
 */
public class MockPermission
        extends MockVersionedObject
        implements MutablePermission
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getDescription()
     */
    @Override
    public String getDescription()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
    * @see org.marketcetera.api.systemmodel.NamedObject#getName()
    */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Permission#getPermission()
     */
    @Override
    public String getPermission()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutablePermission#setPermission(java.lang.String)
     */
    @Override
    public void setPermission(String inPermission)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.GrantedPermission#getMethod()
     */
    @Override
    public Set<PermissionAttribute> getMethod()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutablePermission#setMethod(java.util.Set)
     */
    @Override
    public void setMethod(Set<PermissionAttribute> inMethod)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * permission value
     */
    private String name;
}
