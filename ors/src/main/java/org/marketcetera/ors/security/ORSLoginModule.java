package org.marketcetera.ors.security;

import static org.marketcetera.ors.security.Messages.*;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.sun.security.auth.UserPrincipal;

/* $License$ */
/**
 * Login module that authenticates with users setup in ORS database
 * This login module will only allow username, password combinations
 * that are persisted in ORS via the {@link SimpleUser} instances.
 * <p>
 * This login module doesn't accept any configuration options and it
 * logs to the ORS log file via ORS logging mechanism.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class ORSLoginModule
        implements LoginModule
{
    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    @Override
    public void initialize(Subject inSubject,
                           CallbackHandler inCallbackHandler,
                           Map<String,?> inSharedState,
                           Map<String,?> inOptions)
    {
        subject = inSubject;
        callback = inCallbackHandler;
    }
    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login()
            throws LoginException
    {
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback(PROMPT_USERNAME.getText());
        callbacks[1] = new PasswordCallback(PROMPT_PASSWORD.getText(),false);
        try {
            callback.handle(callbacks);
        } catch (UnsupportedCallbackException e) {
            final LoginException ex = new FailedLoginException(e.getMessage());
            ex.initCause(e);
            throw ex;
        } catch (IOException e) {
            final LoginException ex = new FailedLoginException(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
        username = ((NameCallback)callbacks[0]).getName();
        if(username == null || username.trim().length() == 0) {
            throw new AccountNotFoundException(EMPTY_USERNAME.getText());
        }
        char [] password = ((PasswordCallback)callbacks[1]).getPassword();
        try {
            SimpleUser u = userService.findByName(username);
            if (u == null) {
                USER_LOGIN_ERROR_LOG.warn(this,username);
                throw new AccountNotFoundException(USER_LOGIN_ERROR.getText());
            }
            if (!u.isActive()) {
                USER_LOGIN_ERROR_LOG.warn(this,username);
                throw new AccountNotFoundException(USER_LOGIN_ERROR.getText());
            }
            u.validatePassword(password);
        } catch (PersistenceException e) {
            USER_LOGIN_ERROR_LOG.warn(this,e,username);
            throw new FailedLoginException(USER_LOGIN_ERROR.getText());
        }
        SLF4JLoggerProxy.debug(this,"login done for user {}",username); //$NON-NLS-1$
        return true;
    }
    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#abort()
     */
    @Override
    public boolean abort()
            throws LoginException
    {
        SLF4JLoggerProxy.debug(this,"Aborting login for user {}",username); //$NON-NLS-1$
        clear();
        return true;
    }
    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#commit()
     */
    @Override
    public boolean commit()
            throws LoginException
    {
        principals.add(new UserPrincipal(username));
        subject.getPrincipals().addAll(principals);
        USER_LOGIN_LOG.info(this,
                            username);
        return true;
    }
    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    @Override
    public boolean logout()
            throws LoginException
    {
        subject.getPrincipals().removeAll(principals);
        principals.clear();
        USER_LOGOUT_LOG.info(this,
                             username);
        clear();
        return true;
    }
    /**
     * Get the userService value.
     *
     * @return a <code>UserService</code> value
     */
    public static UserService getUserService()
    {
        return userService;
    }
    /**
     * Sets the userService value.
     *
     * @param inUserService a <code>UserService</code> value
     */
    public static void setUserService(UserService inUserService)
    {
        userService = inUserService;
    }
    /**
     * Clears the login state.
     */
    private void clear()
    {
        username = null;
    }
    /**
     * login subject
     */
    private Subject subject;
    /**
     * login callback handler
     */
    private CallbackHandler callback;
    /**
     * login principals collection
     */
    private Set<Principal> principals = new HashSet<Principal>();
    /**
     * login username
     */
    private String username;
    /**
     * provides datastore access to user objects
     */
    private static UserService userService;
}
