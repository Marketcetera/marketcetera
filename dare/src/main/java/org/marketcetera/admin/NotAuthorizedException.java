package org.marketcetera.admin;

/* $License$ */

/**
 * Indicates an authorization failure.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: NotAuthorizedException.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
public class NotAuthorizedException
        extends RuntimeException
{
    /**
     * Create a new NotAuthorizedException instance.
     */
    public NotAuthorizedException()
    {
    }
    /**
     * Create a new NotAuthorizedException instance.
     *
     * @param inMessage a <code>String</code> value
     */
    public NotAuthorizedException(String inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new NotAuthorizedException instance.
     *
     * @param inCause a <code>Throwable</code> value
     */
    public NotAuthorizedException(Throwable inCause)
    {
        super(inCause);
    }
    /**
     * Create a new NotAuthorizedException instance.
     *
     * @param inMessage a <code>String</code> value
     * @param inCause a <code>Throwable</code> value
     */
    public NotAuthorizedException(String inMessage,
                                  Throwable inCause)
    {
        super(inMessage,
              inCause);
    }
    /**
     * Create a new NotAuthorizedException instance.
     *
     * @param inMessage a <code>String</code> value
     * @param inCause a <code>Throwable</code> value
     * @param inEnableSuppression a <code>boolean</code> value
     * @param inWritableStackTrace a <code>boolean</code> value
     */
    public NotAuthorizedException(String inMessage,
                                  Throwable inCause,
                                  boolean inEnableSuppression,
                                  boolean inWritableStackTrace)
    {
        super(inMessage,
              inCause,
              inEnableSuppression,
              inWritableStackTrace);
    }
    private static final long serialVersionUID = -4546731506980014212L;
}
