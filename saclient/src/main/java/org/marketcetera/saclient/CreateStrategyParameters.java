package org.marketcetera.saclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Specifies the parameters to create a strategy.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="createStrategyParameters")
@ClassVersion("$Id$")
public final class CreateStrategyParameters
        implements Serializable
{
    /**
     * Creates an instance.
     *
     * @param inInstanceName the strategy instance name. Can be null.
     * @param inStrategyName the strategy class name. Cannot be null.
     * @param inLanguage the strategy language. Cannot be null.
     * @param inStrategySource the file containing the strategy source. Cannot be null.
     * @param inParameters the strategy parameters as a list of ':' separated,
     * name=value pairs. Can be null.
     * @param inRouteOrdersToServer if the strategy should route its orders to the server.
     * @throws IOException if the strategy parameters cannot be created
     * @throws NullPointerException if any of the non-null field values are null.
     */
    public CreateStrategyParameters(String inInstanceName,
                                    String inStrategyName,
                                    String inLanguage,
                                    File inStrategySource,
                                    String inParameters,
                                    boolean inRouteOrdersToServer)
            throws IOException
    {
        if(inStrategyName == null) {
            throw new NullPointerException();
        }
        if(inLanguage == null) {
            throw new NullPointerException();
        }
        if(inStrategySource== null) {
            throw new NullPointerException();
        }
        if((!inStrategySource.isFile()) || (!inStrategySource.canRead())) {
            throw new FileNotFoundException(inStrategySource.getAbsolutePath());
        }

        mInstanceName = inInstanceName;
        mStrategyName = inStrategyName;
        mLanguage = inLanguage;
        mStrategySource = IOUtils.toString(new FileInputStream(inStrategySource),
                                           Charset.defaultCharset());
        mParameters = inParameters;
        mRouteOrdersToServer = inRouteOrdersToServer;
    }

    /**
     * Returns the strategy instance name.
     *
     * @return the strategy instance name.
     */
    public String getInstanceName() {
        return mInstanceName;
    }

    /**
     * Returns the strategy class name.
     *
     * @return the strategy class name.
     */
    public String getStrategyName() {
        return mStrategyName;
    }

    /**
     * Returns the strategy language.
     *
     * @return the strategy language.
     */
    public String getLanguage() {
        return mLanguage;
    }

    /**
     * Returns the input stream from which the strategy script file can be read.
     * <p>
     * The input stream should be closed once the script has been read
     * to release the resources.
     *
     * @return the input stream from which the strategy script can be read.
     *
     * @throws IOException if there were errors getting the input stream.
     */
    public InputStream getStrategySource()
            throws IOException
    {
        return IOUtils.toInputStream(mStrategySource,
                                     Charset.defaultCharset());
    }

    /**
     * Returns the strategy parameters.
     * <p>
     * The parameters are specified as ':' separated list of name=value pairs.
     *
     * @return the strategy parameters.
     */
    public String getParameters() {
        return mParameters;
    }

    /**
     * Returns true if the strategy should route its orders to the server.
     *
     * @return if the strategy should routes its orders to the server.
     */
    public boolean isRouteOrdersToServer() {
        return mRouteOrdersToServer;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("CreateStrategyParameters [mInstanceName=").append(mInstanceName).append(", mStrategyName=")
                .append(mStrategyName).append(", mLanguage=").append(mLanguage).append(", mParameters=")
                .append(mParameters).append(", mRouteOrdersToServer=").append(mRouteOrdersToServer)
                .append(", mStrategySource=").append(mStrategySource).append("]");
        return builder.toString();
    }
    /**
     * This constructor has been added for JAXB and is not meant to be
     * used by clients.
     */
    @SuppressWarnings("unused")
    private CreateStrategyParameters()
    {
        mInstanceName = null;
        mStrategyName = null;
        mLanguage = null;
        mStrategySource = null;
        mParameters = null;
        mRouteOrdersToServer = false;
    }

    private final String mInstanceName;
    private final String mStrategyName;
    private final String mLanguage;
    private final String mParameters;
    private final boolean mRouteOrdersToServer;
    private final String mStrategySource;
    private static final long serialVersionUID = -4937743911766362165L;
}
