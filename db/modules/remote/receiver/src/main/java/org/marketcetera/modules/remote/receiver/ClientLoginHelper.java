package org.marketcetera.modules.remote.receiver;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientInitException;

import java.util.UUID;
import java.util.Arrays;

/* $License$ */
/**
 * A class that helps in authenticating when creating a local connection
 * to the broker using the VM connector.
 * <p>
 * This class generates a random user-name / password that is supplied
 * by the {@link ReceiverModule module} and used by {@link ClientLoginModule}.
 * <p>
 * This class also helps unit test the {@link ClientLoginModule}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
abstract class ClientLoginHelper {

    /**
     * Returns true if the supplied credentials are valid. This method should
     * be invoked by the clients of this class to authenticate.
     * <p>
     * The supplied credentials are valid if they match the attributes of
     * this class or if they match the client's credentials.
     *
     * @param inUsername the username.
     * @param inPassword the password.
     *
     * @return if the supplied credentials are valid, false if they are not.
     *
     * @throws ClientInitException if the client has not been initialized.
     */
    public static boolean isValidCredentials(String inUsername,
                                      char[] inPassword)
            throws ClientInitException {
        return (getUserName().equals(inUsername) &&
                    Arrays.equals(getPassword().toCharArray(), inPassword)) ||
                mCurrentHelper.validateCredentials(inUsername, inPassword);
    }
    /**
     * Get the internal user name to use for logging into the embedded
     * jms broker.
     *
     * @return the internal user name.
     */
    static String getUserName() {
        return sUsername;
    }

    /**
     * Get the internal user's password to use for logging into the embedded
     * jms broker.
     *
     * @return the internal user's password.
     */
    static String getPassword() {
        return sPassword;
    }

    /**
     * Sets the current helper. This method is only meant to be used for
     * unit testing.
     *
     * @param inHelper the current helper instance. cannot be null.
     */
    static void setCurrentHelper(ClientLoginHelper inHelper) {
        if(inHelper == null) {
            throw new NullPointerException();
        }
        mCurrentHelper = inHelper;
    }

    /**
     * This method is over-ridden by subclasses to carry out the actual
     * authentication.
     *
     * @param inUsername the username.
     * @param inPassword the password.
     *
     * @return if the supplied credentials are valid.
     *
     * @throws ClientInitException if the client has not been initialized.
     */
    protected abstract boolean validateCredentials(String inUsername,
                                                   char[] inPassword)
            throws ClientInitException ;

    /**
     * The default login helper that is used to authenticate credentials.
     */
    static final ClientLoginHelper DEFAULT_HELPER = new DefaultHelper();
    
    private static ClientLoginHelper mCurrentHelper = DEFAULT_HELPER;
    private static String sUsername = "internal-" + UUID.randomUUID().toString();  //$NON-NLS-1$
    private static String sPassword = UUID.randomUUID().toString();

    /**
     * The default helper that is used for authenticating the supplied
     * credentials with the client via
     * {@link org.marketcetera.client.Client#isCredentialsMatch(String, char[])}. 
     */
    private static class DefaultHelper extends ClientLoginHelper {
        /**
         * Returns true if the supplied credentials are valid.
         * <p>
         * The supplied credentials are valid if they match the attributes of
         * this class or if they match the client's credentials.
         *
         * @param inUsername the username.
         * @param inPassword the password.
         *
         * @return if the supplied credentials are valid, false if they are not.
         *
         * @throws ClientInitException if the client has not been initialized.
         */
        @Override
        protected boolean validateCredentials(String inUsername,
                                              char[] inPassword)
                throws ClientInitException {
            return ClientManager.getInstance().isCredentialsMatch(
                    inUsername, inPassword);
        }
    }
}
