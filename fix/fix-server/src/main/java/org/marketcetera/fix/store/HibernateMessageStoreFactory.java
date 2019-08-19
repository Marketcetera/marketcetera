package org.marketcetera.fix.store;

import java.io.IOException;

import quickfix.MessageStore;
import quickfix.MessageStoreFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Creates {@link HibernateMessageStore} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class HibernateMessageStoreFactory
        implements MessageStoreFactory
{
    /**
     * Create a new HibernateMessageStoreFactory instance.
     *
     * @param inSessionSettings a <code>SessionSettings</code> value
     */
    public HibernateMessageStoreFactory(SessionSettings inSessionSettings)
    {
        sessionSettings = inSessionSettings;
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStoreFactory#create(quickfix.SessionID)
     */
    @Override
    public MessageStore create(SessionID inSessionId)
    {
        try {
            return new HibernateMessageStore(inSessionId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * session settings for all sessions
     */
    @SuppressWarnings("unused")
    private final SessionSettings sessionSettings;
}
