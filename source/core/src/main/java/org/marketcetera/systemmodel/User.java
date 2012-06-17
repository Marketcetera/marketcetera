package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.security.core.userdetails.UserDetails;

/* $License$ */

/**
 * Service object representing a system user.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface User
        extends VersionedObject, UserDetails, NamedObject, SystemObject
{
}
