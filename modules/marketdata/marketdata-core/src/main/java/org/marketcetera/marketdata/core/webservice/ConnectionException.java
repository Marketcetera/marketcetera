package org.marketcetera.marketdata.core.webservice;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates an error occurred trying to connect to a remote source.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class ConnectionException
        extends CoreException
{
    /**
     * Create a new ConnectionException instance.
     */
    public ConnectionException()
    {
    }
    /**
     * Create a new ConnectionException instance.
     *
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     */
    public ConnectionException(String inHostname,
                               int inPort)
    {
        hostname = inHostname;
        port = inPort;
    }
    /**
     * Create a new ConnectionException instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public ConnectionException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new ConnectionException instance.
     *
     * @param inMessage an <code>I18nBoundMessage</code> value
     */
    public ConnectionException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new ConnectionException instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18nBoundMessage</code> value
     */
    public ConnectionException(Throwable inNested,
                               I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    /**
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    public String getHostname()
    {
        return hostname;
    }
    /**
     * Sets the hostname value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * hostname value
     */
    private String hostname;
    /**
     * port value
     */
    private int port;
    private static final long serialVersionUID = 8121822556635716771L;
}
