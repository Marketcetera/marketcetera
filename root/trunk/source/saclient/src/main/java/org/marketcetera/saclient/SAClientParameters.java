package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.apache.commons.lang.ObjectUtils;

import java.beans.ConstructorProperties;
import java.util.Arrays;

/* $License$ */
/**
 * The set of parameters needed to initialize the client connection
 * to the strategy agent. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 * @see org.marketcetera.saclient.SAClientFactory#create(SAClientParameters)
 */
@ClassVersion("$Id$")
public class SAClientParameters {
    /**
     * Gets the user name to use when connecting to the strategy agent.
     *
     * @return the user name.
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * The password to use when connecting to the strategy agent.
     *
     * @return the password.
     */
    public char[] getPassword() {
        return mPassword == null
                ? null
                : Arrays.copyOf(mPassword, mPassword.length);
    }

    /**
     * The URL of the strategy agent.
     *
     * @return the URL of the strategy agent.
     */
    public String getURL() {
        return mURL;
    }

    /**
     * The port number of the strategy agent.
     *
     * @return the strategy agent port number.
     */
    public int getPort() {
        return mPort;
    }

    /**
     * The strategy agent hostname.
     *
     * @return the strategy agent hostname.
     */
    public String getHostname() {
        return mHostname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SAClientParameters that = (SAClientParameters) o;

        return ObjectUtils.equals(mPort, that.mPort)&&
                Arrays.equals(mPassword, that.mPassword) &&
                ObjectUtils.equals(mURL, that.mURL) &&
                ObjectUtils.equals(mUsername, that.mUsername) &&
                ObjectUtils.equals(mHostname, that.mHostname);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(mUsername) +
                Arrays.hashCode(mPassword) +
                ObjectUtils.hashCode(mHostname) +
                ObjectUtils.hashCode(mPort) +
                ObjectUtils.hashCode(mURL);
    }

    /**
     * Creates an instance.
     *
     * @param inUsername the user name.
     * @param inPassword the password.
     * @param inURL the URL.
     * @param inHostname the host name
     * @param inPort the port number
     */
    @ConstructorProperties(
            {"username",
            "password",
            "URL",
            "hostname",
            "port"})
    public SAClientParameters(String inUsername, char[] inPassword,
                              String inURL, String inHostname, int inPort) {
        mUsername = inUsername;
        mPassword = inPassword == null
                ? null
                : Arrays.copyOf(inPassword, inPassword.length);
        mURL = inURL;
        mHostname = inHostname;
        mPort = inPort;
    }


    @Override
    public String toString() {
        return "ClientParameters{" +  //$NON-NLS-1$
                "Username='" + mUsername + '\'' +  //$NON-NLS-1$ $NON-NLS-2$
                ", Password='*****'" +  //$NON-NLS-1$
                ", URL='" + mURL + '\'' +  //$NON-NLS-1$  $NON-NLS-2$
                ", Hostname='" + mHostname + '\'' +  //$NON-NLS-1$ $NON-NLS-2$
                ", Port='" + mPort + '\'' +  //$NON-NLS-1$ $NON-NLS-2$
                '}';  //$NON-NLS-1$
    }

    private final String mUsername;
    private final char[] mPassword;
    private final String mHostname;
    private final int mPort;
    private final String mURL;
}