package org.marketcetera.trade;

import org.marketcetera.trade.UserID;

/* $License$ */

/**
 * Lookups up users based on an account value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AccountUserLookupProvider
{
    /**
     * Get the user associated with the given account.
     *
     * @param inAccount a <code>String</code> value
     * @return a <code>UserID</code> value
     */
    UserID getUserFor(String inAccount);
}
