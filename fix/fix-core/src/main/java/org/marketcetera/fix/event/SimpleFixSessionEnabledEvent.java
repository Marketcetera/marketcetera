package org.marketcetera.fix.event;

import quickfix.SessionID;

/* $License$ */

/**
 * Indicates that a FIX session has been enabled.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleFixSessionEnabledEvent
        extends AbstractFixSessionActionEvent
        implements FixSessionEnabledEvent
{
    /**
     * Create a new SimpleFixSessionEnabledEvent instance.
     *
     * @param inSessionId a <code>SessionID</code> value
     */
    public SimpleFixSessionEnabledEvent(SessionID inSessionId)
    {
        super(inSessionId);
    }
}
