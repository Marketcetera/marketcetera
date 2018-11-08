package com.marketcetera.auth;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.CrowdClient;
import com.google.common.collect.Sets;
import com.marketcetera.admin.Role;
import com.marketcetera.admin.UserFactory;
import com.marketcetera.admin.service.AuthorizationService;
import com.marketcetera.ors.dao.UserService;
import com.marketcetera.ors.security.SimpleUser;
import com.marketcetera.ors.ws.DBAuthenticator;

/* $License$ */

/**
 * Provides authentication via Atlassian Crowd.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AtlassianAuthenticator
        extends DBAuthenticator
        implements Authenticator
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        crowdClient = new RestCrowdClientFactory().newInstance(crowdUrl,
                                                               applicationName,
                                                               applicationPassword);
        SLF4JLoggerProxy.info(this,
                              "Atlassian authenticator started");
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.ws.DBAuthenticator#shouldAllow(org.marketcetera.util.ws.stateless.StatelessClientContext, java.lang.String, char[])
     */
    @Override
    public boolean shouldAllow(StatelessClientContext inContext,
                               String inUsername,
                               char[] inPassword)
            throws I18NException
    {
        boolean authenticated = false;
        try {
            int retryCount = 0;
            while(!authenticated && retryCount < retryMax) {
                try {
                    User atlassianUser = crowdClient.authenticateUser(inUsername,
                                                                      new String(inPassword));
                    SLF4JLoggerProxy.debug(this,
                                           "User {} authenticated",
                                           atlassianUser);
                    SimpleUser localUser = userService.findByName(inUsername);
                    if(localUser == null && addUnknownUsers) {
                        SLF4JLoggerProxy.debug(this,
                                               "User {} does not yet exist in local db, adding",
                                               inUsername);
                        localUser = (SimpleUser)userFactory.create(inUsername,
                                                                   new String(inPassword),
                                                                   null,
                                                                   true);
                        localUser = userService.save(localUser);
                        if(addNewUsersToRoles) {
                            // add this user to some roles
                            for(String roleName : newUserRoles) {
                                try {
                                    Role role = authorizationService.findRoleByName(roleName);
                                    if(role == null) {
                                        throw new IllegalArgumentException("Role not found");
                                    }
                                    role.getSubjects().add(localUser);
                                    role = authorizationService.save(role);
                                    SLF4JLoggerProxy.debug(this,
                                                           "{} added to {}",
                                                           inUsername,
                                                           roleName);
                                } catch (Exception e) {
                                    SLF4JLoggerProxy.warn(this,
                                                          e,
                                                          "Unable to add {} to {}",
                                                          inUsername,
                                                          roleName);
                                }
                            }
                        }
                        SLF4JLoggerProxy.info(this,
                                              "New user {} added",
                                              inUsername);
                    }
                    authenticated = true;
                } catch (OperationFailedException e) {
                    // this exception is thrown, oddly, nearly every time on the first authentication request, so, try a few times if this happens
                    if(retryCount++ < retryMax) {
                        SLF4JLoggerProxy.debug(this,
                                               e,
                                               "Cannot authenticate {}",
                                               inUsername);
                        Thread.sleep(retryDelay);
                    } else {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Cannot authenticate {}",
                                              inUsername);
                    }
                } catch (UserNotFoundException | InactiveAccountException | ExpiredCredentialException | ApplicationPermissionException | InvalidAuthenticationException e) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          "Cannot authenticate {}",
                                          inUsername);
                    break;
                }
            }
        } catch (InterruptedException e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new I18NException(e);
        }
        return authenticated;
    }
    /**
     * Get the crowdUrl value.
     *
     * @return a <code>String</code> value
     */
    public String getCrowdUrl()
    {
        return crowdUrl;
    }
    /**
     * Sets the crowdUrl value.
     *
     * @param inCrowdUrl a <code>String</code> value
     */
    public void setCrowdUrl(String inCrowdUrl)
    {
        crowdUrl = inCrowdUrl;
    }
    /**
     * Get the applicationName value.
     *
     * @return a <code>String</code> value
     */
    public String getApplicationName()
    {
        return applicationName;
    }
    /**
     * Sets the applicationName value.
     *
     * @param inApplicationName a <code>String</code> value
     */
    public void setApplicationName(String inApplicationName)
    {
        applicationName = inApplicationName;
    }
    /**
     * Get the applicationPassword value.
     *
     * @return a <code>String</code> value
     */
    public String getApplicationPassword()
    {
        return applicationPassword;
    }
    /**
     * Sets the applicationPassword value.
     *
     * @param inApplicationPassword a <code>String</code> value
     */
    public void setApplicationPassword(String inApplicationPassword)
    {
        applicationPassword = inApplicationPassword;
    }
    /**
     * Get the newUserRoles value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getNewUserRoles()
    {
        return newUserRoles;
    }
    /**
     * Sets the newUserRoles value.
     *
     * @param inNewUserRoles a <code>Set&lt;String&gt;</code> value
     */
    public void setNewUserRoles(Set<String> inNewUserRoles)
    {
        newUserRoles = inNewUserRoles;
    }
    /**
     * Get the addUnknownUsers value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getAddUnknownUsers()
    {
        return addUnknownUsers;
    }
    /**
     * Sets the addUnknownUsers value.
     *
     * @param inAddUnknownUsers a <code>boolean</code> value
     */
    public void setAddUnknownUsers(boolean inAddUnknownUsers)
    {
        addUnknownUsers = inAddUnknownUsers;
    }
    /**
     * Get the addNewUsersToRoles value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getAddNewUsersToRoles()
    {
        return addNewUsersToRoles;
    }
    /**
     * Sets the addNewUsersToRoles value.
     *
     * @param inAddNewUsersToRoles a <code>boolean</code> value
     */
    public void setAddNewUsersToRoles(boolean inAddNewUsersToRoles)
    {
        addNewUsersToRoles = inAddNewUsersToRoles;
    }
    /**
     * Get the retryMax value.
     *
     * @return an <code>int</code> value
     */
    public int getRetryMax()
    {
        return retryMax;
    }
    /**
     * Sets the retryMax value.
     *
     * @param inRetryMax an <code>int</code> value
     */
    public void setRetryMax(int inRetryMax)
    {
        retryMax = inRetryMax;
    }
    /**
     * Get the retryDelay value.
     *
     * @return a <code>long</code> value
     */
    public long getRetryDelay()
    {
        return retryDelay;
    }
    /**
     * Sets the retryDelay value.
     *
     * @param inRetryDelay a <code>long</code> value
     */
    public void setRetryDelay(long inRetryDelay)
    {
        retryDelay = inRetryDelay;
    }
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authorizationService;
    /**
     * creates new {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * indicates if unknown users should be added
     */
    private boolean addUnknownUsers = true;
    /**
     * indicates if unknown users should be assigned to the indicated roles
     */
    private boolean addNewUsersToRoles = true;
    /**
     * role names to give to new user
     */
    private Set<String> newUserRoles = Sets.newHashSet("Trader");
    /**
     * crowd client object
     */
    private CrowdClient crowdClient;
    /**
     * url used to connect to crowd
     */
    private String crowdUrl = "https://crowd.marketcetera.com/crowd";
    /**
     * crowd application name
     */
    private String applicationName = "dare";
    /**
     * crowd password
     */
    private String applicationPassword = "password";
    /**
     * max number of times to retry authentication
     */
    private int retryMax = 5;
    /**
     * delay between each authentication attempt
     */
    private long retryDelay = 1000;
}
