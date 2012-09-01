package org.marketcetera.api.dao;

import org.marketcetera.api.security.GrantedAuthority;
import org.marketcetera.api.systemmodel.NamedObject;
import org.marketcetera.api.systemmodel.SystemObject;
import org.marketcetera.api.systemmodel.VersionedObject;

/* $License$ */

/**
 * Represents a role granted to a {@link org.marketcetera.api.security.User}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Authority.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
public interface Authority
        extends GrantedAuthority, SystemObject, NamedObject, VersionedObject
{
}
