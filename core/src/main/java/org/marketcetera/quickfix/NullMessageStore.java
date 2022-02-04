package org.marketcetera.quickfix;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import quickfix.MemoryStore;
import quickfix.MessageStore;
import quickfix.SessionID;

/**
 * Provides a no-op {@link MessageStore} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NullMessageStore
        implements MessageStore
{
    /**
     * Create a new NullMessageStore instance.
     *
     * @param inSessionId a <code>SessionID</code> value
     */
    public NullMessageStore(SessionID inSessionId)
    {
        sessionId = inSessionId;
        cache = new MemoryStore(inSessionId);
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#get(int, int, java.util.Collection)
     */
    @Override
    public void get(int inStartSequence,
                    int inEndSequence,
                    Collection<String> inMessages)
            throws IOException
    {
        cache.get(inStartSequence,
                  inEndSequence,
                  inMessages);
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#getCreationTime()
     */
    @Override
    public Date getCreationTime()
            throws IOException
    {
        return cache.getCreationTime();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#getNextSenderMsgSeqNum()
     */
    @Override
    public int getNextSenderMsgSeqNum()
            throws IOException
    {
        return cache.getNextSenderMsgSeqNum();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#getNextTargetMsgSeqNum()
     */
    @Override
    public int getNextTargetMsgSeqNum()
            throws IOException
    {
        return cache.getNextTargetMsgSeqNum();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#incrNextSenderMsgSeqNum()
     */
    @Override
    public void incrNextSenderMsgSeqNum()
            throws IOException
    {
        cache.incrNextSenderMsgSeqNum();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#incrNextTargetMsgSeqNum()
     */
    @Override
    public void incrNextTargetMsgSeqNum()
            throws IOException
    {
        cache.incrNextTargetMsgSeqNum();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#refresh()
     */
    @Override
    public void refresh()
            throws IOException
    {
        cache.refresh();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#reset()
     */
    @Override
    public void reset()
            throws IOException
    {
        cache.reset();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#set(int, java.lang.String)
     */
    @Override
    public boolean set(int inSequence,
                       String inMessage)
            throws IOException
    {
        return cache.set(inSequence,
                         inMessage);
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#setNextSenderMsgSeqNum(int)
     */
    @Override
    public void setNextSenderMsgSeqNum(int inNext)
            throws IOException
    {
        cache.setNextSenderMsgSeqNum(inNext);
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#setNextTargetMsgSeqNum(int)
     */
    @Override
    public void setNextTargetMsgSeqNum(int inNext)
            throws IOException
    {
        cache.setNextTargetMsgSeqNum(inNext);
    }
    /**
     * Get the cache value.
     *
     * @return a <code>MemoryStore</code> value
     */
    public MemoryStore getCache()
    {
        return cache;
    }
    /**
     * Get the sessionId value.
     *
     * @return a <code>SessionID</code> value
     */
    public SessionID getSessionId()
    {
        return sessionId;
    }
    /**
     * cache value
     */
    private final MemoryStore cache;
    /**
     * session ID value
     */
    private final SessionID sessionId;
}
