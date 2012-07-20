package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Represents a role granted to a {@link User}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Authority
        extends GrantedAuthority, SystemObject, NamedObject, VersionedObject
{
}
