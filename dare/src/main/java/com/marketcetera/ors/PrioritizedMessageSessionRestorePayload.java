package com.marketcetera.ors;

import quickfix.Message;

import com.marketcetera.fix.SessionRestorePayload;

/* $License$ */

/**
 * Provides a <code>SessionRestorePayload</code> implementation that allows a message to be delivered with a priority.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PrioritizedMessageSessionRestorePayload
        implements SessionRestorePayload
{
    /**
     * Create a new PrioritizedMessageSessionRestorePayload instance.
     *
     * @param inMessage a <code>Message</code> value
     * @param inPriority an <code>int</code> value
     */
    public PrioritizedMessageSessionRestorePayload(Message inMessage,
                                                   int inPriority)
    {
        message = inMessage;
        priority = inPriority;
    }
    /**
     * Get the message value.
     *
     * @return a <code>Message</code> value
     */
    public Message getMessage()
    {
        return message;
    }
    /**
     * Get the priority value.
     *
     * @return an <code>int</code> value
     */
    public int getPriority()
    {
        return priority;
    }
    /**
     * message value
     */
    private final Message message;
    /**
     * priority value
     */
    private final int priority;
}
