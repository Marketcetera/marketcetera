package com.marketcetera.ors.outgoingorder.impl;

import org.marketcetera.trade.UserID;
import org.springframework.beans.factory.annotation.Autowired;

import com.marketcetera.ors.dao.UserService;
import com.marketcetera.ors.outgoingorder.AccountUserLookupProvider;
import com.marketcetera.ors.security.SimpleUser;

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
