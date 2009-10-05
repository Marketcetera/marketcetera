package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.*;

/* $License$ */
/**
 * Specifies the parameters to create a strategy.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@XmlAccessorType(XmlAccessType.FIELD)
public final class CreateStrategyParameters implements Serializable {

    /**
     * Creates an instance.
     *
     * @param inInstanceName the strategy instance name. Can be null.
     * @param inStrategyName the strategy class name. Cannot be null.
     * @param inLanguage the strategy language. Cannot be null.
     * @param inStrategySource the file containing the strategy source. Cannot be null.
     * @param inParameters the strategy parameters as a list of ':' separated,
     * name=value pairs. Can be null.
     * @param inRouteOrdersToServer if the strategy should route its orders
     * to the server.
     * 
     * @throws NullPointerException if any of the non-null field values are null.
     * @throws FileNotFoundException if the <code>inStrategySource</code> file
     * does not exist or if it cannot be read.
     */
    public CreateStrategyParameters(String inInstanceName,
                              String inStrategyName,
                              String inLanguage,
                              File inStrategySource,
                              String inParameters,
                              boolean inRouteOrdersToServer)
            throws FileNotFoundException {
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
        mStrategySource = new DataHandler(new FileDataSource(inStrategySource));
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
    public InputStream getStrategySource() throws IOException {
        return mStrategySource.getInputStream();
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
    /**
     * This constructor has been added for JAXB and is not meant to be
     * used by clients.
     */
    private CreateStrategyParameters() {
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
    @XmlMimeType("application/octet-stream")
    private final DataHandler mStrategySource;
    private static final long serialVersionUID = 1L;
}
