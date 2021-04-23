package org.marketcetera.trade.event;

/* $License$ */

/**
 * Provides a POJO {@link IncomingMessageIntercetedEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleIncomingMessageInterceptedEvent
        extends AbstractMessageInterceptedEvent
        implements IncomingMessageInterceptedEvent
{
    /**
     * Create a new SimpleIncomingMessageInterceptedEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     */
    public SimpleIncomingMessageInterceptedEvent(quickfix.SessionID inSessionId,
                                                 quickfix.Message inMessage)
    {
        super(inSessionId,
              inMessage);
    }
}
