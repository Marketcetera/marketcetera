package org.marketcetera.quickfix;

import quickfix.MessageStore;
import quickfix.MessageStoreFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Creates new {@link NullMessageStore} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NullMessageStoreFactory
        implements MessageStoreFactory
{
    /* (non-Javadoc)
     * @see quickfix.MessageStoreFactory#create(quickfix.SessionID)
     */
    @Override
    public MessageStore create(SessionID inSessionId)
    {
        return new NullMessageStore(inSessionId);
    }
    /**
     * Create a new NullMessageStoreFactory instance.
     *
     * @param inSessionSettings a <code>SessionSettings</code> value
     */
    public NullMessageStoreFactory(SessionSettings inSessionSettings)
    {
        sessionSettings = inSessionSettings;
    }
    /**
     * Get the sessionSettings value.
     *
     * @return a <code>SessionSettings</code> value
     */
    public SessionSettings getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * session settings value
     */
    private final SessionSettings sessionSettings;
}
