package org.marketcetera.dare;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokerStatusPublisher;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterWorkUnit;
import org.marketcetera.cluster.ClusterWorkUnitType;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.Pair;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.QueueProcessor;
import org.marketcetera.core.file.DirectoryWatcherImpl;
import org.marketcetera.core.file.DirectoryWatcherSubscriber;
import org.marketcetera.core.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributes;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.OrderIntercepted;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.fix.SessionRestorePayload;
import org.marketcetera.fix.SessionRestorePayloadHandler;
import org.marketcetera.fix.provisioning.FixSessionRestoreExecutor;
import org.marketcetera.ors.Messages;
import org.marketcetera.ors.PrioritizedMessageSessionRestorePayload;
import org.marketcetera.ors.QuickFIXApplication;
import org.marketcetera.ors.ReportReceiver;
import org.marketcetera.ors.filters.MessageFilter;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.SessionStatusListener;
import org.marketcetera.quickfix.SessionStatusPublisher;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.RootOrderIdFactory;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.service.OutgoingMessageService;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.AnalyzedMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.Counter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.OperationTimeoutException;

import quickfix.ApplicationExtended;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.ClOrdID;
import quickfix.field.DeliverToCompID;
import quickfix.field.MsgType;
import quickfix.field.SessionRejectReason;
import quickfix.field.Text;
import quickfix.field.TradSesStatus;

/* $License$ */

/**
 * Provides routing engine services for the Marketcetera Automated Trading Platform.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClusterWorkUnit(id="MATP.DARE",type=ClusterWorkUnitType.SINGLETON_RUNTIME)
public class DeployAnywhereRoutingEngine
        implements ApplicationExtended,ReportReceiver,BrokerStatusPublisher,SessionStatusPublisher,DirectoryWatcherSubscriber
{
    /* (non-Javadoc)
     * @see quickfix.Application#onCreate(quickfix.SessionID)
     */
    @Override
    public void onCreate(SessionID inSessionId)
    {
        activeSessions.add(inSessionId);
        String sessionName = brokerService.getSessionName(inSessionId);
        SLF4JLoggerProxy.debug(this,
                               "OnCreate {}", //$NON-NLS-1$
                               sessionName);
        try {
            if(fixSessionRestoreExecutor == null) {
                SLF4JLoggerProxy.warn(FIXMessageUtil.FIX_RESTORE_LOGGER_NAME,
                                      "No FIX session restore executor specified, session restore for {} may be incomplete",
                                      sessionName);
            } else {
                fixSessionRestoreExecutor.sessionCreate(inSessionId,
                                                        new SessionRestorePayloadHandler<PrioritizedMessageSessionRestorePayload>() {
                    @Override
                    public void submit(PrioritizedMessageSessionRestorePayload inPayload)
                    {
                        try {
                            doFromApp(inPayload.getMessage(),
                                      inSessionId,
                                      inPayload.getPriority());
                        } catch (UnsupportedMessageType e) {
                            SLF4JLoggerProxy.warn(DeployAnywhereRoutingEngine.this,
                                                  e);
                        }
                    }
                });
            }
        } finally {
            ServerFixSession fixSession = brokerService.getServerFixSession(inSessionId);
            if(fixSession == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Ignoring onCreate for missing session {}",
                                      sessionName);
            } else {
                updateStatus(fixSession.getActiveFixSession().getFixSession(),
                             false);
            }
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogon(quickfix.SessionID)
     */
    @Override
    public void onLogon(SessionID inSessionId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see quickfix.Application#onLogout(quickfix.SessionID)
     */
    @Override
    public void onLogout(SessionID inSessionId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toAdmin(Message inMessage,
                        SessionID inSessionId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromAdmin(Message inMessage,
                          SessionID inSessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toApp(Message inMessage,
                      SessionID inSessionId)
            throws DoNotSend
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromApp(Message inMessage,
                        SessionID inSessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see quickfix.ApplicationExtended#canLogon(quickfix.SessionID)
     */
    @Override
    public boolean canLogon(SessionID inSessionID)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }

    /* (non-Javadoc)
     * @see quickfix.ApplicationExtended#onBeforeSessionReset(quickfix.SessionID)
     */
    @Override
    public void onBeforeSessionReset(SessionID inSessionID)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.file.DirectoryWatcherSubscriber#received(java.io.File, java.lang.String)
     */
    @Override
    public void received(File inFile,
                         String inOriginalFileName)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.SessionStatusPublisher#addSessionStatusListener(org.marketcetera.quickfix.SessionStatusListener)
     */
    @Override
    public void addSessionStatusListener(SessionStatusListener inSessionStatusListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.SessionStatusPublisher#removeSessionStatusListener(org.marketcetera.quickfix.SessionStatusListener)
     */
    @Override
    public void removeSessionStatusListener(SessionStatusListener inSessionStatusListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusPublisher#addBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusPublisher#removeBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.ReportReceiver#addReport(org.marketcetera.trade.ReportBase)
     */
    @Override
    public void addReport(ReportBase inReport)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.ReportReceiver#deleteReport(org.marketcetera.trade.ReportBase)
     */
    @Override
    public void deleteReport(ReportBase inReport)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(clusterService);
        Validate.notNull(reportService);
        Validate.notNull(brokerService);
        clusterData = clusterService.getInstanceData();
        if(fixInjectorDirectory != null) {
            try {
                String actualDirectory = fixInjectorDirectory + clusterData.getInstanceNumber();
                DirectoryWatcherImpl watcher = new DirectoryWatcherImpl();
                watcher.setDirectoriesToWatch(Lists.newArrayList(new File(actualDirectory)));
                watcher.setPollingInterval(5000); // TODO config
                watcher.addWatcher(this);
                watcher.start();
                SLF4JLoggerProxy.info(this,
                                      "Watching {} for FIX injector files",
                                      actualDirectory);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to watch for FIX injector files: {}",
                                      ExceptionUtils.getRootCauseMessage(e));
            }
        }
        final int acceptorPort = fixSettingsProviderFactory.create().getAcceptorPort();
        scheduledService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    for(SessionID sessionId : activeSessions) {
                        Session session = Session.lookupSession(sessionId);
                        if(session == null) {
                            return;
                        }
                        int senderSeqNum = session.getExpectedSenderNum();
                        int targetSeqNum = session.getExpectedTargetNum();
                        FixSessionAttributes attributes = new FixSessionAttributes(sessionId,
                                                                                   senderSeqNum,
                                                                                   targetSeqNum,
                                                                                   acceptorPort);
                        clusterService.addToMap(FixSessionAttributes.fixSessionAttributesKey,
                                                sessionId.toString(),
                                                attributes.getAsString());
                    }
                } catch (OperationTimeoutException e) {
                    SLF4JLoggerProxy.debug(DeployAnywhereRoutingEngine.this,
                                           "Cluster in transition, cannot update fix session attributes, will try again shortly");
                } catch (Exception e) {
                    SLF4JLoggerProxy.debug(DeployAnywhereRoutingEngine.this,
                                           e,
                                           "Cluster in transition, cannot update fix session attributes, will try again shortly");
                }
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
        isRunning.set(true);
    }
    /**
     * Gets the supported messages value.
     *
     * @return a <code>MessageFilter</code> value
     */
    public MessageFilter getSupportedMessages()
    {
        return supportedMessages;
    }
    /**
     * Get the fixSessionRestoreExecutor value.
     *
     * @return a <code>FixSessionRestoreExecutor&lt;PrioritizedMessageSessionRestorePayload,SessionRestorePayload&gt;</code> value
     */
    public FixSessionRestoreExecutor<PrioritizedMessageSessionRestorePayload,SessionRestorePayload> getFixSessionRestoreExecutor()
    {
        return fixSessionRestoreExecutor;
    }
    /**
     * Sets the fixSessionRestoreExecutor value.
     *
     * @param inFixSessionRestoreExecutor a <code>FixSessionRestoreExecutor&lt;PrioritizedMessageSessionRestorePayload,SessionRestorePayload&gt;</code> value
     */
    public void setFixSessionRestoreExecutor(FixSessionRestoreExecutor<PrioritizedMessageSessionRestorePayload, SessionRestorePayload> inFixSessionRestoreExecutor)
    {
        fixSessionRestoreExecutor = inFixSessionRestoreExecutor;
    }
    /**
     * Sets the supportedMessages value.
     *
     * @param inSupportedMessages a <code>MessageFilter</code> value
     */
    public void setSupportedMessages(MessageFilter inSupportedMessages)
    {
        supportedMessages = inSupportedMessages;
    }
    /**
     * Get the maxExecutionPools value.
     *
     * @return an <code>int</code> value
     */
    public int getMaxExecutionPools()
    {
        return maxExecutionPools;
    }
    /**
     * Sets the maxExecutionPools value.
     *
     * @param inMaxExecutionPools an <code>int</code> value
     */
    public void setMaxExecutionPools(int inMaxExecutionPools)
    {
        maxExecutionPools = inMaxExecutionPools;
    }
    /**
     * Get the executionPoolDelay value.
     *
     * @return a <code>long</code> value
     */
    public long getExecutionPoolDelay()
    {
        return executionPoolDelay;
    }
    /**
     * Sets the executionPoolDelay value.
     *
     * @param inExecutionPoolDelay a <code>long</code> value
     */
    public void setExecutionPoolDelay(long inExecutionPoolDelay)
    {
        executionPoolDelay = inExecutionPoolDelay;
    }
    /**
     * Get the executionPoolTtl value.
     *
     * @return a <code>long</code> value
     */
    public long getExecutionPoolTtl()
    {
        return executionPoolTtl;
    }
    /**
     * Sets the executionPoolTtl value.
     *
     * @param inExecutionPoolTtl a <code>long</code> value
     */
    public void setExecutionPoolTtl(long inExecutionPoolTtl)
    {
        executionPoolTtl = inExecutionPoolTtl;
    }
    /**
     * Get the fixInjectorDirectory value.
     *
     * @return a <code>String</code> value
     */
    public String getFixInjectorDirectory()
    {
        return fixInjectorDirectory;
    }
    /**
     * Sets the fixInjectorDirectory value.
     *
     * @param inFixInjectorDirectory a <code>String</code> value
     */
    public void setFixInjectorDirectory(String inFixInjectorDirectory)
    {
        fixInjectorDirectory = inFixInjectorDirectory;
    }
    /**
     * Get the dontForwardMessages value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getDontForwardMessages()
    {
        return dontForwardMessages;
    }
    /**
     * Sets the dontForwardMessages value.
     *
     * @param inDontForwardMessages a <code>Set&lt;String&gt;</code> value
     */
    public void setDontForwardMessages(Set<String> inDontForwardMessages)
    {
        dontForwardMessages.clear();
        if(inDontForwardMessages != null) {
            dontForwardMessages.addAll(inDontForwardMessages);
        }
    }
    /**
     * Get the allowDeliverToCompID value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getAllowDeliverToCompID()
    {
        return allowDeliverToCompID;
    }
    /**
     * Sets the allowDeliverToCompID value.
     *
     * @param inAllowDeliverToCompID a <code>boolean</code> value
     */
    public void setAllowDeliverToCompID(boolean inAllowDeliverToCompID)
    {
        allowDeliverToCompID = inAllowDeliverToCompID;
    }
    /**
     * Process the given application message.
     *
     * @param inMessage a <code>Message</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inPriority an <code>int</code> value
     * @throws UnsupportedMessageType if the message is of an unsupported message type
     */
    private void doFromApp(Message inMessage,
                           SessionID inSessionId,
                           int inPriority)
            throws UnsupportedMessageType
    {
        ServerFixSession ServerFixSession = brokerService.getServerFixSession(inSessionId);
        Messages.QF_FROM_APP.info(getCategory(inMessage),
                                  inMessage,
                                  ServerFixSession);
        logMessage(inMessage,
                   ServerFixSession);
        // accept only certain message types
        if(getSupportedMessages() != null && !getSupportedMessages().isAccepted(inMessage)) {
            Messages.QF_DISALLOWED_MESSAGE.info(getCategory(inMessage));
            throw new UnsupportedMessageType();
        }
        addToQueue(new MessagePackage(inMessage,
                                      MessageType.FROM_APP,
                                      inSessionId,
                                      Hierarchy.Flat,
                                      inPriority));
    }
    /**
     * Logs the given message, analyzed using the receiver's data
     * dictionary, at the debugging level.
     *
     * @param msg The message.
     */
    private void logMessage(Message msg,
                            ServerFixSession inBroker)
    {
        Object category=(FIXMessageUtil.isHeartbeat(msg)?
                         HEARTBEAT_CATEGORY:this);
        if(SLF4JLoggerProxy.isDebugEnabled(category)) {
            Messages.ANALYZED_MESSAGE.debug(category,
                                            new AnalyzedMessage(inBroker.getDataDictionary(),msg).toString());
        }
    }
    /**
     * Processes the given message package.
     *
     * @param messagePackage a <code>MessagePackage</code> value
     */
    private void processMessage(MessagePackage messagePackage)
    {
        SessionID session = messagePackage.sessionId;
        Message msg = messagePackage.message;
        ServerFixSession serverFixSession = null;
        try {
            serverFixSession = brokerService.getServerFixSession(session);
            switch(messagePackage.getMessageType()) {
                case FROM_ADMIN: {
                    Object category = getCategory(msg);
                    boolean shouldDisplayMessage = !category.equals(HEARTBEAT_CATEGORY) || SLF4JLoggerProxy.isDebugEnabled(HEARTBEAT_CATEGORY);
                    if(shouldDisplayMessage) {
                        Messages.QF_FROM_ADMIN.info(category,
                                                    msg,
                                                    serverFixSession);
                        logMessage(msg,
                                   serverFixSession);
                        // Send message to client.
                        sendToClientTrades(true,
                                           serverFixSession,
                                           msg,
                                           Originator.Broker,
                                           messagePackage.getHierarchy());
                    }
                    break;
                }
                case FROM_APP: {
                    try {
                        // Report trading session status in a human-readable format.
                        if(FIXMessageUtil.isTradingSessionStatus(msg)) {
                            FIXDataDictionary dataDictionary = serverFixSession.getFIXDataDictionary();
                            Messages.QF_TRADE_SESSION_STATUS.info(getCategory(msg),
                                                                  dataDictionary.getHumanFieldValue(TradSesStatus.FIELD,
                                                                                                    msg.getString(TradSesStatus.FIELD)));
                        }
                        // Send message to client.
                        sendToClientTrades(false,
                                           serverFixSession,
                                           msg,
                                           Originator.Broker,
                                           messagePackage.hierarchy);
                        if(!allowDeliverToCompID && msg.getHeader().isSetField(DeliverToCompID.FIELD)) {
                            // OpenFIX certification: we reject all DeliverToCompID since we don't redeliver.
                            try {
                                Message reject = serverFixSession.getFIXMessageFactory().createSessionReject(msg,
                                                                                                             SessionRejectReason.COMPID_PROBLEM);
                                reject.setString(Text.FIELD,
                                                 Messages.QF_COMP_ID_REJECT.getText(msg.getHeader().getString(DeliverToCompID.FIELD)));
                                getSender().sendToTarget(reject,
                                                         session);
                            } catch (SessionNotFound ex) {
                                Messages.QF_COMP_ID_REJECT_FAILED.error(getCategory(msg),
                                                                        ex,
                                                                        serverFixSession.toString());
                            }
                            break;
                        }
                    } catch (FieldNotFound e) {
                        SLF4JLoggerProxy.error(QuickFIXApplication.class,
                                               e);
                    }
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        } catch (HazelcastInstanceNotActiveException e) {
            Messages.QF_STANDARD_SHUTDOWN.warn(DeployAnywhereRoutingEngine.this,
                                               msg);
        } catch (Exception e) {
            if(PlatformServices.isShutdown(e)) {
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    Messages.QF_SHUTDOWN_ERROR.warn(DeployAnywhereRoutingEngine.this,
                                                    e,
                                                    msg);
                } else {
                    Messages.QF_SHUTDOWN_ERROR.warn(DeployAnywhereRoutingEngine.this,
                                                    msg);
                }
            } else {
                SLF4JLoggerProxy.error(DeployAnywhereRoutingEngine.this,
                                       e);
            }
        }
    }
    /**
     * Updates broker status.
     *
     * @param inBroker a <code>Broker</code> value
     * @param inStatus a <code>boolean</code> value
     */
    private void updateStatus(FixSession inFixSession,
                              boolean inStatus)
    {
      FixSessionStatus status;
      BrokerID brokerId = new BrokerID(inFixSession.getBrokerId());
        if(inStatus) {
            status = FixSessionStatus.CONNECTED;
        } else {
            Session session = Session.lookupSession(new SessionID(inFixSession.getSessionId()));
            if(session == null) {
                if(inFixSession.isEnabled()) {
                    if(brokerService.isAffinityMatch(inFixSession,
                                                     clusterData)) {
                        status = FixSessionStatus.BACKUP;
                    } else {
                        status = FixSessionStatus.AFFINITY_MISMATCH;
                    }
                } else {
                    status = FixSessionStatus.DISABLED;
                }
            } else {
                if(brokerService.isSessionTime(new SessionID(inFixSession.getSessionId()))) {
                    status = FixSessionStatus.NOT_CONNECTED;
                } else {
                    status = FixSessionStatus.DISCONNECTED;
                }
            }
        }
        ServerFixSession brokerStatus = brokerService.getServerFixSession(brokerId);
        brokerService.reportBrokerStatus(brokerId,
                                         brokerStatus.getActiveFixSession().getStatus());
//        if(oldStatus == inStatus) {
//            return;
//        }
        // TODO is this crap necessary?
        ActiveFixSession broker = brokerService.generateBroker(inFixSession);
        Messages.QF_SENDING_STATUS.info(this,
                                        inStatus,
                                        broker);
        for(BrokerStatusListener listener : brokerStatusListeners) {
            listener.receiveBrokerStatus(brokerStatus.getActiveFixSession());
        }
    }
    /**
     * Sends the given message to the subscribed clients.
     *
     * @param inIsAdmin a <code>boolean</code> value
     * @param inServerFixSession a <code>ServerFixSession</code> value
     * @param inMessage a <code>Message</code> value
     * @param inOriginator an <code>Originator</code> value
     * @param inHierarchy a <code>Hierarchy</code> value
     */
    private void sendToClientTrades(boolean inIsAdmin,
                                    ServerFixSession inServerFixSession,
                                    Message inMessage,
                                    Originator inOriginator,
                                    Hierarchy inHierarchy)
    {
        UserID actor = null;
        if(!inIsAdmin) {
            actor = outgoingMessageService.getMessageOwner(inMessage,
                                                           new SessionID(inServerFixSession.getActiveFixSession().getFixSession().getSessionId()),
                                                           new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getBrokerId()));
        }
        // Apply message modifiers.
        boolean orderIntercepted = false;
        if((inOriginator == Originator.Broker) && (inServerFixSession.getResponseModifiers() != null)) {
            try {
                inServerFixSession.getResponseModifiers().modifyMessage(inMessage);
            } catch (OrderIntercepted e) {
                SLF4JLoggerProxy.info(this,
                                      "{} intercepted",
                                      inMessage);
                orderIntercepted = true;
            } catch (I18NException ex) {
                Messages.QF_MODIFICATION_FAILED.warn(getCategory(inMessage),
                                                     ex,
                                                     inMessage,
                                                     inServerFixSession);
            }
        }
        TradeMessage reply;
        try {
            reply = FIXConverter.fromQMessage(inMessage,
                                              inOriginator,
                                              new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getBrokerID()),
                                              inHierarchy,
                                              actor,
                                              actor);
        } catch (MessageCreationException ex) {
            Messages.QF_REPORT_FAILED.error(getCategory(inMessage),
                                            ex,
                                            inMessage,
                                            inServerFixSession);
            return;
        }
        try {
            // Persist and send reply.
            getPersister().persistReply(reply);
        } finally {
            Messages.QF_SENDING_REPLY.info(getCategory(inMessage),reply);
            try {
                String msgType = inMessage.getHeader().getString(MsgType.FIELD);
                // check to see if the message has been blacklisted
                if(dontForwardMessages.contains(msgType)) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} is in the blacklist of messages {} for clients and will not be forwarded", //$NON-NLS-1$
                                           inMessage,
                                           dontForwardMessages);
                    return;
                }
            } catch (FieldNotFound e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "On send to client: {}",
                                      ExceptionUtils.getRootCauseMessage(e));
                return;
            }
            if(orderIntercepted) {
                SLF4JLoggerProxy.debug(this,
                                      "{} intercepted and not sent to client",
                                      reply);
            } else {
                getUserManager().convertAndSend(reply);
            }
        }
    }
    /**
     * Gets the category for the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return an <code>Object</code> value
     */
    private Object getCategory(Message inMessage)
    {
        if(FIXMessageUtil.isHeartbeat(inMessage)) {
            return HEARTBEAT_CATEGORY;
        }
        return this;
    }
    /**
     * Processes the given message package.
     *
     * @param inMessagePackage a <code>MessagePackage</code> value
     */
    private void addToQueue(MessagePackage inMessagePackage)
    {
        // this stream contains all messages. now, split them by session id and process further. this allows messages from different sessions to be processed at the same time.
        synchronized(sessionQueues) {
            SessionMessageProcessingQueue sessionQueue = sessionQueues.get(inMessagePackage.getSessionId());
            if(sessionQueue == null) {
                sessionQueue = new SessionMessageProcessingQueue(inMessagePackage.getSessionId());
                sessionQueue.start();
                sessionQueues.put(inMessagePackage.getSessionId(),
                                  sessionQueue);
            }
            sessionQueue.add(inMessagePackage);
        }
    }
    /**
     * Processes messages for a given session.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: QuickFIXApplication.java 17799 2018-11-21 15:06:07Z colin $
     * @since $Release$
     */
    @SuppressWarnings("unused")
    private class SessionMessageProcessingQueue
            extends QueueProcessor<MessagePackage>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(MessagePackage inMessagePackage)
                throws Exception
        {
            try {
                if(fixSessionRestoreExecutor != null && !fixSessionRestoreExecutor.isSessionLoggedOn(inMessagePackage.getSessionId())) {
                    if(inMessagePackage.getPriority() <= 0) {
                        SLF4JLoggerProxy.debug(DeployAnywhereRoutingEngine.this,
                                               "Session {} is not ready to process new messages, deferring {}",
                                               inMessagePackage.getSessionId(),
                                               inMessagePackage.getMessage());
                        add(inMessagePackage);
                        Thread.sleep(100);
                        return;
                    }
                }
                while(suspendExecutionPools.get()) {
                    synchronized(suspendExecutionPools) {
                        suspendExecutionPools.wait();
                    }
                }
                // the processor processes all messages for a given session, break it down further to all messages for a given order family *but* constrain
                //  the maximum number of messages we can process at once to the max size of the thread pool to avoid an OOM if we get 10k's of different orders at once
                OrderID rootOrderId = rootOrderIdFactory.getRootOrderId(inMessagePackage.getMessage());
                String key;
                if(rootOrderId == null) {
                    if(inMessagePackage.getMessage().isSetField(ClOrdID.FIELD)) {
                        // this message absolutely should have a root order ID
                        Messages.QF_NO_ROOT_ORDER_ID.warn(DeployAnywhereRoutingEngine.this,
                                                          inMessagePackage.getMessage());
                        return;
                    }
                    // no order family and no ClOrdId on this message - this is a "special" message and will be processed in order by the standard session queue, not a root order id queue
                    key = inMessagePackage.getSessionId().toString();
                } else {
                    try {
                        String msgType = inMessagePackage.getMessage().getHeader().getString(MsgType.FIELD);
                        if(!standardMessageTypes.contains(msgType)) {
                            // this is not a standard message (ER or CANCEL REJECT) - we'll pass it on, but use the standard session queue, not a root order id queue
                            key = inMessagePackage.getSessionId().toString();
                        } else {
                            key = rootOrderId.getValue();
                        }
                    } catch (FieldNotFound ignored) {
                        key = rootOrderId.getValue();
                    }
                }
                inMessagePackage.key = new MessageKey(inMessagePackage.getSessionId(),
                                                      key);
                MessageKey messageKey = inMessagePackage.key;
                OrderMessageProcessingQueue orderQueue = null;
                // TODO add metrics for order queues
                boolean warned = false;
                long delayStarted = 0;
                while(orderQueue == null) {
                    synchronized(orderQueues) {
                        orderQueue = orderQueues.get(messageKey);
                        if(orderQueue == null) {
                            // no order queue for this order yet
                            // TODO size might be expensive?
                            int size = orderQueues.size();
                            if(size >= maxExecutionPools) {
                                // already at max order queues, have to wait for a slot to become available
                                if(!warned) {
                                    delayStarted = System.currentTimeMillis();
                                    SLF4JLoggerProxy.info(DeployAnywhereRoutingEngine.this,
                                                          "Cannot process {} yet because max order queues ({}) in use, consider increasing the value",
                                                          inMessagePackage.message,
                                                          size);
                                    warned = true;
                                }
                            } else {
                                orderQueue = new OrderMessageProcessingQueue(messageKey);
                                orderQueue.start();
                                orderQueues.put(messageKey,
                                                orderQueue);
                            }
                        }
                    }
                    if(orderQueue == null) {
                        Thread.sleep(executionPoolDelay);
                    }
                }
                if(warned) {
                    SLF4JLoggerProxy.info(DeployAnywhereRoutingEngine.this,
                                          "Resuming work on {} after {}ms delay",
                                          inMessagePackage.message,
                                          System.currentTimeMillis() - delayStarted);
                }
                orderQueue.add(inMessagePackage);
            } catch (Exception e) {
                if(PlatformServices.isShutdown(e)) {
                    // ignore the exception and quietly allow the system to shut down
                    return;
                }
                throw e;
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(MessagePackage inData)
        {
            super.add(inData);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#size()
         */
        @Override
        protected int size()
        {
            return super.size();
        }
        /**
         * Create a new SessionMessageProcessingQueue instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         */
        private SessionMessageProcessingQueue(SessionID inSessionId)
        {
            super("SessionMessageProcessingQueue-" + inSessionId, //$NON-NLS-1$
                  new PriorityBlockingQueue<MessagePackage>());
            sessionId = inSessionId;
        }
        /**
         * session ID value for this message queue
         */
        private final SessionID sessionId;
        /**
         * indicates if a held messages warning has been issued or not
         */
        private boolean heldMessageWarning = false;
        /**
         * keeps track of the effective queue size for this session, including all order queues
         */
        private final Counter effectiveQueueSize = new Counter();
        /**
         * keeps track of the effective queue count for this session, including all order queues
         */
        private final Counter effectiveQueueCounter = new Counter();
    }
    /**
     * Processes executions for a given order.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: QuickFIXApplication.java 17799 2018-11-21 15:06:07Z colin $
     * @since $Release$
     */
    private class OrderMessageProcessingQueue
            extends QueueProcessor<MessagePackage>
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("OrderMessageProcessingQueue [").append(key).append("] ");
            return builder.toString();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(MessagePackage inData)
        {
            super.add(inData);
            timestamp = System.currentTimeMillis();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(MessagePackage inData)
                throws Exception
        {
            try {
                timestamp = System.currentTimeMillis();
                workingOnOrder.set(true);
                processMessage(inData);
            } finally {
                timestamp = System.currentTimeMillis();
                workingOnOrder.set(false);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#size()
         */
        @Override
        protected int size()
        {
            return super.size();
        }
        /**
         * Create a new OrderMessageProcessingQueue instance.
         *
         * @param inKey a <code>MessageKey</code> value
         */
        private OrderMessageProcessingQueue(MessageKey inKey)
        {
            super(StringUtils.trim("OrderMessageProcessingQueue-"+inKey));
            key = inKey;
            timeoutTask = new OrderQueueTimeoutTask(this);
            scheduledService.schedule(timeoutTask,
                                      executionPoolTtl,
                                      TimeUnit.MILLISECONDS);
            timestamp = System.currentTimeMillis();
        }
        /**
         * indicates if this order is currently being worked on or not
         */
        private final AtomicBoolean workingOnOrder = new AtomicBoolean(false);
        /**
         * holds the last time this order queue was touched
         */
        private volatile long timestamp;
        /**
         * key of this queue
         */
        private final MessageKey key;
        /**
         * task used to time out this queue
         */
        private final OrderQueueTimeoutTask timeoutTask;
    }
    /**
     * Times out an order queue if it has been unused for a period of time.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: QuickFIXApplication.java 17799 2018-11-21 15:06:07Z colin $
     * @since $Release$
     */
    private class OrderQueueTimeoutTask
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                long currentTime = System.currentTimeMillis();
                SLF4JLoggerProxy.debug(DeployAnywhereRoutingEngine.this,
                                       "Testing {} for timeout at {}",
                                       queue,
                                       currentTime);
                // TODO how sure are we that a new order couldn't be added? maybe synchronize on queue instead?
                synchronized(orderQueues) {
                    long timestamp = queue.timestamp;
                    if(!queue.workingOnOrder.get() && currentTime > timestamp+executionPoolTtl) {
                        if(queue.size() != 0) {
                            SLF4JLoggerProxy.debug(DeployAnywhereRoutingEngine.this,
                                                   "Not timing out {} because size {} is not zero",
                                                   queue,
                                                   queue.size());
                        } else {
                            SLF4JLoggerProxy.debug(DeployAnywhereRoutingEngine.this,
                                                   "Timing out {} of size {}",
                                                   queue,
                                                   queue.size());
                            orderQueues.remove(queue.key);
                            queue.stop();
                            return;
                        }
                    }
                }
                scheduledService.schedule(queue.timeoutTask,
                                          executionPoolTtl,
                                          TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                          e,
                                          "{} on execution process: {}",
                                          queue.key,
                                          ExceptionUtils.getRootCauseMessage(e));
                } else {
                    SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                          "{} on execution process: {}",
                                          queue.key,
                                          ExceptionUtils.getRootCauseMessage(e));
                }
            }
        }
        /**
         * Create a new OrderQueueTimeoutTask instance.
         *
         * @param inOrderMessageProcessingQueue an <code>OrderMessageProcessingQueue</code> value
         */
        private OrderQueueTimeoutTask(OrderMessageProcessingQueue inOrderMessageProcessingQueue)
        {
            queue = inOrderMessageProcessingQueue;
        }
        /**
         * queue for processing orders
         */
        private final OrderMessageProcessingQueue queue;
    }
    /**
     * Indicates the type of message.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: QuickFIXApplication.java 17799 2018-11-21 15:06:07Z colin $
     * @since 2.1.4
     */
    @ClassVersion("$Id: QuickFIXApplication.java 17799 2018-11-21 15:06:07Z colin $")
    private enum MessageType
    {
        FROM_ADMIN,
        FROM_APP
    }
    /**
     * Encapsulates a message to be processed.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: QuickFIXApplication.java 17799 2018-11-21 15:06:07Z colin $
     * @since 2.1.4
     */
    @ClassVersion("$Id: QuickFIXApplication.java 17799 2018-11-21 15:06:07Z colin $")
    private static class MessagePackage
            implements Serializable, Comparable<MessagePackage>
    {
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(MessagePackage inO)
        {
            return new CompareToBuilder().append(inO.priority,priority).append(id,inO.id).toComparison();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(priority).append(id).toHashCode();
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
            if (!(obj instanceof MessagePackage)) {
                return false;
            }
            MessagePackage other = (MessagePackage) obj;
            return new EqualsBuilder().append(priority,other.priority).append(id,other.id).isEquals();
        }
        /**
         * Create a new MessagePackage instance.
         *
         * @param inMessage a <code>Message</code> value
         * @param inMessageType a <code>MessageType</code> value
         * @param inSessionId a <code>SessionID</code> value
         * @param inHierarchy a <code>Hierarchy</code> value
         * @param inPriority an <code>int</code> value
         */
        private MessagePackage(Message inMessage,
                               MessageType inMessageType,
                               SessionID inSessionId,
                               Hierarchy inHierarchy,
                               int inPriority)
        {
            message = inMessage;
            messageType = inMessageType;
            sessionId = inSessionId;
            hierarchy = inHierarchy;
            priority = inPriority;
        }
        /**
         * Gets the <code>Message</code> value.
         *
         * @return a <code>Message</code> value
         */
        private Message getMessage()
        {
            return message;
        }
        /**
         * Gets the <code>MessageType</code> value.
         *
         * @return a <code>MessageType</code> value
         */
        private MessageType getMessageType()
        {
            return messageType;
        }
        /**
         * Gets the <code>SessionID</code> value.
         *
         * @return a <code>SessionID</code> value
         */
        private SessionID getSessionId()
        {
            return sessionId;
        }
        /**
         * Get the hierarchy value.
         *
         * @return a <code>Hierarchy</code> value
         */
        private Hierarchy getHierarchy()
        {
            return hierarchy;
        }
        /**
         * Get the priority value.
         *
         * @return an <code>int</code> value
         */
        private int getPriority()
        {
            return priority;
        }
        /**
         * key value
         */
        private MessageKey key;
        /**
         * message value
         */
        private final Message message;
        /**
         * message type value
         */
        private final MessageType messageType;
        /**
         * session ID value
         */
        private final SessionID sessionId;
        /**
         * message hierarchy value
         */
        private final Hierarchy hierarchy;
        /**
         * message priority value
         */
        private final int priority;
        /**
         * message counter
         */
        private final long id = counter.incrementAndGet();
        /**
         * counter used to uniquely and sequentially identify messages
         */
        private static final AtomicLong counter = new AtomicLong(0);
        private static final long serialVersionUID = 5052044328496633612L;
    }
    /**
     * Uniquely identifies a family of orders.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: QuickFIXApplication.java 17799 2018-11-21 15:06:07Z colin $
     * @since $Release$
     */
    private static class MessageKey
            extends Pair<SessionID,String>
    {
        /**
         * Create a new OrderKey instance.
         *
         * @param inSessionId a <code>SessionID</code> value
         * @param inKeyValue a <code>String</code> value
         */
        private MessageKey(SessionID inSessionId,
                           String inKeyValue)
        {
            super(inSessionId,
                  inKeyValue);
        }
    }
    /**
     * indicates the "normal" message types we handle
     */
    private static final Set<String> standardMessageTypes = Sets.newHashSet(MsgType.ORDER_CANCEL_REJECT,MsgType.EXECUTION_REPORT);
    /**
     * messages that should not be forwarded to clients (empty to forward all messages)
     */
    private final Set<String> dontForwardMessages = Sets.newHashSet(MsgType.LOGON,MsgType.HEARTBEAT,MsgType.TEST_REQUEST,MsgType.RESEND_REQUEST,MsgType.REJECT,MsgType.SEQUENCE_RESET,MsgType.LOGOUT);
    /**
     * logger category used for heartbeats
     */
    private static final String HEARTBEAT_CATEGORY = DeployAnywhereRoutingEngine.class.getName()+".HEARTBEATS"; //$NON-NLS-1$
    /**
     * sessions that have been created
     */
    private final Set<SessionID> activeSessions = new CopyOnWriteArraySet<>();
    /**
     * session queues by session ID
     */
    private final Map<SessionID,SessionMessageProcessingQueue> sessionQueues = new HashMap<>();
    /**
     * holds processing queues for root order id
     */
    private final Map<MessageKey,OrderMessageProcessingQueue> orderQueues = new HashMap<>();
    /**
     * indicates if the object is actively running as opposed to starting, shutting down, or stopped
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    /**
     * indicates if execution pools are allowed to be allocated or not
     */
    private final AtomicBoolean suspendExecutionPools = new AtomicBoolean(false);
    /**
     * holds those interested in broker status updates
     */
    private final Queue<BrokerStatusListener> brokerStatusListeners = new ConcurrentLinkedQueue<>();
    /**
     * executes jobs at scheduled times
     */
    private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(2);
    /**
     * supported messages value
     */
    private MessageFilter supportedMessages;
    /**
     * maximum number of order pools to have active at once
     */
    private int maxExecutionPools = 5;
    /**
     * interval to wait between checks for available order pools
     */
    private long executionPoolDelay = 10;
    /**
     * number of milliseconds to leave an order pool alive before retiring it
     */
    private long executionPoolTtl = 1000;
    /**
     * identifies this cluster instance
     */
    private ClusterData clusterData;
    /**
     * directory name into which FIX injector files will be dropped, may be <code>null</code>
     */
    private String fixInjectorDirectory;
    /**
     * determines whether we allow the redeliverToCompID flag or not
     */
    private boolean allowDeliverToCompID = false;
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * provides access to report services 
     */
    @Autowired
    private ReportService reportService;
    /**
     * restores FIX sessions, if specified
     */
    @Autowired(required=false)
    private FixSessionRestoreExecutor<PrioritizedMessageSessionRestorePayload,SessionRestorePayload> fixSessionRestoreExecutor;
    /**
     * constructs a FIX settings provider object
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * determines the root order id for a given message
     */
    @Autowired
    private RootOrderIdFactory rootOrderIdFactory;
    /**
     * provides access to outgoing message services
     */
    @Autowired
    private OutgoingMessageService outgoingMessageService;
}
