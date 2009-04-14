package org.marketcetera.modules.remote.receiver;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.misc.RandomStrings;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.module.ExpectedFailure;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.apache.log4j.Level;
import org.apache.commons.lang.ObjectUtils;

import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import java.io.IOException;
import java.util.Arrays;

import com.sun.security.auth.UserPrincipal;

/* $License$ */
/**
 * Tests {@link ClientLoginModule} functionality
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientLoginModuleTest extends TestCaseBase {

    /**
     * Test login success & failures.
     * @throws Exception if there was failure
     */
    @Test
    public void loginTest() throws Exception {
        setLevel(ClientLoginModule.class.getName(), Level.INFO);
        //test failure conditions
        attemptLogin(null, getTestPassword(),
                AccountNotFoundException.class,
                Messages.EMPTY_USERNAME.getText());
        attemptLogin("", getTestPassword(),
                AccountNotFoundException.class,
                Messages.EMPTY_USERNAME.getText());
        final String u = randomString();
        attemptLogin(u, getTestPassword(),
                FailedLoginException.class,
                Messages.USER_LOGIN_FAIL.getText(u));
        assertLastEvent(Level.WARN, ClientLoginModule.class.getName(),
                Messages.USER_LOGIN_ERROR_LOG.getText(u),
                ClientLoginModule.class.getName());

        attemptLogin(getTestUsername(), null,
                FailedLoginException.class,
                Messages.USER_LOGIN_FAIL.getText(getTestUsername()));
        assertLastEvent(Level.WARN, ClientLoginModule.class.getName(),
                Messages.USER_LOGIN_ERROR_LOG.getText(getTestUsername()),
                ClientLoginModule.class.getName());

        attemptLogin(getTestUsername(), "".toCharArray(),
                FailedLoginException.class,
                Messages.USER_LOGIN_FAIL.getText(getTestUsername()));
        assertLastEvent(Level.WARN, ClientLoginModule.class.getName(),
                Messages.USER_LOGIN_ERROR_LOG.getText(getTestUsername()),
                ClientLoginModule.class.getName());

        attemptLogin(getTestUsername(), randomString().toCharArray(),
                FailedLoginException.class,
                Messages.USER_LOGIN_FAIL.getText(getTestUsername()));
        assertLastEvent(Level.WARN, ClientLoginModule.class.getName(),
                Messages.USER_LOGIN_ERROR_LOG.getText(getTestUsername()),
                ClientLoginModule.class.getName());
        //test failure due to client error
        I18NMessage0P fail = new I18NMessage0P(Messages.LOGGER, "testMessage");
        sMockHelper.setFail(fail);
        attemptLogin(getTestUsername(), getTestPassword(),
                FailedLoginException.class,
                Messages.USER_LOGIN_ERROR.getText());
        assertLastEvent(Level.WARN, ClientLoginModule.class.getName(),
                Messages.USER_LOGIN_ERROR_LOG.getText(getTestUsername()),
                ClientLoginModule.class.getName());

        //test successful login
        sMockHelper.setFail(null);
        attemptLogin(getTestUsername(), getTestPassword(), null, null);
        assertLastEvent(Level.INFO, ClientLoginModule.class.getName(),
                Messages.USER_LOGIN_LOG.getText(getTestUsername()),
                ClientLoginModule.class.getName());

        // test logout removes the principal from the subject
        loginContext.logout();
        assertTrue(loginContext.getSubject().getPrincipals().isEmpty());
        assertLastEvent(Level.INFO, ClientLoginModule.class.getName(),
                Messages.USER_LOGOUT_LOG.getText(getTestUsername()),
                ClientLoginModule.class.getName());
    }

    /**
     * test unsupported callbacks
     * @throws Exception if there was failure
     */
    @Test
    public void unsupportedCallback() throws Exception {
        doNotHandleCallbacks = true;
        UnsupportedCallbackException uce = new UnsupportedCallbackException(
                new NameCallback(Messages.PROMPT_USERNAME.getText()));
        LoginException ex = attemptLogin(getTestUsername(), getTestPassword(),
                LoginException.class, uce.getMessage());
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof UnsupportedCallbackException);
        Callback callback = ((UnsupportedCallbackException) ex.getCause()).getCallback();
        assertNotNull(callback);
        assertTrue(callback.getClass().toString(),
                callback instanceof NameCallback);
        org.junit.Assert.assertEquals(Messages.PROMPT_USERNAME.getText(),
                ((NameCallback)callback).getPrompt());
    }

    /**
     * test callback io failure
     * @throws Exception if there was a failure
     */
    @Test
    public void callbackIOFailure() throws Exception {
        callbackException = new IOException("ioeoeoe"); //$NON-NLS-1$
        LoginException ex = attemptLogin(getTestUsername(), getTestPassword(),
                LoginException.class, callbackException.getMessage());
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof IOException);
        assertSame(callbackException, ex.getCause());
    }

    /**
     * Verify that the local helper cannot be set to null.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void nullHelperFail() throws Exception {
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                ClientLoginHelper.setCurrentHelper(null);
            }
        };
    }

    @BeforeClass
    public static void setup() throws Exception {
        LoggerConfiguration.logSetup();
        //Set up our configuration.
        JaasConfiguration.setup();
        //Override login helper to help with unit testing
        sMockHelper = new MockLoginHelper();
        ClientLoginHelper.setCurrentHelper(sMockHelper);
    }

    @AfterClass
    public static void cleanup() throws Exception {
        ClientLoginHelper.setCurrentHelper(ClientLoginHelper.DEFAULT_HELPER);
    }
    @After
    public void reset() throws Exception {
        doNotHandleCallbacks = false;
        callbackException = null;
        sMockHelper.setFail(null);
    }

    private static String getTestUsername() {
        return sMockHelper.getTestUsername();
    }

    private static char[] getTestPassword() {
        return sMockHelper.getTestPassword();
    }

    private String randomString() {
        return RandomStrings.genStrLetter();
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
            loginContext = new LoginContext(
                    JaasConfiguration.REMOTING_LOGIN_DOMAIN,ch);
            loginContext.login();
            assertNull("Expected failure:" + failure + failureMsg, failure);
            //verify that the appropriate principals are set in the subject
            assertTrue(loginContext.getSubject().getPrincipals().toString(),
                    loginContext.getSubject().getPrincipals().contains(
                            new UserPrincipal(getTestUsername())));
        } catch (LoginException e) {
            assertNotNull("Unexpected failure:" + e,failure);
            assertTrue("Expected:" + failure + ":Actual:" +
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
                            new UserPrincipal(getTestUsername())));
            }
            assertEquals(2,ch.getNumCallbacks());
            //These values are only set if call back handler doesn't throw
            //exceptions
            if (callbackException == null && !doNotHandleCallbacks) {
                assertEquals(Messages.PROMPT_USERNAME.getText(), ch.getNamePrompt());
                assertEquals(Messages.PROMPT_PASSWORD.getText(), ch.getPasswordPrompt());
                assertNull(ch.getDefaultName());
            }
            return e;
        }
        return null;
    }
    private static boolean doNotHandleCallbacks = false;
    private static IOException callbackException = null;
    private LoginContext loginContext;

    private static MockLoginHelper sMockHelper;

    /**
     * A mock login helper to aid unit testing.
     */
    private static class MockLoginHelper extends ClientLoginHelper {
        @Override
        protected boolean validateCredentials(String inUsername,
                                              char[] inPassword)
                throws ClientInitException {
            if(mFail != null) {
                throw new ClientInitException(mFail);
            }
            return ObjectUtils.equals(inUsername, mUsername) &&
                    Arrays.equals(inPassword, mPassword);
        }

        public String getTestUsername() {
            return mUsername;
        }

        public char[] getTestPassword() {
            return mPassword;
        }

        public void setFail(I18NBoundMessage inFailMessage) {
            mFail = inFailMessage;
        }

        private I18NBoundMessage mFail = null;
        private final String mUsername = "testuser";
        private final char[] mPassword = "testpassword".toCharArray();
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
}