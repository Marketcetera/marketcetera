package org.marketcetera.admin;

import javax.annotation.PostConstruct;

import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Adds an existing user as a subject to an existing supervisor role.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AddUserToSupervisorRoleAction.java 17796 2018-11-20 18:47:57Z colin $
 * @since $Release$
 */
public class AddUserToSupervisorRoleAction
{
    @PostConstruct
    public void start()
    {
        SupervisorPermission supervisorPermission = authzService.findSupervisorPermissionByName(supervisorPermissionName);
        if(supervisorPermission == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Not adding {} to {} because no supervisor permission exists by that name",
                                  subjectUsername,
                                  supervisorPermissionName);
            return;
        }
        User user = userService.findByName(subjectUsername);
        if(user == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Not adding {} to {} because no user exists by that name",
                                  subjectUsername,
                                  supervisorPermissionName);
            return;
        }
        supervisorPermission.getSubjects().add(user);
        SLF4JLoggerProxy.info(this,
                              "Adding {} to {}",
                              subjectUsername,
                              supervisorPermissionName);
        authzService.save(supervisorPermission);
    }
    /**
     * Get the subjectUsername value.
     *
     * @return a <code>String</code> value
     */
    public String getSubjectUsername()
    {
        return subjectUsername;
    }
    /**
     * Sets the subjectUsername value.
     *
     * @param a <code>String</code> value
     */
    public void setSubjectUsername(String inSubjectUsername)
    {
        subjectUsername = inSubjectUsername;
    }
    /**
     * Get the supervisorPermissionName value.
     *
     * @return a <code>String</code> value
     */
    public String getSupervisorPermissionName()
    {
        return supervisorPermissionName;
    }
    /**
     * Sets the supervisorPermissionName value.
     *
     * @param a <code>String</code> value
     */
    public void setSupervisorPermissionName(String inSupervisorPermissionName)
    {
        supervisorPermissionName = inSupervisorPermissionName;
    }
    /**
     * 
     */
    private String subjectUsername;
    /**
     * 
     */
    private String supervisorPermissionName;
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
