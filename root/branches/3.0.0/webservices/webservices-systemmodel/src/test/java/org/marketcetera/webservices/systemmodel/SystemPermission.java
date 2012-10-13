package org.marketcetera.webservices.systemmodel;

/* $License$ */


/**
 * Describes permission levels in the system.
 *
 * @version $Id$
 * @since $Release$
 */
public enum SystemPermission
{
    /**
     * full administrative rights to change system entities
     */
    ROLE_ADMIN,
    /**
     * right to use business functions
     */
    ROLE_USER;
}
