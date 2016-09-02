package org.marketcetera.saclient;

import java.beans.ConstructorProperties;

import org.marketcetera.core.BaseClientContextualParameters;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */
/**
 * The set of parameters needed to initialize the client connection
 * to the strategy agent. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 * @see org.marketcetera.saclient.SEClientFactoryImpl#create(SEClientParameters)
 */
@ClassVersion("$Id$")
public class SEClientParameters
        extends BaseClientContextualParameters
{
    /**
     * The URL of the strategy agent.
     *
     * @return the URL of the strategy agent.
     */
    public String getURL() {
        return mURL;
    }
    /**
     * Get the useJms value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getUseJms()
    {
        return useJms;
    }
    /**
     * Sets the useJms value.
     *
     * @param inUseJms a <code>boolean</code> value
     */
    public void setUseJms(boolean inUseJms)
    {
        useJms = inUseJms;
    }
    /**
     * Sets the uRL value.
     *
     * @param inURL a <code>String</code> value
     */
    public void setURL(String inURL)
    {
        mURL = inURL;
    }
    /**
     * Create a new SAClientParameters instance.
     */
    public SEClientParameters() {}
    /**
     * Creates an instance.
     *
     * @param inUsername the user name.
     * @param inPassword the password.
     * @param inURL the URL.
     * @param inHostname the host name
     * @param inPort the port number
     */
    @ConstructorProperties({ "username","password","URL","hostname","port" })
    public SEClientParameters(String inUsername,
                              String inPassword,
                              String inURL,
                              String inHostname,
                              int inPort)
    {
        this(inUsername,
             inPassword,
             inURL,
             inHostname,
             inPort,
             null);
    }
    /**
     * Create a new SAClientParameters instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inURL a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param contextClassProvider a <code>ContextClassProvider</code> value
     */
    @ConstructorProperties({ "username","password","URL","hostname","port","contextClasses" })
    public SEClientParameters(String inUsername,
                              String inPassword,
                              String inURL,
                              String inHostname,
                              int inPort,
                              ContextClassProvider inContextClassProvider)
    {
        this(inUsername,
             inPassword,
             inURL,
             inHostname,
             inPort,
             inContextClassProvider,
             true);
    }
    /**
     * Create a new SAClientParameters instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inURL a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     * @param inUseJms a <code>boolean</code> value
     */
    @ConstructorProperties({ "username","password","URL","hostname","port","contextClasses","useJms" })
    public SEClientParameters(String inUsername,
                              String inPassword,
                              String inURL,
                              String inHostname,
                              int inPort,
                              ContextClassProvider inContextClassProvider,
                              boolean inUseJms)
    {
        setUsername(inUsername);
        setPassword(inPassword);
        mURL = inURL;
        setHostname(inHostname);
        setPort(inPort);
        setContextClassProvider(inContextClassProvider);
        useJms = inUseJms;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SEClientParameters [URL=").append(mURL).append(", hostname=").append(getHostname())
                .append(", port=").append(getPort()).append(", username()=").append(getUsername()).append("]");
        return builder.toString();
    }
    /**
     * indicates whether to use JMS or not
     */
    private boolean useJms;
    /**
     * JMS URL to use to connect
     */
    private String mURL;
}
