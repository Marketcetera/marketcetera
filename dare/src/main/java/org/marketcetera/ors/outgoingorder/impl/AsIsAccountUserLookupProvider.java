package org.marketcetera.ors.outgoingorder.impl;

import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.outgoingorder.AccountUserLookupProvider;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.UserID;
import org.springframework.beans.factory.annotation.Autowired;


/* $License$ */

/**
 * Looks up users by account without translating the name.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AsIsAccountUserLookupProvider
        implements AccountUserLookupProvider
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.AccountUserLookupProvider#getUserFor(java.lang.String)
     */
    @Override
    public UserID getUserFor(String inAccount)
    {
        SimpleUser user = userService.findByName(inAccount);
        if(user == null) {
            return null;
        }
        return user.getUserID();
    }
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
}
