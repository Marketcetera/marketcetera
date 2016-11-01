package org.marketcetera.quickfix;

import quickfix.Log;
import quickfix.LogFactory;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides a <code>LogFactory</code> implementation that does nothing.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NullLogFactory
        implements LogFactory
{
    /* (non-Javadoc)
     * @see quickfix.LogFactory#create()
     */
    @Override
    public Log create()
    {
        return new NullLog();
    }
    /* (non-Javadoc)
     * @see quickfix.LogFactory#create(quickfix.SessionID)
     */
    @Override
    public Log create(SessionID inSessionID)
    {
        return new NullLog();
    }
}
