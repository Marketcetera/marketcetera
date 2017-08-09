package org.marketcetera.admin;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SupervisorPermissionFactory
{
    SupervisorPermission create(String inName,
                                String inDescription);
}
