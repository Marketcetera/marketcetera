package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.security.core.userdetails.UserDetails;

/* $License$ */

/**
 * Service object representing a system user.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: User.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: User.java 82384 2012-07-20 19:09:59Z colin $")
public interface User
        extends VersionedObject, UserDetails, NamedObject, SystemObject
{
}
