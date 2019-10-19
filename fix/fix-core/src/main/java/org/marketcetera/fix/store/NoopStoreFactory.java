package org.marketcetera.fix.store;

/* $License$ */

/**
 * Creates {@link quickfix.NoopStore} Quickfix/J message stores
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NoopStoreFactory
        implements quickfix.MessageStoreFactory
{
    /* (non-Javadoc)
     * @see quickfix.MessageStoreFactory#create(quickfix.SessionID)
     */
    @Override
    public quickfix.MessageStore create(quickfix.SessionID inSessionID)
    {
        return new quickfix.NoopStore();
    }
    /**
     * Create a new NoopStoreFactory instance.
     *
     * @param inSettings a <code>quickfix.SessionSettings</code> value
     */
    public NoopStoreFactory(quickfix.SessionSettings inSettings)
    {
    }
}
