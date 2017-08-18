package org.marketcetera.fix;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that an outgoing order was intercepted and should not be processed any further.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderIntercepted
        extends CoreException
{
    /**
     * Create a new OrderIntercepted instance.
     */
    public OrderIntercepted()
    {
        super();
    }
    /**
     * Create a new OrderIntercepted instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public OrderIntercepted(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new OrderIntercepted instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public OrderIntercepted(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new OrderIntercepted instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public OrderIntercepted(Throwable inNested,
                            I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = -5703845005842514423L;
}
