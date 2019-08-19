package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates an order could not be sent.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SendOrderFailed
        extends RuntimeException
{
    /**
     * Create a new SendOrderFailed instance.
     */
    public SendOrderFailed()
    {
        super();
    }
    /**
     * Create a new SendOrderFailed instance.
     *
     * @param inMessage a <code>String</code> value
     */
    public SendOrderFailed(String inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new SendOrderFailed instance.
     *
     * @param inCause a <code>Throwable</code> value
     */
    public SendOrderFailed(Throwable inCause)
    {
        super(inCause);
    }
    /**
     * Create a new SendOrderFailed instance.
     *
     * @param inMessage a <code>String</code> value
     * @param inCause a <code>Throwable</code> value
     */
    public SendOrderFailed(String inMessage,
                           Throwable inCause)
    {
        super(inMessage,
              inCause);
    }
    /**
     * Create a new SendOrderFailed instance.
     *
     * @param inMessage a <code>String</code> value
     * @param inCause a <code>Throwable</code> value
     * @param inEnableSuppression a <code>boolean</code> value
     * @param inWritableStackTrace a <code>boolean</code> value
     */
    public SendOrderFailed(String inMessage,
            Throwable inCause,
            boolean inEnableSuppression,
            boolean inWritableStackTrace)
    {
        super(inMessage,
              inCause,
              inEnableSuppression,
              inWritableStackTrace);
    }
    private static final long serialVersionUID = 4740841884155879579L;
}
