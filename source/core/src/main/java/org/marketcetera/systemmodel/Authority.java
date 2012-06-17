package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Represents a role granted to a {@link User}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Authority.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Authority.java 82316 2012-03-21 21:13:27Z colin $")
public interface Authority
        extends GrantedAuthority, SystemObject, NamedObject, VersionedObject
{
}
