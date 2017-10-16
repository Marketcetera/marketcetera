package org.marketcetera.admin.user;

import java.util.List;

import org.marketcetera.admin.service.UserService;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Creates {@link PersistentUser} objects as specified.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SimpleUserInitializer.java 17339 2017-08-10 02:14:34Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: SimpleUserInitializer.java 17339 2017-08-10 02:14:34Z colin $")
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
            for(PersistentUser user : users) {
                try {
                    userService.save(user);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                         "Not adding user: {} because {}",
                                         user,
                                         e.getMessage());
                }
            }
        }
    }
    /**
     * Get the users value.
     *
     * @return a <code>List&lt;SimpleUser&gt;</code> value
     */
    public List<PersistentUser> getUsers()
    {
        return users;
    }
    /**
     * Sets the users value.
     *
     * @param inUsers a <code>List&lt;SimpleUser&gt;</code> value
     */
    public void setUsers(List<PersistentUser> inUsers)
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
    private List<PersistentUser> users;
    /**
     * provides access to user objects and services
     */
    @Autowired
    private UserService userService;
}
