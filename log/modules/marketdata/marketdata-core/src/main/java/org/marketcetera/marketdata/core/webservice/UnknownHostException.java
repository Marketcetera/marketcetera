package org.marketcetera.marketdata.core.webservice;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UnknownHostException
        extends CoreException
{
    /**
     * Create a new UnknownHostException instance.
     */
    public UnknownHostException() {}
    /**
     * Create a new UnknownHostException instance.
     *
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     */
    public UnknownHostException(String inHostname,
                                int inPort)
    {
        hostname = inHostname;
    }
    /**
     * Create a new UnknownHostException instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public UnknownHostException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new UnknownHostException instance.
     *
     * @param inMessage an <code>I18BoundMessage</code> value
     */
    public UnknownHostException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new UnknownHostException instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18BoundMessage</code> value
     */
    public UnknownHostException(Throwable inNested,
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
    private static final long serialVersionUID = 1L;
}
