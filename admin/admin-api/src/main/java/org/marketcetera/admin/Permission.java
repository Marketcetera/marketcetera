package org.marketcetera.admin;

import org.marketcetera.persist.SummaryNDEntityBase;
import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Indicates a specific permission that can be checked to verify access is allowed for a particular function.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
public interface Permission
        extends SummaryNDEntityBase,GrantedAuthority
{
    /* (non-Javadoc)
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Override
    default String getAuthority()
    {
        return getName();
    }
}
