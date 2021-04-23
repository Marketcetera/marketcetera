package org.marketcetera.fix;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that an incoming or outgoing message was intercepted and should not be processed any further.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MessageIntercepted
        extends CoreException
{
    /**
     * Create a new MessageIntercepted instance.
     */
    public MessageIntercepted()
    {
        super();
    }
    /**
     * Create a new MessageIntercepted instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public MessageIntercepted(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new MessageIntercepted instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MessageIntercepted(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new MessageIntercepted instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MessageIntercepted(Throwable inNested,
                              I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = -5703845005842514423L;
}
