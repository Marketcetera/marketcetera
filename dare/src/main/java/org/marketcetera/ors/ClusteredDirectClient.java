package org.marketcetera.ors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.Validations;
import org.marketcetera.client.jms.DataEnvelope;
import org.marketcetera.cluster.AbstractCallableClusterTask;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.QueueDescriptor;
import org.marketcetera.cluster.SimpleQueueDescriptor;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.fix.ClusteredBrokerStatus;
import org.marketcetera.ors.brokers.BrokerService;
import org.marketcetera.ors.brokers.Selector;
import org.marketcetera.ors.ws.ClientSession;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.HazelcastInstanceNotActiveException;

/* $License$ */

/**
 * Provides in-process access the client services augmented by a full cluster.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ClusteredDirectClient.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.5.0
 */
public class ClusteredDirectClient
        extends DirectClient
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.DirectClient#start()
     */
    @Override
    public void start()
    {
        Validate.notNull(clusterService);
        instanceData = clusterService.getInstanceData();
        processedMessages.clear();
        if(enableRemoteQueue) {
            clusterQueueListener = new QueueListener();
        }
        super.start();
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        if(clusterQueueListener != null) {
            try {
                clusterQueueListener.stop();
            } catch (InterruptedException ignored) {
            } finally {
                clusterQueueListener = null;
            }
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuilder().append(ClusteredDirectClient.class.getSimpleName()).append(" for ").append(getUsername()).toString();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.DirectClient#sendOrder(org.marketcetera.trade.OrderSingle)
     */
    @Override
    public void sendOrder(OrderSingle inOrderSingle)
            throws ConnectionException, OrderValidationException
    {
        Validations.validate(inOrderSingle);
        submitEnvelope(new DataEnvelope(inOrderSingle,
                                         getSessionId()));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.DirectClient#sendOrder(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void sendOrder(OrderReplace inOrderReplace)
            throws ConnectionException, OrderValidationException
    {
        Validations.validate(inOrderReplace);
        submitEnvelope(new DataEnvelope(inOrderReplace,
                                         getSessionId()));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.DirectClient#sendOrder(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void sendOrder(OrderCancel inOrderCancel)
            throws ConnectionException, OrderValidationException
    {
        Validations.validate(inOrderCancel);
        submitEnvelope(new DataEnvelope(inOrderCancel,
                                         getSessionId()));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.DirectClient#sendOrderRaw(org.marketcetera.trade.FIXOrder)
     */
    @Override
    public void sendOrderRaw(FIXOrder inFIXOrder)
            throws ConnectionException, OrderValidationException
    {
        Validations.validate(inFIXOrder);
        submitEnvelope(new DataEnvelope(inFIXOrder,
                                         getSessionId()));
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
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    /**
     * Get the enableRemoteQueue value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getEnableRemoteQueue()
    {
        return enableRemoteQueue;
    }
    /**
     * Sets the enableRemoteQueue value.
     *
     * @param inEnableRemoteQueue a <code>boolean</code> value
     */
    public void setEnableRemoteQueue(boolean inEnableRemoteQueue)
    {
        enableRemoteQueue = inEnableRemoteQueue;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.DirectClient#generateSessionId()
     */
    @Override
    protected SessionId generateSessionId()
    {
        GenerateSessionIdTask<ClientSession> idTask = new GenerateSessionIdTask<>(getUsername());
        SessionId sessionId = null;
        try {
            ClusterData thisInstance = clusterService.getInstanceData();
            Map<Object,Future<GeneratedSessionId>> idTaskResult = clusterService.execute(idTask);
            for(Map.Entry<Object,Future<GeneratedSessionId>> entry : idTaskResult.entrySet()) {
                GeneratedSessionId result = entry.getValue().get();
                if(result != null && thisInstance.equals(result.getClusterData())) {
                    sessionId = result.getSessionId();
                    break;
                }
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Could not generate a valid remote session id");
            throw new RuntimeException(e);
        }
        return sessionId;
    }
    /**
     * Submits the given order envelope to be sent by the appropriate cluster member.
     *
     * @param inEnvelope an <code>DataEnvelope</code> value
     */
    private void submitEnvelope(DataEnvelope inEnvelope)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} submitted to this instance",
                               inEnvelope);
        if(inEnvelope.getOrder().getBrokerID() == null) {
            // this is targeted to the default broker
            Selector selector = getRequestHandler().getSelector();
            if(selector == null) {
                // hand it to the regular handler to let it be rejected normally
                getRequestHandler().receiveMessage(inEnvelope);
                return;
            }
            BrokerID defaultBrokerId = selector.chooseBroker(inEnvelope.getOrder());
            inEnvelope.getOrder().setBrokerID(defaultBrokerId);
            SLF4JLoggerProxy.debug(this,
                                   "{} now targeted to {}",
                                   inEnvelope,
                                   defaultBrokerId);
        }
        // short-circuit if this instance can handle this envelop, otherwise, kick it to the cluster queue
        if(canHandle(inEnvelope)) {
            getRequestHandler().receiveMessage(inEnvelope);
        } else {
            if(enableRemoteQueue) {
                SLF4JLoggerProxy.debug(this,
                                       "This instance does not appear to be able to handle {}, submitting to cluster queue",
                                       inEnvelope);
                // validate session ID first, because the other instances in the cluster will trust us when we hand them the order with the session
                SessionHolder<ClientSession> sessionInfo = getRequestHandler().getUserManager().getSessionManager().get(inEnvelope.getSessionId());
                if(sessionInfo == null) {
                    throw new I18NException(new I18NBoundMessage1P(Messages.RH_SESSION_EXPIRED,
                                                                   inEnvelope.getSessionId()));
                }
                clusterService.addToQueue(requestHandlerProcessingQueue,
                                          new DareRequestPackage(inEnvelope));
            } else {
                // hand it to the regular handler to let it be rejected normally
                getRequestHandler().receiveMessage(inEnvelope);
            }
        }
    }
    /**
     * Generates a session id on a remote cluster member.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: ClusteredDirectClient.java 17266 2017-04-28 14:58:00Z colin $
     * @since 2.5.0
     */
    private static class GenerateSessionIdTask<SessionClazz>
            extends AbstractCallableClusterTask<GeneratedSessionId>
    {
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public GeneratedSessionId call()
                throws Exception
        {
            StatelessClientContext context = new StatelessClientContext();
            context.setAppId(new AppId("DirectClient"));
            context.setClientId(sessionManager.getServerId());
            context.setVersionId(new VersionId(ApplicationVersion.DEFAULT_VERSION.getVersionInfo()));
            LocaleWrapper locale = new LocaleWrapper(Locale.getDefault());
            context.setLocale(locale);
            if(sessionId == null) {
                sessionId = SessionId.generate();
            }
            SessionHolder<SessionClazz> sessionHolder = new SessionHolder<>(username,
                                                                            context);
            sessionManager.put(sessionId,
                               sessionHolder);
            GeneratedSessionId generatedSessionId = new GeneratedSessionId(sessionId,
                                                                           getClusterService().getInstanceData());
            return generatedSessionId;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("GenerateSessionIdTask [").append(username).append("]");
            return builder.toString();
        }
        /* (non-Javadoc)
         * @see com.marketcetera.matp.cluster.AbstractClusterTask#getRequiredWorkUnitId()
         */
        @Override
        public String getRequiredWorkUnitId()
        {
            return "MATP.DARE";
        }
        /**
         * Sets the sessionManager value.
         *
         * @param inSessionManager a <code>SessionManager<SessionClazz></code> value
         */
        private void setSessionManager(SessionManager<SessionClazz> inSessionManager)
        {
            sessionManager = inSessionManager;
        }
        /**
         * Create a new GenerateSessionId instance.
         *
         * @param inUsername a <code>String</code> value
         */
        private GenerateSessionIdTask(String inUsername)
        {
            this(inUsername,
                 null);
        }
        /**
         * Create a new GenerateSessionId instance.
         *
         * @param inUsername a <code>String</code> value
         * @param inSessionId a <code>SessionId</code> value
         */
        private GenerateSessionIdTask(String inUsername,
                                      SessionId inSessionId)
        {
            username = inUsername;
            sessionId = inSessionId;
        }
        /**
         * session id to duplicate or <code>null</code>
         */
        private SessionId sessionId;
        /**
         * username value for whom to generate the session
         */
        private final String username;
        /**
         * session manager supplied by the cluster member
         */
        @Autowired
        private transient SessionManager<SessionClazz> sessionManager;
        private static final long serialVersionUID = 3356670495078424996L;
    }
    /**
     * Indicates if this member of the cluster can handle the given order.
     *
     * @param inOrder an <code>DataEnvelope</code> value
     * @return a <code>boolean</code> value
     */
    private boolean canHandle(DataEnvelope inOrder)
    {
        SLF4JLoggerProxy.debug(this,
                               "Checking to see if {} can be handled by this instance",
                               inOrder);
        ClusteredBrokerStatus status = brokerService.getBrokerStatus(inOrder.getOrder().getBrokerID());
        if(status == null) {
            SLF4JLoggerProxy.debug(this,
                                   "{} cannot be handled by this host because there is no host data for {}",
                                   inOrder,
                                   inOrder.getOrder().getBrokerID());
            return false;
        }
        // doesn't matter (here) whether the host is logged on or not, just whether this session is active or not on this host
        if(status.getClusterData().equals(instanceData) && status.getStatus().isPrimary()) {
            SLF4JLoggerProxy.debug(this,
                                   "{} can be handled by this host as per {}",
                                   inOrder,
                                   status);
            return true;
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "{} cannot be handled by this host as per {}",
                                   inOrder,
                                   status);
            return false;
        }
    }
    /**
     * Waits for messages to process coming from the cluster queue.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: ClusteredDirectClient.java 17266 2017-04-28 14:58:00Z colin $
     * @since 2.5.0
     */
    private class QueueListener
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                while(keepRunning.get()) {
                    DareRequestPackage messagePackage = null;
                    try {
                        // TODO this causes a problem thanks to a Hazelcast bug: https://github.com/hazelcast/hazelcast/issues/5526
                        messagePackage = clusterService.peekFromQueue(requestHandlerProcessingQueue);
                        if(messagePackage == null) {
                            Thread.sleep(10);
                            continue;
                        }
                        SLF4JLoggerProxy.debug(ClusteredDirectClient.this,
                                               "Accepting {} to process",
                                               messagePackage);
                        // we may be the first worker thread to handle this message or not the first. check the status of the message before continuing.
                        if(processedMessages.contains(messagePackage.getId())) {
                            // a previous work thread has handled this one, that means we're in the cleanup role and this one needs no more work
                            SLF4JLoggerProxy.debug(ClusteredDirectClient.this,
                                                   "{} has been handled, we can confirm it is done",
                                                   messagePackage);
                            // remove it from the queue, if it's still the head
                            DareRequestPackage head = clusterService.peekFromQueue(requestHandlerProcessingQueue);
                            // head may be null!, also, can't rely on identity equality since the object may have been serialized
                            if(messagePackage.equals(head)) {
                                // this is the same object that we have verified, not a new one, so we can remove it
                                SLF4JLoggerProxy.debug(ClusteredDirectClient.this,
                                                       "The current queue head: {} is the same object we've been looking at ({}), so we're the cleanup - removing",
                                                       head,
                                                       messagePackage);
                                clusterService.takeFromQueue(requestHandlerProcessingQueue);
                            } else {
                                SLF4JLoggerProxy.debug(ClusteredDirectClient.this,
                                                       "The current queue head: {} is not the same object we've been looking at ({}), so we're the verify - not removing",
                                                       head,
                                                       messagePackage);
                            }
                            continue;
                        }
                        // this message has not been successfully handled yet, either we're the first one or previous worker threads have failed at this thread
                        SLF4JLoggerProxy.debug(ClusteredDirectClient.this,
                                               "{} has not been handled, processing now",
                                               messagePackage);
                        if(!canHandle(messagePackage.getDataEnvelope())) {
                            SLF4JLoggerProxy.debug(ClusteredDirectClient.this,
                                                   "This member cannot process {}, skipping",
                                                   messagePackage);
                            Thread.sleep(100);
                            continue;
                        } else {
                            // any orders handled by this queue could not be handled on the original instance on which they were submitted.
                            GenerateSessionIdTask<ClientSession> idTask = new GenerateSessionIdTask<>(getUsername(),
                                                                                                      messagePackage.getDataEnvelope().getSessionId());
                            idTask.setSessionManager(sessionManager);
                            idTask.setClusterService(clusterService);
                            idTask.call();
                            // we ignore the returned value because it is actually too much information. regardless of whether the message was sent successfully or not,
                            //  as long as it doesn't throw an exception, we're ok. the FIX engine will store it up until the session can be restarted.
                            getRequestHandler().receiveMessage(messagePackage.getDataEnvelope());
                            processedMessages.add(messagePackage.getId());
                            SLF4JLoggerProxy.debug(ClusteredDirectClient.this,
                                                   "{} successfully processed",
                                                   messagePackage);
                        }
                        // leave it in the queue until somebody can process it
                    } catch (HazelcastInstanceNotActiveException | InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(ClusteredDirectClient.this,
                                              e);
                        // let this order be rejected by the message handler, drop it from the queue for now
                        if(messagePackage != null) {
                            processedMessages.add(messagePackage.getId());
                        }
                    }
                }
            } finally {
                SLF4JLoggerProxy.info(ClusteredDirectClient.this,
                                      "DARE Request Handler Queue stopping");
            }
        }
        /**
         * Stops the object.
         *
         * @throws InterruptedException if the stop operation is interrupted
         */
        public void stop()
                throws InterruptedException
        {
            keepRunning.set(false);
            thread.interrupt();
            thread.join();
        }
        /**
         * Create a new QueueListener instance.
         */
        private QueueListener()
        {
            thread = new Thread(this,
                                "DARE Request Handler Queue");
            thread.start();
        }
        /**
         * thread on which the processor runs
         */
        private final Thread thread;
        /**
         * indicates if the thread should keep running or not
         */
        private final AtomicBoolean keepRunning = new AtomicBoolean(true);
    }
    /**
     * Combines the data that describe a FIX message that needs to be sent.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: ClusteredDirectClient.java 17266 2017-04-28 14:58:00Z colin $
     * @since 2.5.0
     */
    private static class DareRequestPackage
            implements Serializable
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("DARE RequestPackage [").append(id).append("]");
            return builder.toString();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(id).toHashCode();
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
            if (!(obj instanceof DareRequestPackage)) {
                return false;
            }
            DareRequestPackage other = (DareRequestPackage) obj;
            return new EqualsBuilder().append(id,other.id).isEquals();
        }
        /**
         * Gets the id value.
         *
         * @return a <code>String</code> value
         */
        private String getId()
        {
            return id;
        }
        /**
         * Get the DataEnvelope value.
         *
         * @return an <code>DataEnvelope</code> value
         */
        private DataEnvelope getDataEnvelope()
        {
            return DataEnvelope;
        }
        /**
         * Create a new MessagePackage instance.
         *
         * @param inDataEnvelope an <code>DataEnvelope</code> value
         */
        private DareRequestPackage(DataEnvelope inDataEnvelope)
        {
            DataEnvelope = inDataEnvelope;
            id = UUID.randomUUID().toString();
        }
        /**
         * order request to handle
         */
        private final DataEnvelope DataEnvelope;
        /**
         * uniquely identifies the message package
         */
        private final String id;
        private static final long serialVersionUID = 8687504696325940663L;
    }
    /**
     * contains messages processed since coming live
     */
    private final Set<String> processedMessages = new HashSet<>();
    /**
     * describes the cluster queue we're using to manage DARE requests
     */
    private static final QueueDescriptor<DareRequestPackage> requestHandlerProcessingQueue = new SimpleQueueDescriptor<>("dare-request-handler-processing-queue");
    /**
     * indicates what host we're running on
     */
    private ClusterData instanceData;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * listens to a cluster-wide work queue for messages to process
     */
    private QueueListener clusterQueueListener;
    /**
     * manages sessions
     */
    @Autowired
    private SessionManager<ClientSession> sessionManager;
    /**
     * indicates if the remote queue feature should be enabled
     */
    private boolean enableRemoteQueue = true;
}
