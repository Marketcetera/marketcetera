package org.marketcetera.fix.store;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.marketcetera.core.BatchQueueProcessor;
import org.marketcetera.core.CloseableLock;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.BooleanBuilder;

import quickfix.MemoryStore;
import quickfix.Message;
import quickfix.MessageStore;
import quickfix.SessionID;
import quickfix.SystemTime;

/* $License$ */

/**
 * Provides a <code>MessageStore</code> implementation consistent with Hibernate.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class HibernateMessageStore
        implements MessageStore
{
    /**
     * Create a new HibernateMessageStore instance.
     * 
     * @param inSessionId a <code>SessionID</code> value
     * @throws IOException if an error occurs creating the message store
     */
    public HibernateMessageStore(SessionID inSessionId)
            throws IOException
    {
        sessionId = inSessionId;
        HibernateMessageStoreConfiguration config = HibernateMessageStoreConfiguration.getInstance();
        messageDao = config.getMessageDao();
        sessionDao = config.getSessionDao();
        messageTypeWhitelist.addAll(config.getMessageTypeWhitelist());
        messageTypeBlacklist.addAll(config.getMessageTypeBlacklist());
        SLF4JLoggerProxy.trace(HibernateMessageStore.this,
                               "Whitelist: {} blacklist: {}",
                               messageTypeWhitelist,
                               messageTypeBlacklist);
        transactionManager = HibernateMessageStoreConfiguration.getInstance().getTransactionManager();
        cache = new MemoryStore();
        initMessageProcessor();
        loadCache();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#set(int, java.lang.String)
     */
    @Override
    public boolean set(int inSequence,
                       String inMessage)
    {
        messageProcessor.add(new StoreMessagePackage(inSequence,
                                                     inMessage));
        return true;
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
        QMessageStoreMessage m = QMessageStoreMessage.messageStoreMessage;
        BooleanBuilder where = new BooleanBuilder();
        where = where.and(m.sessionId.eq(sessionId.toString()));
        where = where.and(m.msgSeqNum.goe(inStartSequence));
        where = where.and(m.msgSeqNum.loe(inEndSequence));
        Sort sort = new Sort(Sort.Direction.ASC,
                             QMessageStoreMessage.messageStoreMessage.msgSeqNum.getMetadata().getName());
        PageRequest pageable = new PageRequest(0,
                                               Integer.MAX_VALUE,
                                               sort);
        Page<MessageStoreMessage> messagePage = messageDao.findAll(where,
                                                                   pageable);
        Iterator<MessageStoreMessage> messageIterator = messagePage.iterator();
        while(messageIterator.hasNext()) {
            inMessages.add(messageIterator.next().getMessage());
        }
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
     * @see quickfix.MessageStore#setNextSenderMsgSeqNum(int)
     */
    @Override
    public void setNextSenderMsgSeqNum(int inNext)
            throws IOException
    {
        cache.setNextSenderMsgSeqNum(inNext);
        storeSequenceNumbers();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#setNextTargetMsgSeqNum(int)
     */
    @Override
    public void setNextTargetMsgSeqNum(int inNext)
            throws IOException
    {
        cache.setNextTargetMsgSeqNum(inNext);
        storeSequenceNumbers();
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#incrNextSenderMsgSeqNum()
     */
    @Override
    public void incrNextSenderMsgSeqNum()
            throws IOException
    {
        cache.incrNextSenderMsgSeqNum();
        setNextSenderMsgSeqNum(cache.getNextSenderMsgSeqNum());
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#incrNextTargetMsgSeqNum()
     */
    @Override
    public void incrNextTargetMsgSeqNum()
            throws IOException
    {
        cache.incrNextTargetMsgSeqNum();
        setNextTargetMsgSeqNum(cache.getNextTargetMsgSeqNum());
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
     * @see quickfix.MessageStore#reset()
     */
    @Override
    public void reset()
            throws IOException
    {
        SLF4JLoggerProxy.debug(this,
                               "{} resetting message store",
                               sessionId);
        try(CloseableLock resetLock = CloseableLock.create(cacheLock.writeLock())) {
            resetLock.lock();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("resetTransaction");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setReadOnly(false);
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                initMessageProcessor();
                cache.reset();
                messageDao.deleteBySessionId(sessionId.toString());
                MessageStoreSession session = getSession();
                session.setCreationTime(cache.getCreationTime());
                session.setTargetSeqNum(cache.getNextTargetMsgSeqNum());
                session.setSenderSeqNum(cache.getNextSenderMsgSeqNum());
                sessionDao.save(session);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
                try {
                    transactionManager.rollback(status);
                } catch (Exception e1) {
                    SLF4JLoggerProxy.warn(this,
                                          e1);
                } finally {
                    status = null;
                }
            } finally {
                if(status != null) {
                    transactionManager.commit(status);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see quickfix.MessageStore#refresh()
     */
    @Override
    public void refresh()
            throws IOException
    {
        SLF4JLoggerProxy.debug(this,
                               "{} refreshing message store",
                               sessionId);
        try(CloseableLock loadLock = CloseableLock.create(cacheLock.writeLock())) {
            loadLock.lock();
            loadCache();
        }
    }
    /**
     * Get the attached persistent session corresponding to the message store session.
     *
     * @return a <code>MessageStoreSession</code> value
     * @throws IOException if an error occurs retrieving or creating the session
     */
    private MessageStoreSession getSession()
            throws IOException
    {
        MessageStoreSession persistentSession = sessionDao.findBySessionId(sessionId.toString());
        if(persistentSession == null) {
            persistentSession = new MessageStoreSession();
            persistentSession.setSessionId(sessionId.toString());
            persistentSession.setCreationTime(cache.getCreationTime());
            persistentSession.setTargetSeqNum(cache.getNextTargetMsgSeqNum());
            persistentSession.setSenderSeqNum(cache.getNextSenderMsgSeqNum());
            persistentSession = sessionDao.save(persistentSession);
        }
        return persistentSession;
    }
    /**
     * Initializes the message processor, discarding any pending messages.
     */
    private void initMessageProcessor()
    {
        if(messageProcessor != null) {
            try {
                messageProcessor.stop();
            } catch (Exception ignored) {}
            messageProcessor = null;
        }
        messageProcessor = new MessageProcessor();
        messageProcessor.start();
    }
    /**
     * Load the cache from the database representation.
     *
     * @throws IOException if an error occurs loading the cache
     */
    private void loadCache()
            throws IOException
    {
        MessageStoreSession persistentSession = getSession();
        try {
            Method method = cache.getClass().getDeclaredMethod("setCreationTime",
                                                               java.util.Calendar.class);
            method.setAccessible(true);
            method.invoke(cache,
                          SystemTime.getUtcCalendar(persistentSession.getCreationTime()));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new RuntimeException(e);
        }
        cache.setNextSenderMsgSeqNum(persistentSession.getSenderSeqNum());
        cache.setNextTargetMsgSeqNum(persistentSession.getTargetSeqNum());
    }
    /**
     * Store the current sequence numbers in the cache.
     *
     * @throws IOException if an error occurs storing the sequence numbers
     */
    private void storeSequenceNumbers()
            throws IOException
    {
        messageProcessor.add(new StoreSequenceNumberPackage(cache.getNextTargetMsgSeqNum(),
                                                            cache.getNextSenderMsgSeqNum()));
    }
    /**
     * Provides common behavior for message packages.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private abstract static class MessagePackage
    {
    }
    /**
     * Contains the information necessary to update the message store.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class StoreMessagePackage
            extends MessagePackage
    {
        /**
         * Create a new StoreMessagePackage instance.
         *
         * @param inMsgSeqNum an <code>int</code> value
         * @param inMessage a <code>String</code> value
         */
        private StoreMessagePackage(int inMsgSeqNum,
                                    String inMessage)
        {
            msgSeqNum = inMsgSeqNum;
            message = inMessage;
        }
        /**
         * the message sequence number value
         */
        private final int msgSeqNum;
        /**
         * the message value
         */
        private final String message;
    }
    /**
     * Contains the information necessary to update the session store.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class StoreSequenceNumberPackage
            extends MessagePackage
    {
        /**
         * Create a new StoreSequenceNumberPackage instance.
         *
         * @param inNextTargetMsgSeqNum an <code>int</code> value
         * @param inNextSenderMsgSeqNum an <code>int</code> value
         */
        private StoreSequenceNumberPackage(int inNextTargetMsgSeqNum,
                                           int inNextSenderMsgSeqNum)
        {
            nextTargetMsgSeqNum = inNextTargetMsgSeqNum;
            nextSenderMsgSeqNum = inNextSenderMsgSeqNum;
        }
        /**
         * next target message sequence number value
         */
        private final int nextTargetMsgSeqNum;
        /**
         * next sender message sequence number value
         */
        private final int nextSenderMsgSeqNum;
    }
    /**
     * Processes store updates.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class MessageProcessor
            extends BatchQueueProcessor<MessagePackage>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.BatchQueueProcessor#processData(java.util.Deque)
         */
        @Override
        protected void processData(Deque<MessagePackage> inData)
                throws Exception
        {
            Collection<MessageStoreMessage> newMessages = Lists.newArrayList();
            StoreSequenceNumberPackage latestSequence = null;
            try(CloseableLock updateLock = CloseableLock.create(cacheLock.writeLock())) {
                updateLock.lock();
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setName("insertTransaction");
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                def.setReadOnly(false);
                TransactionStatus status = transactionManager.getTransaction(def);
                try {
                    for(MessagePackage messagePackage : inData) {
                        try {
                            if(messagePackage instanceof StoreMessagePackage) {
                                StoreMessagePackage storeMessagePackage = (StoreMessagePackage)messagePackage;
                                if(!messageTypeBlacklist.isEmpty() || !messageTypeWhitelist.isEmpty()) {
                                    Message fixMessage = new Message(storeMessagePackage.message);
                                    String msgType = fixMessage.getHeader().getString(quickfix.field.MsgType.FIELD);
                                    if(!messageTypeWhitelist.isEmpty()) {
                                        if(!messageTypeWhitelist.contains(msgType)) {
                                            continue;
                                        }
                                    }
                                    if(!messageTypeBlacklist.isEmpty()) {
                                        if(messageTypeBlacklist.contains(msgType)) {
                                            continue;
                                        }
                                    }
                                }
                                MessageStoreMessage persistentMessage = new MessageStoreMessage();
                                persistentMessage.setMessage(storeMessagePackage.message);
                                persistentMessage.setMsgSeqNum(storeMessagePackage.msgSeqNum);
                                persistentMessage.setSessionId(sessionId.toString());
                                newMessages.add(persistentMessage);
                            } else if(messagePackage instanceof StoreSequenceNumberPackage) {
                                latestSequence = (StoreSequenceNumberPackage)messagePackage;
                            }
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(HibernateMessageStore.this,
                                                  e,
                                                  "Unable to process: {}",
                                                  messagePackage);
                        }
                    }
                    messageDao.saveAll(newMessages);
                    if(latestSequence != null) {
                        MessageStoreSession persistentSession = getSession();
                        persistentSession.setTargetSeqNum(latestSequence.nextTargetMsgSeqNum);
                        persistentSession.setSenderSeqNum(latestSequence.nextSenderMsgSeqNum);
                        sessionDao.save(persistentSession);
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                    try {
                        transactionManager.rollback(status);
                    } catch (Exception e1) {
                        SLF4JLoggerProxy.warn(this,
                                              e1);
                    } finally {
                        status = null;
                    }
                } finally {
                    if(status != null) {
                        transactionManager.commit(status);
                    }
                }
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.BatchQueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(MessagePackage inData)
        {
            super.add(inData);
        }
        /**
         * Create a new MessageProcessor instance.
         */
        private MessageProcessor()
        {
            super("MessageStoreMessageProcessor-" + sessionId);
        }
    }
    /**
     * message types listed here will not be stored, others will
     */
    private final Set<String> messageTypeBlacklist = Sets.newHashSet();
    /**
     * message types listed here will be stored, others will not
     */
    private final Set<String> messageTypeWhitelist = Sets.newHashSet();
    /**
     * guards access to the persistent message store
     */
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    /**
     * processes messages
     */
    private MessageProcessor messageProcessor;
    /**
     * session id value of the message store
     */
    private final SessionID sessionId;
    /**
     * in-memory message store cache
     */
    private final MemoryStore cache;
    /**
     * provides access to the message data store
     */
    private final MessageStoreMessageDao messageDao;
    /**
     * provides access to the session data store
     */
    private final MessageStoreSessionDao sessionDao;
    /**
     * provides access to transaction services
     */
    private final PlatformTransactionManager transactionManager;
}
