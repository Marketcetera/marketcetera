package org.marketcetera.quickfix;

import quickfix.Log;

/* $License$ */

/**
 * Provides a {@link Log} implementation that does nothing.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NullLog
        implements Log
{
    /* (non-Javadoc)
     * @see quickfix.Log#clear()
     */
    @Override
    public void clear()
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onIncoming(java.lang.String)
     */
    @Override
    public void onIncoming(String inMessage)
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onOutgoing(java.lang.String)
     */
    @Override
    public void onOutgoing(String inMessage)
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onEvent(java.lang.String)
     */
    @Override
    public void onEvent(String inText)
    {
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onErrorEvent(java.lang.String)
     */
    @Override
    public void onErrorEvent(String inText)
    {
    }
}
