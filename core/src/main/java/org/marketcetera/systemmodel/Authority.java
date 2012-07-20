package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Represents a role granted to a {@link User}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Authority.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Authority.java 82384 2012-07-20 19:09:59Z colin $")
public interface Authority
        extends GrantedAuthority, SystemObject, NamedObject, VersionedObject
{
}
