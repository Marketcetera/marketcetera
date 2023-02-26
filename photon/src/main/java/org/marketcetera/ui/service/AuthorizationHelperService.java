package org.marketcetera.ui.service;

import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Provides assistance resolving permissions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AuthorizationHelperService
{
    /**
     * Determines if the current user has the given authority.
     *
     * @param inGrantedAuthority a <code>GrantedAuthority</code> value
     * @return a <code>boolean</code> value
     */
    boolean hasPermission(GrantedAuthority inGrantedAuthority);
}
