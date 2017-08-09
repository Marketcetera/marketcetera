package org.marketcetera.admin;

/* $License$ */

/**
 * Enumerates user attribute type values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserAttributeType.java 84561 2015-03-31 18:18:14Z colin $
 * @since 1.2.0
 */
public enum UserAttributeType
{
    /**
     * stores the user's preferred UI display layout
     */
    DISPLAY_LAYOUT,
    /**
     * list of strategy engines known to this user
     */
    STRATEGY_ENGINES
}
