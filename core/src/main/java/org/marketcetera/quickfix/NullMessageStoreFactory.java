package org.marketcetera.quickfix;

import java.io.IOException;

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
        try {
            return new NullMessageStore(inSessionId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
