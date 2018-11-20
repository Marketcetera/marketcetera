package org.marketcetera.trade.impl;

import org.marketcetera.admin.User;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.IdentifyOwnerStrategy;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides an owner using a default user.
 * 
 * <p>This strategy is intended to always succeed and should be used as a last option since it assigns
 * ownership of everything to a single user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DefaultOwnerStrategy
        implements IdentifyOwnerStrategy
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.IdentifyOwnerStrategy#getOwnerOf(quickfix.Message, quickfix.SessionID, org.marketcetera.trade.BrokerID)
     */
    @Override
    public UserID getOwnerOf(Message inMessage,
                             SessionID inSessionId,
                             BrokerID inBrokerId)
    {
        synchronized(lookupLock) {
            if(defaultUser == null) {
                defaultUser = userService.findByName(username);
            }
        }
        if(defaultUser == null) {
            SLF4JLoggerProxy.warn(this,
                                  "No user by name ''{}'' exists",
                                  username);
            return null;
        }
        return defaultUser.getUserID();
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /**
     * Get the userService value.
     *
     * @return a <code>UserService</code> value
     */
    public UserService getUserService()
    {
        return userService;
    }
    /**
     * Sets the userService value.
     *
     * @param a <code>UserService</code> value
     */
    public void setUserService(UserService inUserService)
    {
        userService = inUserService;
    }
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * username value of the default owner
     */
    private String username;
    /**
     * resolved name of the default user
     */
    private User defaultUser;
    /**
     * guards access to the default user
     */
    private final Object lookupLock = new Object();
}
