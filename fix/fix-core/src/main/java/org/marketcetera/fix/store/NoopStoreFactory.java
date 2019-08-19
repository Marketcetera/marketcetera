package org.marketcetera.fix.store;

import quickfix.MessageStore;
import quickfix.MessageStoreFactory;
import quickfix.NoopStore;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Creates {@link NoopStore} Quickfix/J message stores
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NoopStoreFactory
        implements MessageStoreFactory
{
    /* (non-Javadoc)
     * @see quickfix.MessageStoreFactory#create(quickfix.SessionID)
     */
    @Override
    public MessageStore create(SessionID inSessionID)
    {
        return new NoopStore();
    }
    /**
     * Create a new NoopStoreFactory instance.
     *
     * @param inSettings a <code>SessionSettings</code> value
     */
    public NoopStoreFactory(SessionSettings inSettings)
    {
    }
}
