package org.marketcetera.fix.event;

import quickfix.SessionID;

/* $License$ */

/**
 * Indicates that a FIX session has been disabled.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleFixSessionDisabledEvent
        extends AbstractFixSessionActionEvent
        implements FixSessionDisabledEvent
{
    /**
     * Create a new SimpleFixSessionDisabledEvent instance.
     *
     * @param inSessionId a <code>SessionID</code> value
     */
    public SimpleFixSessionDisabledEvent(SessionID inSessionId)
    {
        super(inSessionId);
    }
}
