package org.marketcetera.event.beans;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.util.QuoteAction;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link QuoteEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public final class QuoteBean
        extends MarketDataBean
{
    /**
     * Get the action value.
     *
     * @return a <code>QuoteAction</code> value
     */
    public QuoteAction getAction()
    {
        return action;
    }
    /**
     * Sets the action value.
     *
     * @param a <code>QuoteAction</code> value
     */
    public void setAction(QuoteAction inAction)
    {
        action = inAction;
    }
    /**
     * the action of the quote
     */
    private volatile QuoteAction action;
    private static final long serialVersionUID = 1L;
}
