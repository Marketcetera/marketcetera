package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Describes authority levels in the system.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SystemAuthority.java 82320 2012-04-02 17:03:23Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: SystemAuthority.java 82320 2012-04-02 17:03:23Z colin $")
public enum SystemAuthority
{
    /**
     * full administrative rights to change system entities
     */
    ROLE_ADMIN,
    /**
     * right to use business functions
     */
    ROLE_USER;
    /**
     * Gets this value represented as a <code>GrantedAuthority</code>.
     *
     * @return a <code>GrantedAuthority</code> value
     */
    public GrantedAuthority getAsGrantedAuthority()
    {
        return new GrantedAuthority() {
            @Override
            public String getAuthority()
            {
                return name();
            }
            private static final long serialVersionUID = 1L;
        };
    }
}
