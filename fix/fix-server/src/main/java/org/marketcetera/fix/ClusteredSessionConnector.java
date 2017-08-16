package org.marketcetera.fix;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.cluster.CallableClusterTask;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributes;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.SessionConnector;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import quickfix.MessageStore;
import quickfix.MessageStoreFactory;
import quickfix.Session;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides a <code>SessionConnector</code> interface that connects to the given session anywhere in the cluster.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ClusteredSessionConnector.java 85164 2016-03-03 21:27:19Z colin $
 * @since 1.0.1
 */
public class ClusteredSessionConnector
        implements SessionConnector
{
    /**
     * Create a new ClusteredSessionConnector instance.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inClusterService a <code>ClusterService</code> value
     */
    public ClusteredSessionConnector(FixSession inFixSession,
                                     ClusterService inClusterService)
    {
        session = inFixSession;
        clusterService = inClusterService;
        Validate.notNull(session,
                         "Session required");
        Validate.notNull(clusterService,
                         "Cluster service required");
        sessionId = new SessionID(session.getSessionId());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(sessionId).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ClusteredSessionConnector)) {
            return false;
        }
        ClusteredSessionConnector other = (ClusteredSessionConnector) obj;
        return new EqualsBuilder().append(sessionId,other.sessionId).isEquals();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.broker.SessionConnector#resetConnection()
     */
    @Override
    public void resetConnection()
    {
        ResetConnectionTask task = new ResetConnectionTask(sessionId);
        task.getResult(clusterService);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.broker.SessionConnector#disconnectConnection()
     */
    @Override
    public void disconnectConnection()
    {
        DisconnectConnectionTask task = new DisconnectConnectionTask(sessionId);
        task.getResult(clusterService);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.broker.SessionConnector#resetSequenceNumber()
     */
    @Override
    public void resetSequenceNumber()
    {
        ResetSequenceNumberTask task = new ResetSequenceNumberTask(sessionId);
        task.getResult(clusterService);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.broker.SessionConnector#setNextSenderSequenceNumber(int)
     */
    @Override
    public void setNextSenderSequenceNumber(int inNextSequenceNumber)
    {
        SetNextSenderSequenceNumberTask task = new SetNextSenderSequenceNumberTask(sessionId,
                                                                                   inNextSequenceNumber);
        task.getResult(clusterService);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.broker.SessionConnector#getNextSenderSequenceNumber()
     */
    @Override
    public int getNextSenderSequenceNumber()
    {
        updateSessionValues();
        return cachedSenderSeqNum;
    }
    /**
     * Update the cached session values from the cluster-wide data store.
     */
    private void updateSessionValues()
    {
        try {
            Map<String,String> clusterFixSessionAttributes = clusterService.getMap(FixSessionAttributes.fixSessionAttributesKey);
            if(clusterFixSessionAttributes.containsKey(sessionId.toString())) {
                FixSessionAttributes fixSessionAttributes = FixSessionAttributes.getFromString(clusterFixSessionAttributes.get(sessionId.toString()));
                SLF4JLoggerProxy.trace(this,
                                      "Found {} for {}",
                                      fixSessionAttributes,
                                      sessionId);
                cachedSenderSeqNum = fixSessionAttributes.getNextSenderSeqNum();
                cachedTargetSeqNum = fixSessionAttributes.getNextTargetSeqNum();
                cachedAcceptorPort = fixSessionAttributes.getAcceptorPort();
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.debug(this,
                                   "Cluster in transition, returning cached seq num values for {}",
                                   sessionId);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.broker.SessionConnector#setNextTargetSequenceNumber(int)
     */
    @Override
    public void setNextTargetSequenceNumber(int inNextSequenceNumber)
    {
        SetNextTargetSequenceNumberTask task = new SetNextTargetSequenceNumberTask(sessionId,
                                                                                   inNextSequenceNumber);
        task.getResult(clusterService);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.broker.SessionConnector#getNextTargetSequenceNumber()
     */
    @Override
    public int getNextTargetSequenceNumber()
    {
        updateSessionValues();
        return cachedTargetSeqNum;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.broker.SessionConnector#getPort()
     */
    @Override
    public int getAcceptorPort()
    {
        updateSessionValues();
        return cachedAcceptorPort;
    }
    /**
     * Get the clusterService value.
     *
     * @return a <code>ClusterService</code> value
     */
    public ClusterService getClusterService()
    {
        return clusterService;
    }
    /**
     * Sets the clusterService value.
     *
     * @param inClusterService a <code>ClusterService</code> value
     */
    public void setClusterService(ClusterService inClusterService)
    {
        clusterService = inClusterService;
    }
    /**
     * Get the session value.
     *
     * @return a <code>FixSession</code> value
     */
    public FixSession getSession()
    {
        return session;
    }
    /**
     * Set the session value.
     *
     * @param inSession a <code>FixSession</code> value
     */
    public void setSession(FixSession inSession)
    {
        session = inSession;
    }
    /**
     * Provides common behavior for cluster-aware tasks.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: ClusteredSessionConnector.java 85164 2016-03-03 21:27:19Z colin $
     * @since 1.0.1
     */
    private static abstract class AbstractTask<Clazz extends Serializable>
            extends CallableClusterTask<Clazz>
    {
        /**
         * Gets the result of the task.
         *
         * @param inClusterService a <code>ClusterService</code> value
         * @return a <code>Clazz</code> value
         */
        public Clazz getResult(ClusterService inClusterService)
        {
            try {
                Map<Object,Future<Clazz>> taskResult = inClusterService.execute(this);
                Clazz result = null;
                for(Map.Entry<Object,Future<Clazz>> entry : taskResult.entrySet()) {
                    result = entry.getValue().get(1000,
                                                  TimeUnit.MILLISECONDS);
                    if(result != null) {
                        break;
                    }
                }
                return result;
            } catch (InterruptedException | NullPointerException ignored) {
                // this can happen normally on shutdown and can be safely ignored
            } catch (ExecutionException | TimeoutException ignored) {
                // this can happen if a cluster member has suddenly dropped out and can be safely ignored
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(ClusteredSessionConnector.class,
                                      e);
            }
            return null;
        }
        /**
         * Create a new AbstractTask instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         */
        protected AbstractTask(SessionID inSessionId)
        {
            sessionId = inSessionId;
        }
        /**
         * Gets the session value.
         *
         * @return a <code>Session</code> value or <code>null</code>
         */
        protected Session getSession()
        {
            return Session.lookupSession(sessionId);
        }
        /**
         * Update the sequence number store for the session.
         *
         * @param inSenderSeqNum an <code>int</code> value
         * @param inTargetSeqNum an <code>int</code> value
         */
        protected void updateSequenceNumberStore(int inSenderSeqNum,
                                                 int inTargetSeqNum)
        {
            FixSessionAttributes attributes = new FixSessionAttributes(getSessionId(),
                                                                       inSenderSeqNum,
                                                                       inTargetSeqNum,
                                                                       fixSettingsProviderFactory.create().getAcceptorPort());
            getClusterService().addToMap(FixSessionAttributes.fixSessionAttributesKey,
                                         getSessionId().toString(),
                                         attributes.getAsString());
        }
        /**
         * Get the message store for the session.
         *
         * @return a <code>MessageStore</code> value or <code>null</code>
         * @throws IOException if an error occurs retrieving or refreshing the message store
         */
        protected MessageStore getMessageStore()
                throws IOException
        {
            MessageStore messageStore = messageStoresBySessionId.get(getSessionId());
            if(messageStore != null) {
                messageStore.refresh();
                return messageStore;
            }
            FixSession fixSession = brokerService.findFixSessionBySessionId(getSessionId());
            if(fixSession == null) {
                SLF4JLoggerProxy.debug(ClusteredSessionConnector.class,
                                       "{} is not a valid session, cannot retrieve message store",
                                       getSessionId());
                return null;
            }
            if(brokerService.isAffinityMatch(fixSession,
                                             getClusterService().getInstanceData())) {
                FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
                MessageStoreFactory messageStoreFactory = fixSettingsProvider.getMessageStoreFactory(brokerService.generateSessionSettings(Lists.newArrayList(fixSession)));
                messageStore = messageStoreFactory.create(getSessionId());
                messageStoresBySessionId.put(getSessionId(),
                                             messageStore);
                return messageStore;
            } else {
                SLF4JLoggerProxy.debug(ClusteredSessionConnector.class,
                                       "{} is not an affinity match for {}, will not retrieve message store",
                                       getClusterService().getInstanceData(),
                                       getSessionId());
                return null;
            }
        }
        /**
         * Get the session id value.
         *
         * @return a <code>SessionID</code> value
         */
        protected SessionID getSessionId()
        {
            return sessionId;
        }
        /**
         * session ID value
         */
        private SessionID sessionId;
        /**
         * cached message store values
         */
        protected static final Map<SessionID,MessageStore> messageStoresBySessionId = new HashMap<>();
        /**
         * provides a fix settings provider factory value
         */
        @Autowired
        private FixSettingsProviderFactory fixSettingsProviderFactory;
        /**
         * provides access to broker services
         */
        @Autowired
        private BrokerService brokerService;
        private static final long serialVersionUID = 6327240204263634344L;
    }
    /**
     * Sets the next sender sequence number.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: ClusteredSessionConnector.java 85164 2016-03-03 21:27:19Z colin $
     * @since 1.0.1
     */
    private static class SetNextSenderSequenceNumberTask
            extends AbstractTask<Integer>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Integer call()
                throws IOException
        {
            int senderSeqNum = 0;
            int targetSeqNum = 0;
            Session session = getSession();
            if(session == null) {
                MessageStore messageStore = getMessageStore();
                if(messageStore == null) {
                    SLF4JLoggerProxy.debug(ClusteredSessionConnector.class,
                                           "{} has no session and cannot create message store for {}, cannot update sequence number",
                                           getClusterService().getInstanceData(),
                                           getSessionId());
                    return -1;
                }
                SLF4JLoggerProxy.debug(ClusteredSessionConnector.class,
                                       "{} has no active session for {}, but a message store exists: {}",
                                       getClusterService().getInstanceData(),
                                       getSessionId(),
                                       messageStore);
                messageStore.setNextSenderMsgSeqNum(nextSequenceNumber);
                senderSeqNum = nextSequenceNumber;
                targetSeqNum = messageStore.getNextTargetMsgSeqNum();
            } else {
                session.setNextSenderMsgSeqNum(nextSequenceNumber);
                senderSeqNum = nextSequenceNumber;
                targetSeqNum = session.getExpectedTargetNum();
                SLF4JLoggerProxy.debug(ClusteredSessionConnector.class,
                                       "{} successfully set next sender sequence number using active session for {} to {}",
                                       getClusterService().getInstanceData(),
                                       getSessionId(),
                                       nextSequenceNumber);
            }
            updateSequenceNumberStore(senderSeqNum,
                                      targetSeqNum);
            return 0;
        }
        /**
         * Create a new SetNextSenderSequenceNumberTask instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         * @param inNextSequenceNumber an <code>int</code> value
         */
        private SetNextSenderSequenceNumberTask(SessionID inSessionId,
                                                int inNextSequenceNumber)
        {
            super(inSessionId);
            nextSequenceNumber = inNextSequenceNumber;
        }
        /**
         * next sequence number value
         */
        private int nextSequenceNumber;
        private static final long serialVersionUID = -1196869199807441218L;
    }
    /**
     * Sets the next target sequence number.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: ClusteredSessionConnector.java 85164 2016-03-03 21:27:19Z colin $
     * @since 1.0.1
     */
    private static class SetNextTargetSequenceNumberTask
            extends AbstractTask<Integer>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Integer call()
                throws IOException
        {
            int senderSeqNum = 0;
            int targetSeqNum = 0;
            Session session = getSession();
            if(session == null) {
                MessageStore messageStore = getMessageStore();
                if(messageStore == null) {
                    SLF4JLoggerProxy.debug(ClusteredSessionConnector.class,
                                           "{} has no session and cannot create message store for {}, cannot update sequence number",
                                           getClusterService().getInstanceData(),
                                           getSessionId());
                    return -1;
                }
                SLF4JLoggerProxy.debug(ClusteredSessionConnector.class,
                                       "{} has no active session for {}, but a message store exists: {}",
                                       getClusterService().getInstanceData(),
                                       getSessionId(),
                                       messageStore);
                messageStore.setNextTargetMsgSeqNum(nextSequenceNumber);
                senderSeqNum = messageStore.getNextSenderMsgSeqNum();
                targetSeqNum = nextSequenceNumber;
            } else {
                session.setNextTargetMsgSeqNum(nextSequenceNumber);
                SLF4JLoggerProxy.debug(ClusteredSessionConnector.class,
                                       "{} successfully set next sender sequence number using active session for {} to {}",
                                       getClusterService().getInstanceData(),
                                       getSessionId(),
                                       nextSequenceNumber);
                senderSeqNum = session.getExpectedSenderNum();
                targetSeqNum = nextSequenceNumber;
            }
            updateSequenceNumberStore(senderSeqNum,
                                      targetSeqNum);
            return 0;
        }
        /**
         * Create a new SetNextTargetSequenceNumberTask instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         * @param inNextSequenceNumber an <code>int</code> value
         */
        private SetNextTargetSequenceNumberTask(SessionID inSessionId,
                                                int inNextSequenceNumber)
        {
            super(inSessionId);
            nextSequenceNumber = inNextSequenceNumber;
        }
        /**
         * next sequence number value
         */
        private int nextSequenceNumber;
        private static final long serialVersionUID = -4902346659529811363L;
    }
    /**
     * Resets the sequence number.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: ClusteredSessionConnector.java 85164 2016-03-03 21:27:19Z colin $
     * @since 1.0.1
     */
    private static class ResetSequenceNumberTask
            extends AbstractTask<Integer>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Integer call()
                throws Exception
        {
            Session session = getSession();
            if(session == null) {
                return -1;
            }
            session.setNextSenderMsgSeqNum(0);
            session.setNextTargetMsgSeqNum(0);
            return 0;
        }
        /**
         * Create a new ResetSequenceNumberTask instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         */
        private ResetSequenceNumberTask(SessionID inSessionId)
        {
            super(inSessionId);
        }
        private static final long serialVersionUID = 8824861024562226231L;
    }
    /**
     * Resets the connection.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: ClusteredSessionConnector.java 85164 2016-03-03 21:27:19Z colin $
     * @since 1.0.1
     */
    private static class ResetConnectionTask
            extends AbstractTask<Integer>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Integer call()
                throws Exception
        {
            Session session = getSession();
            if(session == null) {
                return -1;
            }
            session.reset();
            return 0;
        }
        /**
         * Create a new ResetConnectionTask instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         */
        private ResetConnectionTask(SessionID inSessionId)
        {
            super(inSessionId);
        }
        private static final long serialVersionUID = -7867270174613496783L;
    }
    /**
     * Disconnects the connection.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: ClusteredSessionConnector.java 85164 2016-03-03 21:27:19Z colin $
     * @since 1.0.1
     */
    private static class DisconnectConnectionTask
            extends AbstractTask<Integer>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Integer call()
        {
            Session session = getSession();
            if(session == null) {
                return -1;
            }
            session.logout();
            return 0;
        }
        /**
         * Create a new DisconnectConnectionTask instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         */
        private DisconnectConnectionTask(SessionID inSessionId)
        {
            super(inSessionId);
        }
        private static final long serialVersionUID = -896431529001876174L;
    }
    /**
     * session value
     */
    private FixSession session;
    /**
     * session id value
     */
    private SessionID sessionId;
    /**
     * provides access to cluster services
     */
    private ClusterService clusterService;
    /**
     * cached sender seq num
     */
    private volatile int cachedSenderSeqNum = 1;
    /**
     * cached target seq num
     */
    private volatile int cachedTargetSeqNum = 1;
    /**
     * cached acceptor port
     */
    private volatile int cachedAcceptorPort = 9800;
}
