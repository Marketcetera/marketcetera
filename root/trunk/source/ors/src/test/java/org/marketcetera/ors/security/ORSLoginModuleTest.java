package org.marketcetera.ors.security;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.PersistTestBase;
import static org.marketcetera.ors.security.Messages.EMPTY_USERNAME;
import static org.marketcetera.ors.security.Messages.PROMPT_USERNAME;
import static org.marketcetera.ors.security.Messages.PROMPT_PASSWORD;
import static org.marketcetera.ors.security.Messages.USER_LOGIN_ERROR;
import static org.marketcetera.ors.security.Messages.USER_LOGIN_ERROR_LOG;
import static org.marketcetera.ors.security.Messages.USER_LOGIN_LOG;
import static org.marketcetera.ors.security.Messages.USER_LOGOUT_LOG;
import org.marketcetera.util.test.TestCaseBase;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.apache.log4j.Level;

import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.sun.security.auth.UserPrincipal;

/* $License$ */
/**
 * Tests ORSLoginModule functionality
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class ORSLoginModuleTest extends TestCaseBase {
    private static SimpleUser user;
    private static char[] password;
    private static boolean doNotHandleCallbacks = false;
    private static IOException callbackException = null;
    private LoginContext loginContext;

    /**
     * Test login success & failures.
     * @throws Exception if there was failure
     */
    @Test
    public void loginTest() throws Exception {
        setLevel(ORSLoginModule.class.getName(), Level.INFO);
        //test failure conditions
        attemptLogin(null, password,
                AccountNotFoundException.class,
                EMPTY_USERNAME.getText());
        attemptLogin("", password, //$NON-NLS-1$
                AccountNotFoundException.class,
                EMPTY_USERNAME.getText());
        final String u = randomString();
        attemptLogin(u, password,
                AccountNotFoundException.class,
                USER_LOGIN_ERROR.getText());
        assertLastEvent(Level.WARN, ORSLoginModule.class.getName(),
                USER_LOGIN_ERROR_LOG.getText(u),
                ORSLoginModule.class.getName());

        attemptLogin(user.getName(), null,
                FailedLoginException.class,
                USER_LOGIN_ERROR.getText());
        assertLastEvent(Level.WARN, ORSLoginModule.class.getName(),
                USER_LOGIN_ERROR_LOG.getText(user.getName()),
                ORSLoginModule.class.getName());

        attemptLogin(user.getName(), "".toCharArray(), //$NON-NLS-1$
                FailedLoginException.class,
                USER_LOGIN_ERROR.getText());
        assertLastEvent(Level.WARN, ORSLoginModule.class.getName(),
                USER_LOGIN_ERROR_LOG.getText(user.getName()),
                ORSLoginModule.class.getName());

        attemptLogin(user.getName(), randomString().toCharArray(),
                FailedLoginException.class,
                USER_LOGIN_ERROR.getText());
        assertLastEvent(Level.WARN, ORSLoginModule.class.getName(),
                USER_LOGIN_ERROR_LOG.getText(user.getName()),
                ORSLoginModule.class.getName());

        //test successful login
        attemptLogin(user.getName(), password, null, null);
        assertLastEvent(Level.INFO, ORSLoginModule.class.getName(),
                USER_LOGIN_LOG.getText(user.getName()),
                ORSLoginModule.class.getName());

        // test logout removes the principal from the subject
        loginContext.logout();
        assertTrue(loginContext.getSubject().getPrincipals().isEmpty());
        assertLastEvent(Level.INFO, ORSLoginModule.class.getName(),
                USER_LOGOUT_LOG.getText(user.getName()),
                ORSLoginModule.class.getName());
    }

    /**
     * test unsupported callbacks
     * @throws Exception if there was failure
     */
    @Test
    public void unsupportedCallback() throws Exception {
        doNotHandleCallbacks = true;
        UnsupportedCallbackException uce = new UnsupportedCallbackException(
                new NameCallback(PROMPT_USERNAME.getText()));
        LoginException ex = attemptLogin(user.getName(), password,
                LoginException.class, uce.getMessage());
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof UnsupportedCallbackException);
        Callback callback = ((UnsupportedCallbackException) ex.getCause()).getCallback();
        assertNotNull(callback);
        assertTrue(callback.getClass().toString(),
                callback instanceof NameCallback);
        assertEquals(PROMPT_USERNAME.getText(),
                ((NameCallback)callback).getPrompt());
    }

    /**
     * test callback io failure
     * @throws Exception if there was a failure
     */
    @Test
    public void callbackIOFailure() throws Exception {
        callbackException = new IOException("ioeoeoe"); //$NON-NLS-1$
        LoginException ex = attemptLogin(user.getName(), password,
                LoginException.class, callbackException.getMessage());
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof IOException);
        assertSame(callbackException, ex.getCause());
    }

    @BeforeClass
    public static void setup() throws Exception {
        springSetup();
        user = new SimpleUser();
        user.setName(randomString());
        password = randomString().toCharArray();
        user.setPassword(password);
        user.save();
        Configuration.setConfiguration(new MockConfiguration());
    }
    @AfterClass
    public static void cleanup() throws Exception {
        user.delete();
        user = null;
    }
    @After
    public void reset() throws Exception {
        doNotHandleCallbacks = false;
        callbackException = null;
    }

    static void springSetup()
        throws Exception {
        PersistTestBase.springSetup(getSpringFiles()); //$NON-NLS-1$
    }

    static String[] getSpringFiles() {
        return new String[] {
            "file:"+DIR_ROOT+File.separator+ //$NON-NLS-1$
            "conf"+File.separator+ //$NON-NLS-1$
            "persist_tests.xml"}; //$NON-NLS-1$
    }

    private static String randomString() {
        return PersistTestBase.randomString();
    }

    /**
     * Attempt login and test for failure / success conditions
     * 
     * @param name the user name
     * @param password the password
     * @param failure expected failure
     * @param failureMsg expected failure message
     *
     * @return the failure exception if any
     *
     * @throws Exception if there was unexpected failure
     */
    private LoginException attemptLogin(
            String name, char[] password,
            Class<? extends LoginException> failure,
            String failureMsg) throws Exception{
        MockCallbackHandler ch = null;
        loginContext = null;
        try {
            ch = new MockCallbackHandler(name, password);
            loginContext = new LoginContext("ors_test",ch); //$NON-NLS-1$
            loginContext.login();
            assertNull("Expected failure:" + failure + failureMsg, failure); //$NON-NLS-1$
            //verify that the appropriate principals are set in the subject
            assertTrue(loginContext.getSubject().getPrincipals().toString(),
                    loginContext.getSubject().getPrincipals().contains(
                            new UserPrincipal(user.getName())));
        } catch (LoginException e) {
            assertNotNull("Unexpected failure:" + e,failure); //$NON-NLS-1$
            assertTrue("Expected:" + failure + ":Actual:" + //$NON-NLS-1$ //$NON-NLS-2$
                    e.getClass().getName() + e.toString(),
                    failure.isInstance(e));
            if (failureMsg != null) {
                assertEquals(failureMsg,e.getMessage());
            }
            assertNotNull(loginContext);
            //verify that the appropriate principals are not set in the subject
            if (loginContext.getSubject() != null &&
                    loginContext.getSubject().getPrincipals() != null) {
                assertFalse(loginContext.getSubject().getPrincipals().toString(),
                    loginContext.getSubject().getPrincipals().contains(
                            new UserPrincipal(user.getName())));
            }
            assertEquals(2,ch.getNumCallbacks());
            //These values are only set if call back handler doesn't throw
            //exceptions
            if (callbackException == null && !doNotHandleCallbacks) {
                assertEquals(PROMPT_USERNAME.getText(), ch.getNamePrompt());
                assertEquals(PROMPT_PASSWORD.getText(), ch.getPasswordPrompt());
                assertNull(ch.getDefaultName());
            }
            return e;
        }
        return null;
    }

    /**
     * Test call back handler
     */
    private static class MockCallbackHandler implements CallbackHandler {

        MockCallbackHandler(String nameValue, char[] passwordValue) {
            this.nameValue = nameValue;
            this.passwordValue = passwordValue;
        }

        public String getNamePrompt() {
            return namePrompt;
        }

        public String getPasswordPrompt() {
            return passwordPrompt;
        }

        public String getDefaultName() {
            return defaultName;
        }

        public int getNumCallbacks() {
            return numCallbacks;
        }

        public void handle(Callback[] callbacks)
                throws IOException, UnsupportedCallbackException {
            if(callbacks == null) {
                return;
            }
            numCallbacks = callbacks.length;
            for(Callback c :callbacks) {
                if(doNotHandleCallbacks) {
                    throw new UnsupportedCallbackException(c);
                }
                if(callbackException != null) {
                    throw callbackException;
                }
                if(c instanceof NameCallback) {
                    NameCallback nc = (NameCallback) c;
                    namePrompt = nc.getPrompt();
                    defaultName = nc.getName();
                    nc.setName(nameValue);
                } else if (c instanceof PasswordCallback) {
                    PasswordCallback pc = (PasswordCallback) c;
                    passwordPrompt = pc.getPrompt();
                    pc.setPassword(passwordValue);
                } else {
                    throw new UnsupportedCallbackException(c);
                }
            }
        }
        private int numCallbacks;
        private String namePrompt;
        private String defaultName;
        private String nameValue;
        private String passwordPrompt;
        private char[] passwordValue;
    }

    /**
     * Create our own configuration so as to not have to create login
     * configuration files
     */
    private static class MockConfiguration extends Configuration {
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            return new AppConfigurationEntry[]{
                    new AppConfigurationEntry(ORSLoginModule.class.getName(),
                            AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                            new HashMap<String,String>())
            };
        }
    }
}
