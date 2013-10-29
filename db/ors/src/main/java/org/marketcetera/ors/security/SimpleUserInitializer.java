package org.marketcetera.ors.security;

import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.ors.dao.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Creates {@link SimpleUser} objects as specified.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class SimpleUserInitializer
        implements InitializingBean
{
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        if(users != null) {
            for(SimpleUser user : users) {
                userService.save(user);
            }
        }
    }
    /**
     * Get the users value.
     *
     * @return a <code>List&lt;SimpleUser&gt;</code> value
     */
    public List<SimpleUser> getUsers()
    {
        return users;
    }
    /**
     * Sets the users value.
     *
     * @param inUsers a <code>List&lt;SimpleUser&gt;</code> value
     */
    public void setUsers(List<SimpleUser> inUsers)
    {
        users = inUsers;
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
     * @param inUserService a <code>UserService</code> value
     */
    public void setUserService(UserService inUserService)
    {
        userService = inUserService;
    }
    /**
     * list of users to add, may be <code>null</code>
     */
    private List<SimpleUser> users;
    /**
     * provides access to user objects and services
     */
    @Autowired
    private UserService userService;
}
