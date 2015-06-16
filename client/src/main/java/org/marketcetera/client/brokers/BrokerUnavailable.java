package org.marketcetera.client.brokers;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that a desired broker is unavailable.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BrokerUnavailable
        extends CoreException
{
    /**
     * Create a new BrokerUnavailable instance.
     */
    public BrokerUnavailable()
    {
    }
    /**
     * Create a new BrokerUnavailable instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public BrokerUnavailable(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new BrokerUnavailable instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public BrokerUnavailable(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new BrokerUnavailable instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public BrokerUnavailable(Throwable inNested,
                             I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = -7520912372924607229L;
}
