package org.marketcetera.modules.remote.receiver;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.client.ClientInitException;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.callback.*;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.security.Principal;

import com.sun.security.auth.UserPrincipal;

/* $License$ */
/**
 * Login module that authenticates by supplying the provided credentials to
 * {@link org.marketcetera.client.Client#isCredentialsMatch(String, char[])}. 
 * <p>
 * This login module doesn't accept any configuration options and it
 * logs to the log file via the system logging mechanism.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientLoginModule implements LoginModule {

    @Override
    public void initialize(Subject subject,
                           CallbackHandler callbackHandler,
                           Map<String, ?> sharedState,
                           Map<String, ?> options) {
        this.mSubject = subject;
        this.mCallback = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback(Messages.PROMPT_USERNAME.getText());
        callbacks[1] = new PasswordCallback(Messages.PROMPT_PASSWORD.getText(),false);
        try {
            mCallback.handle(callbacks);
        } catch (UnsupportedCallbackException e) {
            final LoginException ex = new FailedLoginException(e.getMessage());
            ex.initCause(e);
            throw ex;
        } catch (IOException e) {
            final LoginException ex = new FailedLoginException(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
        mUsername = ((NameCallback)callbacks[0]).getName();
        if(mUsername == null || mUsername.trim().length() == 0) {
            throw new AccountNotFoundException(Messages.EMPTY_USERNAME.getText());
        }
        char [] password = ((PasswordCallback)callbacks[1]).getPassword();
        try {
            if(!ClientLoginHelper.isValidCredentials(mUsername, password)) {
                Messages.USER_LOGIN_ERROR_LOG.warn(this, mUsername);
                throw new FailedLoginException(
                        Messages.USER_LOGIN_FAIL.getText(mUsername));
            }
        } catch (ClientInitException e) {
            Messages.USER_LOGIN_ERROR_LOG.warn(this,e, mUsername);
            LoginException exception = new FailedLoginException(
                    Messages.USER_LOGIN_ERROR.getText());
            exception.initCause(e);
            throw exception;
        }
        SLF4JLoggerProxy.debug(this,"login done for user {}", mUsername); //$NON-NLS-1$
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        mPrincipals.add(new UserPrincipal(mUsername));
        mSubject.getPrincipals().addAll(mPrincipals);
        Messages.USER_LOGIN_LOG.info(this, mUsername);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        SLF4JLoggerProxy.debug(this,"Aborting login for user {}", mUsername); //$NON-NLS-1$
        clear();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        mSubject.getPrincipals().removeAll(mPrincipals);
        mPrincipals.clear();
        Messages.USER_LOGOUT_LOG.info(this, mUsername);
        clear();
        return true;
    }
    private void clear() {
        mUsername = null;
    }
    private Subject mSubject;
    private CallbackHandler mCallback;
    private Set<Principal> mPrincipals = new HashSet<Principal>();
    private String mUsername;
}