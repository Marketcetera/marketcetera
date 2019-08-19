package org.marketcetera.admin;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.core.PlatformServices;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Changes the password of a given user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ChangePasswordAction
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        try {
            username = StringUtils.trimToNull(username);
            oldPassword = StringUtils.trimToNull(oldPassword);
            newPassword = StringUtils.trimToNull(newPassword);
            Validate.notNull(username,
                             "Username required");
            Validate.notNull(oldPassword,
                             "Old password required");
            Validate.notNull(newPassword,
                             "New password required");
            User simpleUser = userService.findByName(username);
            Validate.notNull(simpleUser,
                             "Unknown user: " + username);
//            simpleUser.changePassword(oldPassword.toCharArray(),
//                                      newPassword.toCharArray());
//            simpleUser = userService.save(simpleUser);
            throw new UnsupportedOperationException(); // TODO
        } catch (Exception e) {
            PlatformServices.handleException(this,
                                             "Unable to change password for " + username,
                                             e);
        }
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
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /**
     * Get the oldPassword value.
     *
     * @return a <code>String</code> value
     */
    public String getOldPassword()
    {
        return oldPassword;
    }
    /**
     * Sets the oldPassword value.
     *
     * @param inOldPassword a <code>String</code> value
     */
    public void setOldPassword(String inOldPassword)
    {
        oldPassword = inOldPassword;
    }
    /**
     * Get the newPassword value.
     *
     * @return a <code>String</code> value
     */
    public String getNewPassword()
    {
        return newPassword;
    }
    /**
     * Sets the newPassword value.
     *
     * @param inNewPassword a <code>String</code> value
     */
    public void setNewPassword(String inNewPassword)
    {
        newPassword = inNewPassword;
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
     * username value
     */
    private String username;
    /**
     * old password value
     */
    private String oldPassword;
    /**
     * new password value
     */
    private String newPassword;
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
}
