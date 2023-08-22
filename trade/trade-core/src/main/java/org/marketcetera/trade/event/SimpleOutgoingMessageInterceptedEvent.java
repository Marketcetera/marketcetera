package org.marketcetera.trade.event;

/* $License$ */

/**
 * Provides a POJO {@link OutgoingMessageInterceptedEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleOutgoingMessageInterceptedEvent
        extends AbstractMessageInterceptedEvent
        implements OutgoingMessageInterceptedEvent
{
    /**
     * Create a new SimpleOutgoingMessageInterceptedEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     */
    public SimpleOutgoingMessageInterceptedEvent(quickfix.SessionID inSessionId,
                                                 quickfix.Message inMessage)
    {
        super(inSessionId,
              inMessage);
    }
}
