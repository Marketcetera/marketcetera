package org.marketcetera.web.service;

/* $License$ */

/**
 * Indicates that no service is available.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NoServiceException
        extends RuntimeException
{
    /**
     * Create a new NoServiceException instance.
     */
    public NoServiceException()
    {
        super();
    }
    /**
     * Create a new NoServiceException instance.
     *
     * @param inMessage a <code>String</code> value
     */
    public NoServiceException(String inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new NoServiceException instance.
     *
     * @param inCause a <code>Throwable</code> value
     */
    public NoServiceException(Throwable inCause)
    {
        super(inCause);
    }
    /**
     * Create a new NoServiceException instance.
     *
     * @param inMessage a <code>String</code> value
     * @param inCause a <code>Throwable</code> value
     */
    public NoServiceException(String inMessage,
                              Throwable inCause)
    {
        super(inMessage,
              inCause);
    }
    /**
     * Create a new NoServiceException instance.
     *
     * @param inMessage a <code>String</code> value
     * @param inCause a <code>Throwable</code> value
     * @param inEnableSuppression a <code>boolean</code> value
     * @param inWritableStackTrace a <code>boolean</code> value
     */
    public NoServiceException(String inMessage,
                              Throwable inCause,
                              boolean inEnableSuppression,
                              boolean inWritableStackTrace)
    {
        super(inMessage,
              inCause,
              inEnableSuppression,
              inWritableStackTrace);
    }
    private static final long serialVersionUID = -7657091217738295482L;
}
