package com.marketcetera.admin;

import javax.annotation.PostConstruct;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.marketcetera.admin.service.AuthorizationService;
import com.marketcetera.ors.dao.UserService;

/* $License$ */

/**
 * Add an existing user to an existing role.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AddUserToRoleAction
{
    @PostConstruct
    public void start()
    {
        Role role = authzService.findRoleByName(roleName);
        if(role == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Not adding {} to {} because no role exists by that name",
                                  username,
                                  roleName);
            return;
        }
        User user = userService.findByName(username);
        if(user == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Not adding {} to {} because no user exists by that name",
                                  username,
                                  roleName);
            return;
        }
        role.getSubjects().add(user);
        SLF4JLoggerProxy.info(this,
                              "Adding {} to {}",
                              username,
                              roleName);
        authzService.save(role);
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
     * Get the roleName value.
     *
     * @return a <code>String</code> value
     */
    public String getRoleName()
    {
        return roleName;
    }
    /**
     * Sets the roleName value.
     *
     * @param a <code>String</code> value
     */
    public void setRoleName(String inRoleName)
    {
        roleName = inRoleName;
    }
    /**
     * 
     */
    private String username;
    /**
     * 
     */
    private String roleName;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
}
