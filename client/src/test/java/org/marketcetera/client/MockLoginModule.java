package org.marketcetera.client;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.apache.commons.lang.ObjectUtils;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.callback.*;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.security.Principal;

import com.sun.security.auth.UserPrincipal;

/* $License$ */
/**
 *
 * A Test login module to aid testing of Client via {@link MockServer}.
 * It allows login when the username is the same as the password.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MockLoginModule implements LoginModule {

    @Override
    public void initialize(Subject subject,
                           CallbackHandler callbackHandler,
                           Map<String, ?> sharedState,
                           Map<String, ?> options) {
        this.subject = subject;
        this.callback = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("Name");
        callbacks[1] = new PasswordCallback("Password",false);
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
        char [] password = ((PasswordCallback)callbacks[1]).getPassword();
        String pass = String.valueOf(password);
        if(!ObjectUtils.equals(username, pass)) {
            throw new FailedLoginException(username + "<>" + pass);
        }
        SLF4JLoggerProxy.debug(this,"login done for user {}",username); //$NON-NLS-1$
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        principals.add(new UserPrincipal(username));
        subject.getPrincipals().addAll(principals);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        SLF4JLoggerProxy.debug(this,"Aborting login for user {}",username); //$NON-NLS-1$
        clear();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().removeAll(principals);
        principals.clear();
        clear();
        return true;
    }
    private void clear() {
        username = null;
    }
    private Subject subject;
    private CallbackHandler callback;
    private Set<Principal> principals = new HashSet<Principal>();
    private String username;
}