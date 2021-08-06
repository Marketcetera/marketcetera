package org.marketcetera.fix;

import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Defines FIX permission names.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum FixPermissions
        implements GrantedAuthority
{
    /**
     * view the status of brokers
     */
    ViewBrokerStatusAction;
    /* (non-Javadoc)
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Override
    public String getAuthority()
    {
        return name();
    }
}
