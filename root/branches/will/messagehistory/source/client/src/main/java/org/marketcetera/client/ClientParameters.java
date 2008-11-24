package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.apache.commons.lang.ObjectUtils;

import java.beans.ConstructorProperties;
import java.util.Arrays;

/* $License$ */
/**
 * The set of parameters needed to initialize the client. These parameters
 * are provided to the following methods.
 * <ul>
 *      <li>{@link ClientManager#init(ClientParameters)}</li>
 *      <li>{@link Client#reconnect(ClientParameters)}</li>
 * </ul>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientParameters {
    /**
     * Gets the user name to use when connecting to the server.
     *
     * @return the user name.
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * The password to use when connecting to the server.
     *
     * @return the password.
     */
    public char[] getPassword() {
        return mPassword;
    }

    /**
     * The URL of the server.
     *
     * @return the URL of the server.
     */
    public String getURL() {
        return mURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientParameters that = (ClientParameters) o;

        return Arrays.equals(mPassword, that.mPassword) &&
                ObjectUtils.equals(mURL, that.mURL) &&
                ObjectUtils.equals(mUsername, that.mUsername);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(mUsername) +
                ObjectUtils.hashCode(mPassword) +
                ObjectUtils.hashCode(mURL);
    }

    /**
     * Creates an instance.
     *
     * @param inUsername the username.
     * @param inPassword the password.
     * @param inURL the URL.
     */
    @ConstructorProperties({"username", "password", "URL"})  //$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
    public ClientParameters(String inUsername, char[] inPassword,
                            String inURL) {
        mUsername = inUsername;
        mPassword = inPassword;
        mURL = inURL;
    }

    public String toString() {
        return "ClientParameters{" +  //$NON-NLS-1$
                "Username='" + mUsername + '\'' +  //$NON-NLS-1$ $NON-NLS-2$
                ", Password='*****'" +  //$NON-NLS-1$
                ", URL='" + mURL + '\'' +  //$NON-NLS-1$  $NON-NLS-2$
                '}';  //$NON-NLS-1$
    }

    private String mUsername;
    private char[] mPassword;
    private String mURL;
}
