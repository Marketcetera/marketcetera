package org.marketcetera.ors;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.marketcetera.client.BrokerStatusListener;
import org.marketcetera.client.BrokerStatusPublisher;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.Pair;
import org.marketcetera.core.QueueProcessor;
import org.marketcetera.core.file.DirectoryWatcherImpl;
import org.marketcetera.core.file.DirectoryWatcherSubscriber;
import org.marketcetera.core.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.ClusteredBrokerStatus;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributes;
import org.marketcetera.fix.FixSessionDay;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.SessionRestorePayload;
import org.marketcetera.fix.SessionRestorePayloadHandler;
import org.marketcetera.fix.SessionService;
import org.marketcetera.metrics.IsotopeService;
import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.ors.brokers.BrokerService;
import org.marketcetera.ors.brokers.FixSessionRestoreExecutor;
import org.marketcetera.ors.brokers.impl.BrokerServiceImpl;
import org.marketcetera.ors.config.LogonAction;
import org.marketcetera.ors.config.LogoutAction;
import org.marketcetera.ors.dao.PersistentReportDao;
import org.marketcetera.ors.dao.ReportService;
import org.marketcetera.ors.filters.MessageFilter;
import org.marketcetera.ors.history.RootOrderIdFactory;
import org.marketcetera.ors.info.RequestInfo;
import org.marketcetera.ors.info.RequestInfoImpl;
import org.marketcetera.ors.info.SessionInfo;
import org.marketcetera.ors.info.SessionInfoImpl;
import org.marketcetera.ors.info.SystemInfo;
import org.marketcetera.ors.outgoingorder.OutgoingMessageService;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.quickfix.SessionStatus;
import org.marketcetera.quickfix.SessionStatusListener;
import org.marketcetera.quickfix.SessionStatusPublisher;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.FIXMessageSupport;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsOperations;

import quickfix.ApplicationExtended;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.BeginString;
import quickfix.field.ClOrdID;
import quickfix.field.DeliverToCompID;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.RefMsgType;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.SessionRejectReason;
import quickfix.field.TargetCompID;
import quickfix.field.Text;
import quickfix.field.TradSesStatus;

import com.codahale.metrics.Counter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.OperationTimeoutException;

/* $License$ */

/**
 * The QuickFIX/J intermediary, intercepting messages from/to the
 * QuickFIX/J counterparties and the ORS.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class QuickFIXApplication
        implements ApplicationExtended, ReportReceiver, BrokerStatusPublisher, SessionStatusPublisher, DirectoryWatcherSubscriber
{
    /**
     * Create a new QuickFIXApplication instance.
     */
    public QuickFIXApplication() {}
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(clusterService);
        Validate.notNull(reportService);
        Validate.notNull(brokerService);
        clusterData = getClusterService().getInstanceData();
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
                    SLF4JLoggerProxy.debug(QuickFIXApplication.this,
                                           "Cluster in transition, cannot update fix session attributes, will try again shortly");
                } catch (Exception e) {
                    SLF4JLoggerProxy.debug(QuickFIXApplication.this,
                                           e,
                                           "Cluster in transition, cannot update fix session attributes, will try again shortly");
                }
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
        isRunning.set(true);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onCreate(quickfix.SessionID)
     */
    @Override
    public void onCreate(final SessionID inSessionId)
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
                            SLF4JLoggerProxy.warn(QuickFIXApplication.this,
                                                  e);
                        }
                    }
                });
            }
        } finally {
            FixSession fixSession = brokerService.findFixSessionBySessionId(inSessionId);
            if(fixSession == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Ignoring onCreate for missing session {}",
                                      sessionName);
            } else {
                updateStatus(fixSession,
                             false);
            }
        }
    }
    /* (non-Javadoc)
     * @see quickfix.ApplicationExtended#canLogon(quickfix.SessionID)
     */
    @Override
    public boolean canLogon(SessionID inSessionId)
    {
        if(sessionService.isSessionTime(inSessionId)) {
            return true;
        }
        SLF4JLoggerProxy.debug(this,
                               "{} is not current, no logon at this time",
                               inSessionId);
        return false;
    }
    /* (non-Javadoc)
     * @see quickfix.ApplicationExtended#onBeforeSessionReset(quickfix.SessionID)
     */
    @Override
    public void onBeforeSessionReset(SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} resetting session",
                               inSessionId);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogon(quickfix.SessionID)
     */
    @Override
    public void onLogon(final SessionID inSessionId)
    {
        if(!isRunning.get()) {
            return;
        }
        String sessionName = brokerService.getSessionName(inSessionId);
        SLF4JLoggerProxy.info(FIXMessageUtil.FIX_RESTORE_LOGGER_NAME,
                              "Initiator {} logged on",
                              sessionName);
        FixSession fixSession = brokerService.findFixSessionBySessionId(inSessionId);
        if(fixSession == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Ignoring logon from missing session {}",
                                  sessionName);
            return;
        }
        updateStatus(fixSession,
                     true);
        Broker broker = brokerService.generateBroker(fixSession);
        if(broker.getSpringBroker().getLogonActions() != null) {
            for(LogonAction action : broker.getSpringBroker().getLogonActions()) {
                try {
                    action.onLogon(broker,
                                   getSender());
                } catch (Exception e) {
                    if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                        SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                              e,
                                              "{} on logon: {}",
                                              sessionName,
                                              ExceptionUtils.getRootCauseMessage(e));
                    } else {
                        SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                              "{} on logon: {}",
                                              sessionName,
                                              ExceptionUtils.getRootCauseMessage(e));
                    }
                }
            }
        }
        SessionStatus status = new SessionStatus(inSessionId,
                                                 true,
                                                 true);
        for(SessionStatusListener listener : sessionStatusListeners) {
            try {
                listener.receiveSessionStatus(status);
            } catch (Exception e) {
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                          e,
                                          "{} on session status: {}",
                                          sessionName,
                                          ExceptionUtils.getRootCauseMessage(e));
                } else {
                    SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                          "{} on session status: {}",
                                          sessionName,
                                          ExceptionUtils.getRootCauseMessage(e));
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogout(quickfix.SessionID)
     */
    @Override
    public void onLogout(SessionID inSessionId)
    {
        if(!isRunning.get()) {
            return;
        }
        String sessionName = brokerService.getSessionName(inSessionId);
        SLF4JLoggerProxy.info(FIXMessageUtil.FIX_RESTORE_LOGGER_NAME,
                              "{} logged out",
                              sessionName);
        try {
            FixSession fixSession = brokerService.findFixSessionBySessionId(inSessionId);
            if(fixSession == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Ignoring logout from missing session {}",
                                      inSessionId);
                return;
            }
            updateStatus(fixSession,
                         false);
            Broker broker = brokerService.generateBroker(fixSession);
            Collection<LogoutAction> logoutActions = broker.getSpringBroker().getLogoutActions();
            if(logoutActions != null) {
                for(LogoutAction action : logoutActions) {
                    try {
                        action.onLogout(broker,
                                        getSender());
                    } catch (Exception e) {
                        if(isShutdown(e)) {
                            throw e;
                        }
                        if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                            SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                                  e,
                                                  "{} on log out: {}",
                                                  sessionName,
                                                  ExceptionUtils.getRootCauseMessage(e));
                        } else {
                            SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                                  "{} on log out: {}",
                                                  sessionName,
                                                  ExceptionUtils.getRootCauseMessage(e));
                        }
                    }
                }
            }
            if(fixSessionRestoreExecutor != null) {
                fixSessionRestoreExecutor.sessionLogout(inSessionId);
            }
            SessionStatus status = new SessionStatus(inSessionId,
                                                     true,
                                                     false);
            for(SessionStatusListener listener : sessionStatusListeners) {
                try {
                    listener.receiveSessionStatus(status);
                } catch (Exception e) {
                    if(isShutdown(e)) {
                        throw e;
                    }
                    if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                        SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                              e,
                                              "{} on session status: {}",
                                              sessionName,
                                              ExceptionUtils.getRootCauseMessage(e));
                    } else {
                        SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                              "{} on session status: {}",
                                              sessionName,
                                              ExceptionUtils.getRootCauseMessage(e));
                    }
                }
            }
        } catch (Exception e) {
            if(isShutdown(e)) {
                // this exception can be safely ignored
                return;
            }
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                      e,
                                      "{} on log out: {}",
                                      sessionName,
                                      ExceptionUtils.getRootCauseMessage(e));
            } else {
                SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                      "{} on log out: {}",
                                      sessionName,
                                      ExceptionUtils.getRootCauseMessage(e));
            }
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toAdmin(Message inMessage,
                        SessionID inSessionId)
    {
        if(!isRunning.get()) {
            return;
        }
        Broker broker = brokerService.getBroker(inSessionId);
        if(broker == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Ignoring toAdmin for {} from missing session {}",
                                  inMessage,
                                  brokerService.getSessionName(inSessionId));
            return;
        }
        // Apply message modifiers.
        if(broker.getModifiers() != null) {
            try {
                RequestInfo requestInfo = new RequestInfoImpl(new SessionInfoImpl(getSystemInfo()));
                requestInfo.setValue (RequestInfo.BROKER,
                                      broker);
                requestInfo.setValue(RequestInfo.BROKER_ID,
                                     broker.getBrokerID());
                requestInfo.setValue(RequestInfo.FIX_MESSAGE_FACTORY,
                                     broker.getFIXMessageFactory());
                requestInfo.setValue(RequestInfo.CURRENT_MESSAGE,
                                     inMessage);
                broker.getModifiers().modifyMessage(requestInfo);
                inMessage=requestInfo.getValueIfInstanceOf(RequestInfo.CURRENT_MESSAGE,
                                                           Message.class);
            } catch (OrderIntercepted e) {
                SLF4JLoggerProxy.debug(this,
                                       "{} has been intercepted",
                                       inMessage);
                return;
            } catch (I18NException ex) {
                Messages.QF_MODIFICATION_FAILED.warn(getCategory(inMessage),
                                                     ex,
                                                     inMessage,
                                                     broker.toString());
            }
        }
        if(broker.getPreSendModifiers() != null) {
            try {
                RequestInfo requestInfo = new RequestInfoImpl(new SessionInfoImpl(getSystemInfo()));
                requestInfo.setValue (RequestInfo.BROKER,
                                      broker);
                requestInfo.setValue(RequestInfo.BROKER_ID,
                                     broker.getBrokerID());
                requestInfo.setValue(RequestInfo.FIX_MESSAGE_FACTORY,
                                     broker.getFIXMessageFactory());
                requestInfo.setValue(RequestInfo.CURRENT_MESSAGE,
                                     inMessage);
                broker.getPreSendModifiers().modifyMessage(requestInfo);
                inMessage=requestInfo.getValueIfInstanceOf(RequestInfo.CURRENT_MESSAGE,
                                                           Message.class);
            } catch (OrderIntercepted e) {
                SLF4JLoggerProxy.debug(this,
                                       "{} has been intercepted",
                                       inMessage);
                return;
            } catch (I18NException ex) {
                Messages.QF_MODIFICATION_FAILED.warn(getCategory(inMessage),
                                                     ex,
                                                     inMessage,
                                                     broker.toString());
            }
        }
        Object category = getCategory(inMessage);
        boolean shouldDisplayMessage = !category.equals(HEARTBEAT_CATEGORY) || SLF4JLoggerProxy.isDebugEnabled(HEARTBEAT_CATEGORY);
        if(shouldDisplayMessage) {
            Messages.QF_TO_ADMIN.info(category,
                                      inMessage,
                                      broker);
            broker.logMessage(inMessage);
        }
        // If the QuickFIX/J engine is sending a reject (e.g. the
        // counterparty sent us a malformed execution report, for
        // example, and we are rejecting it), we notify the client of
        // the rejection.
        if (FIXMessageUtil.isReject(inMessage)) {
            try {
                String msgType=(inMessage.isSetField(MsgType.FIELD)?null:inMessage.getString(RefMsgType.FIELD));
                String msgTypeName=broker.getFIXDataDictionary().
                    getHumanFieldValue(MsgType.FIELD, msgType);
                inMessage.setString(Text.FIELD,Messages.QF_IN_MESSAGE_REJECTED.
                              getText(msgTypeName,inMessage.getString(Text.FIELD)));
            } catch (FieldNotFound ex) {
                Messages.QF_MODIFICATION_FAILED.warn(getCategory(inMessage),
                                                     ex,
                                                     inMessage,
                                                     broker.toString());
                // Send original message instead of modified one.
            }
            sendToClientTrades(true,broker,inMessage,Originator.Server,Hierarchy.Flat);
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromAdmin(Message inMessage,
                          SessionID inSessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
    {
        if(!isRunning.get()) {
            return;
        }
        if(FIXMessageUtil.isLogon(inMessage)) {
            if(!isSessionTime(inSessionId)) {
                FixSession session = brokerService.findFixSessionBySessionId(inSessionId);
                if(session == null) {
                    throw new RejectLogon(inSessionId + " is not a known session");
                }
                ClusteredBrokerStatus brokerStatus = brokerService.generateBrokerStatus(session,
                                                                                        clusterData,
                                                                                        FixSessionStatus.DISCONNECTED,
                                                                                        false);
                brokerService.reportBrokerStatus(brokerStatus);
                throw new RejectLogon(inSessionId + " logon is not allowed");
            }
        }
        isotopeService.inject(inMessage);
        addToQueue(new MessagePackage(inMessage,
                                      MessageType.FROM_ADMIN,
                                      inSessionId,
                                      Hierarchy.Flat,
                                      0));
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toApp(Message inMessage,
                      SessionID inSessionId)
        throws DoNotSend
    {
        if(!isRunning.get()) {
            return;
        }
        Broker broker = brokerService.getBroker(inSessionId);
        if(broker == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Ignoring toApp for {} from missing session {}",
                                  inMessage,
                                  brokerService.getSessionName(inSessionId));
            return;
        }
        isotopeService.remove(inMessage);
        Messages.QF_TO_APP.info(getCategory(inMessage),inMessage,broker);
        broker.logMessage(inMessage);
        try {
            rootOrderIdFactory.receiveOutgoingMessage(inMessage);
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromApp(Message inMessage,
                        SessionID inSessionId)
            throws UnsupportedMessageType
    {
        if(!isRunning.get()) {
            return;
        }
        doFromApp(inMessage,
                  inSessionId,
                  0);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.OrderReceiver#addReport(org.marketcetera.trade.ReportBase)
     */
    @Override
    public void addReport(ReportBase inReport)
    {
        if(!isRunning.get()) {
            return;
        }
        SLF4JLoggerProxy.debug(this,
                               "Manually adding {}", //$NON-NLS-1$
                               inReport);
        if(!(inReport instanceof FIXMessageSupport)) {
            throw new UnsupportedOperationException();
        }
        FixSession fixSession = brokerService.findFixSessionByBrokerId(inReport.getBrokerID());
        if(fixSession == null) {
            throw new IllegalArgumentException(Messages.QF_UNKNOWN_BROKER_ID.getText(inReport.getBrokerID()));
        }
        Broker broker = brokerService.generateBroker(fixSession);
        SessionID sessionId = new SessionID(fixSession.getSessionId());
        Message msg = ((FIXMessageSupport)inReport).getMessage();
        try {
            // need to modify message version of this message to match the broker's
            msg.getHeader().setField(new BeginString(sessionId.getBeginString()));
            // invert the target and sender because the message is supposed to have come *from* the target *to* the sender
            msg.getHeader().setField(new SenderCompID(sessionId.getTargetCompID()));
            msg.getHeader().setField(new TargetCompID(sessionId.getSenderCompID()));
            // mark these messages as stinkers if there's ever any question about the data
            msg.getHeader().setField(new MsgSeqNum(seqCounter.incrementAndGet()));
            if(!msg.getHeader().isSetField(SendingTime.FIELD)) {
                msg.getHeader().setField(new SendingTime(new Date()));
            }
            // recalculate checksum and length
            String newMessageValue = msg.toString();
            SLF4JLoggerProxy.debug(this,
                                   "Message converted to {}", //$NON-NLS-1$
                                   newMessageValue);
            // validate fix message with the broker's dictionary
            broker.getDataDictionary().validate(msg);
        } catch (IncorrectTagValue e) {
            throw new IllegalArgumentException(Messages.QF_CANNOT_ADD_INVALID_REPORT.getText(ExceptionUtils.getRootCauseMessage(e)));
        } catch (FieldNotFound e) {
            throw new IllegalArgumentException(Messages.QF_CANNOT_ADD_INVALID_REPORT.getText(ExceptionUtils.getRootCauseMessage(e)));
        } catch (IncorrectDataFormat e) {
            throw new IllegalArgumentException(Messages.QF_CANNOT_ADD_INVALID_REPORT.getText(ExceptionUtils.getRootCauseMessage(e)));
        }
        Messages.QF_FROM_APP.info(getCategory(msg),
                                  msg,
                                  broker);
        addToQueue(new MessagePackage(msg,
                                      MessageType.FROM_APP,
                                      sessionId,
                                      inReport.getHierarchy(),
                                      0));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.OrderReceiver#deleteReport(org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void deleteReport(ReportBase inReport)
    {
        if(!isRunning.get()) {
            return;
        }
        SLF4JLoggerProxy.debug(this,
                               "Deleting {}", //$NON-NLS-1$
                               inReport);
        getPersister().deleteMessage(inReport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.BrokerStatusPublisher#addBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inListener)
    {
        brokerStatusListeners.add(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.BrokerStatusPublisher#removeBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inListener)
    {
        brokerStatusListeners.remove(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.SessionStatusPublisher#addSessionStatusListener(org.marketcetera.quickfix.SessionStatusListener)
     */
    @Override
    public void addSessionStatusListener(SessionStatusListener inSessionStatusListener)
    {
        sessionStatusListeners.add(inSessionStatusListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.SessionStatusPublisher#removeSessionStatusListener(org.marketcetera.quickfix.SessionStatusListener)
     */
    @Override
    public void removeSessionStatusListener(SessionStatusListener inSessionStatusListener)
    {
        sessionStatusListeners.remove(inSessionStatusListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.file.DirectoryWatcherSubscriber#received(java.io.File, java.lang.String)
     */
    @Override
    public void received(File inFile,
                         String inOriginalFileName)
    {
        SLF4JLoggerProxy.info(this,
                              "Received FIX injector file: {}",
                              inOriginalFileName);
        try {
            for(String line : FileUtils.readLines(inFile)) {
                try {
                    Message message = getMessageFromLine(line);
                    if(message == null) {
                        SLF4JLoggerProxy.info(this,
                                              "Not injecting {}",
                                              line);
                        continue;
                    }
                    SessionID sessionId = getInjectionSessionIdFrom(message);
                    String msgType = message.getHeader().getString(MsgType.FIELD);
                    if(standardMessageTypes.contains(msgType)) {
                        SLF4JLoggerProxy.info(this,
                                              "{} injecting {}",
                                              sessionId,
                                              message);
                        doFromApp(message,
                                  sessionId,
                                  1);
                    } else {
                        SLF4JLoggerProxy.info(this,
                                              "{} not injecting {}",
                                              sessionId,
                                              message);
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          "On file process: {}",
                                          ExceptionUtils.getRootCauseMessage(e));
                }
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "On file process: {}",
                                  ExceptionUtils.getRootCauseMessage(e));
        }
    }
    /**
     * Suspends execution processing.
     */
    public void suspendExecutionProcessing()
    {
        SLF4JLoggerProxy.debug(this,
                               "Execution pools suspended");
        suspendExecutionPools.set(true);
    }
    /**
     * Resumes execution processing.
     */
    public void resumeExecutionProcessing()
    {
        SLF4JLoggerProxy.debug(this,
                               "Execution pools resumed");
        suspendExecutionPools.set(false);
        synchronized(suspendExecutionPools) {
            suspendExecutionPools.notifyAll();
        }
    }
    /**
     * Gets the system info value.
     *
     * @return a <code>SystemInfo</code> value
     */
    public SystemInfo getSystemInfo()
    {
        return mSystemInfo;
    }
    /**
     * Get the productKey value.
     *
     * @return a <code>String</code> value
     */
    public String getProductKey()
    {
        return productKey;
    }
    /**
     * Sets the productKey value.
     *
     * @param a <code>String</code> value
     */
    public void setProductKey(String inProductKey)
    {
        productKey = inProductKey;
    }
    /**
     * Sets the systemInfo value.
     *
     * @param a <code>SystemInfo</code> value
     */
    public void setSystemInfo(SystemInfo inSystemInfo)
    {
        mSystemInfo = inSystemInfo;
    }
    /**
     * Sets the supportedMessages value.
     *
     * @param a <code>MessageFilter</code> value
     */
    public void setSupportedMessages(MessageFilter inSupportedMessages)
    {
        mSupportedMessages = inSupportedMessages;
    }
    /**
     * Sets the persister value.
     *
     * @param a <code>ReplyPersister</code> value
     */
    public void setPersister(ReplyPersister inPersister)
    {
        mPersister = inPersister;
    }
    /**
     * Sets the sender value.
     *
     * @param a <code>QuickFIXSender</code> value
     */
    public void setSender(QuickFIXSender inSender)
    {
        mSender = inSender;
    }
    /**
     * Sets the userManager value.
     *
     * @param a <code>UserManager</code> value
     */
    public void setUserManager(UserManager inUserManager)
    {
        mUserManager = inUserManager;
    }
    /**
     * Sets the toClientStatus value.
     *
     * @param a <code>JmsOperations</code> value
     */
    public void setToClientStatus(JmsOperations inToClientStatus)
    {
        mToClientStatus = inToClientStatus;
    }
    /**
     * Sets the toTradeRecorder value.
     *
     * @param a <code>JmsOperations</code> value
     */
    public void setToTradeRecorder(JmsOperations inToTradeRecorder)
    {
        mToTradeRecorder = inToTradeRecorder;
    }
    /**
     * Gets the supported messages value.
     *
     * @return a <code>MessageFilter</code> value
     */
    public MessageFilter getSupportedMessages()
    {
        return mSupportedMessages;
    }
    /**
     * Gets the reply persister value.
     *
     * @return a <code>ReplyPersister</code> value
     */
    public ReplyPersister getPersister()
    {
        return mPersister;
    }
    /**
     * Gets the QuickFIXSender value.
     *
     * @return a <code>QuickFIXSender</code> value
     */
    public QuickFIXSender getSender()
    {
        return mSender;
    }
    /**
     * Gets the user manager value.
     *
     * @return a <code>UserManager</code> value
     */
    public UserManager getUserManager()
    {
        return mUserManager;
    }
    /**
     * Gets the client status value.
     *
     * @return a <code>JmsOperations</code> value
     */
    public JmsOperations getToClientStatus()
    {
        return mToClientStatus;
    }
    /**
     * Gets the trade recorder value.
     *
     * @return a <code>JmsOperations</code> value
     */
    public JmsOperations getToTradeRecorder()
    {
        return mToTradeRecorder;
    }
    /**
     * Get the reportService value.
     *
     * @return a <code>ReportService</code> value
     */
    public ReportService getReportService()
    {
        return reportService;
    }
    /**
     * Sets the reportService value.
     *
     * @param inReportService a <code>ReportService</code> value
     */
    public void setReportService(ReportService inReportService)
    {
        reportService = inReportService;
    }
    /**
     * Get the fixSettingsProviderFactory value.
     *
     * @return a <code>FixSettingsProviderFactory</code> value
     */
    public FixSettingsProviderFactory getFixSettingsProviderFactory()
    {
        return fixSettingsProviderFactory;
    }
    /**
     * Sets the fixSettingsProviderFactory value.
     *
     * @param inFixSettingsProviderFactory a <code>FixSettingsProviderFactory</code> value
     */
    public void setFixSettingsProviderFactory(FixSettingsProviderFactory inFixSettingsProviderFactory)
    {
        fixSettingsProviderFactory = inFixSettingsProviderFactory;
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
     * Get the sessionService value.
     *
     * @return a <code>SessionService</code> value
     */
    public SessionService getSessionService()
    {
        return sessionService;
    }
    /**
     * Sets the sessionService value.
     *
     * @param inSessionService a <code>SessionService</code> value
     */
    public void setSessionService(SessionService inSessionService)
    {
        sessionService = inSessionService;
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
     * Get the forwardMessages value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getForwardMessages()
    {
        return forwardMessages;
    }
    /**
     * Sets the forwardMessages value.
     *
     * @param inForwardMessages a <code>Set&lt;String&gt;</code> value
     */
    public void setForwardMessages(Set<String> inForwardMessages)
    {
        forwardMessages.clear();
        if(forwardMessages != null) {
            forwardMessages.addAll(forwardMessages);
        }
    }
    /**
     * Get the rootOrderIdFactory value.
     *
     * @return a <code>RootOrderIdFactory</code> value
     */
    public RootOrderIdFactory getRootOrderIdFactory()
    {
        return rootOrderIdFactory;
    }
    /**
     * Sets the rootOrderIdFactory value.
     *
     * @param a <code>RootOrderIdFactory</code> value
     */
    public void setRootOrderIdFactory(RootOrderIdFactory inRootOrderIdFactory)
    {
        rootOrderIdFactory = inRootOrderIdFactory;
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
     * Get the reportDao value.
     *
     * @return a <code>PersistentReportDao</code> value
     */
    public PersistentReportDao getReportDao()
    {
        return reportDao;
    }
    /**
     * Sets the reportDao value.
     *
     * @param inReportDao a <code>PersistentReportDao</code> value
     */
    public void setReportDao(PersistentReportDao inReportDao)
    {
        reportDao = inReportDao;
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
     * Get the fixSessionRestoreExecutor value.
     *
     * @return a <code>FixSessionRestoreExecutor</code> value
     */
    public FixSessionRestoreExecutor<PrioritizedMessageSessionRestorePayload,SessionRestorePayload> getFixSessionRestoreExecutor()
    {
        return fixSessionRestoreExecutor;
    }
    /**
     * Sets the fixSessionRestoreExecutor value.
     *
     * @param inFixSessionRestoreExecutor a <code>FixSessionRestoreExecutor</code> value
     */
    public void setFixSessionRestoreExecutor(FixSessionRestoreExecutor<PrioritizedMessageSessionRestorePayload,SessionRestorePayload> inFixSessionRestoreExecutor)
    {
        fixSessionRestoreExecutor = inFixSessionRestoreExecutor;
    }
    /**
     * Get the outgoingMessageService value.
     *
     * @return an <code>OutgoingMessageService</code> value
     */
    public OutgoingMessageService getOutgoingMessageService()
    {
        return outgoingMessageService;
    }
    /**
     * Sets the outgoingMessageService value.
     *
     * @param an <code>OutgoingMessageService</code> value
     */
    public void setOutgoingMessageService(OutgoingMessageService inOutgoingMessageService)
    {
        outgoingMessageService = inOutgoingMessageService;
    }
    /**
     * Stops the object.
     */
    void stop()
    {
        isRunning.set(false);
        for(OrderMessageProcessingQueue orderQueue : orderQueues.values()) {
            try {
                orderQueue.stop();
            } catch (Exception ignored) {}
        }
        for(SessionMessageProcessingQueue sessionQueue : sessionQueues.values()) {
            try {
                sessionQueue.stop();
            } catch (Exception ignored) {}
        }
    }
    /**
     * Gets the SessionID value appropriate for FIX injection from the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>SessionID</code> value
     * @throws FieldNotFound if the session ID cannot be constructed
     */
    private SessionID getInjectionSessionIdFrom(Message inMessage)
            throws FieldNotFound
    {
        String beginString = inMessage.getHeader().getString(BeginString.FIELD);
        String senderCompId = inMessage.getHeader().getString(SenderCompID.FIELD);
        String targetCompId = inMessage.getHeader().getString(TargetCompID.FIELD);
        return new SessionID(beginString,
                             targetCompId,
                             senderCompId);
    }
    /**
     * Gets the FIX message from the given string, if any.
     *
     * @param inLine a <code>String</code> value
     * @return a <code>Message</code> value or <code>null</code>
     * @throws InvalidMessage if the message cannot be constructed
     */
    private Message getMessageFromLine(String inLine)
            throws InvalidMessage
    {
        if(inLine.contains("8=")) {
            return new Message(inLine.substring(inLine.indexOf("8=")));
        } else {
            return null;
        }
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
        isotopeService.inject(inMessage);
        Broker b=brokerService.getBroker(inSessionId);
        Messages.QF_FROM_APP.info(getCategory(inMessage),inMessage,b);
        b.logMessage(inMessage);
        // Accept only certain message types.
        if(getSupportedMessages() != null && !getSupportedMessages().isAccepted(inMessage)){
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
     * Determine if the given session should be active or not at this time.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>boolean</code> value
     */
    private boolean isSessionTime(SessionID inSessionId)
    {
        FixSession session = brokerService.findFixSessionBySessionId(inSessionId);
        if(session == null) {
            return false;
        }
        String rawDaysValue = StringUtils.trimToNull(session.getSessionSettings().get(BrokerServiceImpl.sessionDaysKey));
        if(rawDaysValue == null) {
            SLF4JLoggerProxy.debug(this,
                                   "{} has no specified active days",
                                   inSessionId);
        } else {
            Date startOfSession = brokerService.getSessionStart(inSessionId);
            if(startOfSession == null) {
                SLF4JLoggerProxy.debug(this,
                                       "Unable to calculate start of session for {}, using now",
                                       inSessionId);
                startOfSession = new Date();
            }
            DateTime now = new DateTime(startOfSession);
            int today = now.getDayOfWeek();
            int daysValue = Integer.parseInt(rawDaysValue);
            FixSessionDay fixSessionDay = FixSessionDay.values()[today-1];
            if(fixSessionDay.isActiveToday(daysValue)) {
                SLF4JLoggerProxy.debug(this,
                                       "{} is active {} from {}",
                                       inSessionId,
                                       fixSessionDay,
                                       daysValue);
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "{} is *not* active on {} from {}",
                                       inSessionId,
                                       fixSessionDay,
                                       daysValue);
                return false;
            }
        }
        Session activeSession = Session.lookupSession(inSessionId);
        if(activeSession == null) {
            return false;
        }
        return activeSession.isSessionTime();
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
                if(isSessionTime(new SessionID(inFixSession.getSessionId()))) {
                    status = FixSessionStatus.NOT_CONNECTED;
                } else {
                    status = FixSessionStatus.DISCONNECTED;
                }
            }
        }
        ClusteredBrokerStatus brokerStatus = brokerService.generateBrokerStatus(inFixSession,
                                                                                clusterData,
                                                                                status,
                                                                                inStatus);
        brokerService.reportBrokerStatus(brokerStatus);
//        if(oldStatus == inStatus) {
//            return;
//        }
        // TODO is this crap necessary?
        Broker broker = brokerService.generateBroker(inFixSession);
        Messages.QF_SENDING_STATUS.info(this,
                                        inStatus,
                                        broker);
        if(getToClientStatus()==null) {
            return;
        }
        getToClientStatus().convertAndSend(brokerStatus);
        for(BrokerStatusListener listener : brokerStatusListeners) {
            listener.receiveBrokerStatus(brokerStatus);
        }
    }
    /**
     * Sends the given message to the subscribed clients.
     *
     * @param inIsAdmin a <code>boolean</code> value
     * @param inBroker a <code>Broker</code> value
     * @param inMessage a <code>Message</code> value
     * @param inOriginator an <code>Originator</code> value
     * @param inHierarchy a <code>Hierarchy</code> value
     */
    private void sendToClientTrades(boolean inIsAdmin,
                                    Broker inBroker,
                                    Message inMessage,
                                    Originator inOriginator,
                                    Hierarchy inHierarchy)
    {
        if(getUserManager() == null) {
            return;
        }
        UserID actor = null;
        if(!inIsAdmin) {
            actor = outgoingMessageService.getMessageOwner(inMessage,
                                                           inBroker.getSessionID(),
                                                           inBroker.getBrokerID());
        }
        // Apply message modifiers.
        boolean orderIntercepted = false;
        if((inOriginator == Originator.Broker) && (inBroker.getResponseModifiers() != null)) {
            try {
                SessionInfo sessionInfo = new SessionInfoImpl(getSystemInfo());
                sessionInfo.setValue(SessionInfo.ACTOR_ID,
                                     actor);
                RequestInfo requestInfo = new RequestInfoImpl(sessionInfo);
                requestInfo.setValue(RequestInfo.BROKER,
                                     inBroker);
                requestInfo.setValue(RequestInfo.BROKER_ID,
                                     inBroker.getBrokerID());
                requestInfo.setValue(RequestInfo.ORIGINATOR,
                                     inOriginator);
                requestInfo.setValue(RequestInfo.FIX_MESSAGE_FACTORY,
                                     inBroker.getFIXMessageFactory());
                requestInfo.setValue(RequestInfo.CURRENT_MESSAGE,
                                     inMessage);
                inBroker.getResponseModifiers().modifyMessage(requestInfo);
                inMessage = requestInfo.getValueIfInstanceOf(RequestInfo.CURRENT_MESSAGE,
                                                             Message.class);
            } catch (OrderIntercepted e) {
                SLF4JLoggerProxy.info(this,
                                      "{} intercepted",
                                      inMessage);
                orderIntercepted = true;
            } catch (I18NException ex) {
                Messages.QF_MODIFICATION_FAILED.warn(getCategory(inMessage),
                                                     ex,
                                                     inMessage,
                                                     inBroker);
            }
        }
        TradeMessage reply;
        try {
            reply = FIXConverter.fromQMessage(inMessage,
                                              inOriginator,
                                              inBroker.getBrokerID(),
                                              inHierarchy,
                                              actor,
                                              actor);
        } catch (MessageCreationException ex) {
            Messages.QF_REPORT_FAILED.error(getCategory(inMessage),
                                            ex,
                                            inMessage,
                                            inBroker);
            return;
        }
        isotopeService.inject(inMessage);
        try {
            // Persist and send reply.
            getPersister().persistReply(reply);
        } finally {
            Messages.QF_SENDING_REPLY.info(getCategory(inMessage),reply);
            isotopeService.inject(inMessage);
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
     * Determines if the given exception indicates a shutdown.
     *
     * @param inThrowable a <code>Throwable</code> value
     * @return a <code>boolean</code> value
     */
    private boolean isShutdown(Throwable inThrowable)
    {
        if(inThrowable instanceof HazelcastInstanceNotActiveException) {
            return true;
        }
        if(inThrowable instanceof InterruptedException) {
            return true;
        }
        if(ExceptionUtils.getRootCause(inThrowable) instanceof HazelcastInstanceNotActiveException) {
            return true;
        }
        return ExceptionUtils.getFullStackTrace(inThrowable).contains(hazelcastInstanceIsNotActive);
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
     * Processes the given message package.
     *
     * @param messagePackage a <code>MessagePackage</code> value
     */
    private void processMessage(MessagePackage messagePackage)
    {
        SessionID session = messagePackage.sessionId;
        Message msg = messagePackage.message;
        Broker broker = null;
        try {
            broker = brokerService.getBroker(session);
            isotopeService.inject(msg);
            switch(messagePackage.getMessageType()) {
                case FROM_ADMIN: {
                    Object category = getCategory(msg);
                    boolean shouldDisplayMessage = !category.equals(HEARTBEAT_CATEGORY) || SLF4JLoggerProxy.isDebugEnabled(HEARTBEAT_CATEGORY);
                    if(shouldDisplayMessage) {
                        Messages.QF_FROM_ADMIN.info(category,
                                                    msg,
                                                    broker);
                        broker.logMessage(msg);
                        // Send message to client.
                        sendToClientTrades(true,
                                           broker,
                                           msg,
                                           Originator.Broker,
                                           messagePackage.getHierarchy());
                    }
                    break;
                }
                case FROM_APP: {
                    try {
                        // Report trading session status in a human-readable format.
                        if (FIXMessageUtil.isTradingSessionStatus(msg)) {
                            Messages.QF_TRADE_SESSION_STATUS.info(getCategory(msg),
                                                                  broker.getFIXDataDictionary().getHumanFieldValue(TradSesStatus.FIELD,
                                                                                                                   msg.getString(TradSesStatus.FIELD)));
                        }
                        // Send message to client.
                        sendToClientTrades(false,
                                           broker,
                                           msg,
                                           Originator.Broker,
                                           messagePackage.hierarchy);
                        if(!allowDeliverToCompID && msg.getHeader().isSetField(DeliverToCompID.FIELD)) {
                            // OpenFIX certification: we reject all DeliverToCompID since we don't redeliver.
                            try {
                                Message reject = broker.getFIXMessageFactory().createSessionReject(msg,
                                                                                                   SessionRejectReason.COMPID_PROBLEM);
                                reject.setString(Text.FIELD,
                                                 Messages.QF_COMP_ID_REJECT.getText(msg.getHeader().getString(DeliverToCompID.FIELD)));
                                getSender().sendToTarget(reject,
                                                         session);
                            } catch (SessionNotFound ex) {
                                Messages.QF_COMP_ID_REJECT_FAILED.error(getCategory(msg),
                                                                        ex,
                                                                        broker.toString());
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
            Messages.QF_STANDARD_SHUTDOWN.warn(QuickFIXApplication.this,
                                               msg);
        } catch (Exception e) {
            if(isShutdown(e)) {
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    Messages.QF_SHUTDOWN_ERROR.warn(QuickFIXApplication.this,
                                                    e,
                                                    msg);
                } else {
                    Messages.QF_SHUTDOWN_ERROR.warn(QuickFIXApplication.this,
                                                    msg);
                }
            } else {
                SLF4JLoggerProxy.error(QuickFIXApplication.this,
                                       e);
            }
        }
    }
    /**
     * Processes messages for a given session.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
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
                        SLF4JLoggerProxy.debug(QuickFIXApplication.this,
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
                        Messages.QF_NO_ROOT_ORDER_ID.warn(QuickFIXApplication.this,
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
                                    SLF4JLoggerProxy.info(QuickFIXApplication.this,
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
                    SLF4JLoggerProxy.info(QuickFIXApplication.this,
                                          "Resuming work on {} after {}ms delay",
                                          inMessagePackage.message,
                                          System.currentTimeMillis() - delayStarted);
                }
                orderQueue.add(inMessagePackage);
            } catch (Exception e) {
                if(isShutdown(e)) {
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
     * @version $Id$
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
     * @version $Id$
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
                SLF4JLoggerProxy.debug(QuickFIXApplication.this,
                                       "Testing {} for timeout at {}",
                                       queue,
                                       currentTime);
                // TODO how sure are we that a new order couldn't be added? maybe synchronize on queue instead?
                synchronized(orderQueues) {
                    long timestamp = queue.timestamp;
                    if(!queue.workingOnOrder.get() && currentTime > timestamp+executionPoolTtl) {
                        if(queue.size() != 0) {
                            SLF4JLoggerProxy.debug(QuickFIXApplication.this,
                                                   "Not timing out {} because size {} is not zero",
                                                   queue,
                                                   queue.size());
                        } else {
                            SLF4JLoggerProxy.debug(QuickFIXApplication.this,
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
     * @version $Id$
     * @since 2.1.4
     */
    @ClassVersion("$Id$")
    private enum MessageType
    {
        FROM_ADMIN,
        FROM_APP
    }
    /**
     * Encapsulates a message to be processed.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.4
     */
    @ClassVersion("$Id$")
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
     * @version $Id$
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
     * determines whether we allow the redeliverToCompID flag or not
     */
    private boolean allowDeliverToCompID = false;
    /**
     * provides access to outgoing message services
     */
    @Autowired
    private OutgoingMessageService outgoingMessageService;
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * provides access to report services 
     */
    @Autowired
    private ReportService reportService;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * provides access to session services
     */
    @Autowired
    private SessionService sessionService;
    /**
     * identifies this cluster instance
     */
    private ClusterData clusterData;
    /**
     * messages that should not be forwarded to clients (empty to forward all messages)
     */
    private final Set<String> dontForwardMessages = Sets.newHashSet(MsgType.LOGON,MsgType.HEARTBEAT,MsgType.TEST_REQUEST,MsgType.RESEND_REQUEST,MsgType.REJECT,MsgType.SEQUENCE_RESET,MsgType.LOGOUT);
    /**
     * messages that should be forwarded to client (empty for all messages)
     */
    private final Set<String> forwardMessages = new HashSet<>();
    /**
     * determines the root order id for a given message
     */
    private RootOrderIdFactory rootOrderIdFactory;
    /**
     * provides access to the isotope services
     */
    private IsotopeService isotopeService = new IsotopeService();
    /**
     * system info value
     */
    private SystemInfo mSystemInfo;
    /**
     * supported messages value
     */
    private MessageFilter mSupportedMessages;
    /**
     * persists incoming messages
     */
    private ReplyPersister mPersister;
    /**
     * object which sends messages as necessary
     */
    private QuickFIXSender mSender;
    /**
     * provides user services
     */
    private UserManager mUserManager;
    /**
     * sends messages to clients
     */
    private JmsOperations mToClientStatus;
    /**
     * sends trades to clients
     */
    private JmsOperations mToTradeRecorder;
    /**
     * provides data store access to persistent report objects
     */
    private PersistentReportDao reportDao;
    /**
     * provides authorization for particular brokers
     */
    private String productKey;
    /**
     * holds processing queues for root order id
     */
    private final Map<MessageKey,OrderMessageProcessingQueue> orderQueues = new HashMap<>();
    /**
     * indicates if execution pools are allowed to be allocated or not
     */
    private final AtomicBoolean suspendExecutionPools = new AtomicBoolean(false);
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
     * directory name into which FIX injector files will be dropped, may be <code>null</code>
     */
    private String fixInjectorDirectory;
    /**
     * indicates if the object is actively running as opposed to starting, shutting down, or stopped
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    /**
     * restores FIX sessions, if specified
     */
    @Autowired(required=false)
    private FixSessionRestoreExecutor<PrioritizedMessageSessionRestorePayload,SessionRestorePayload> fixSessionRestoreExecutor;
    /**
     * constructs a FIX settings provider object
     */
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * holds those interested in broker status updates
     */
    private final Queue<BrokerStatusListener> brokerStatusListeners = new ConcurrentLinkedQueue<>();
    /**
     * holds those interested in session status updates
     */
    private final Queue<SessionStatusListener> sessionStatusListeners = new ConcurrentLinkedQueue<>();
    /**
     * session queues by session ID
     */
    private final Map<SessionID,SessionMessageProcessingQueue> sessionQueues = new HashMap<>();
    /**
     * sessions that have been created
     */
    private final Set<SessionID> activeSessions = new CopyOnWriteArraySet<>();
    /**
     * executes jobs at scheduled times
     */
    private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(2);
    /**
     * logger category used for heartbeats
     */
    private static final String HEARTBEAT_CATEGORY = QuickFIXApplication.class.getName()+".HEARTBEATS"; //$NON-NLS-1$
    /**
     * indicates that hazelcast is not active
     */
    private static final String hazelcastInstanceIsNotActive = "Hazelcast instance is not active"; //$NON-NLS-1$
    /**
     * validation name of this component
     */
    static final String COMPONENT_NAME = "DARE"; //$NON-NLS-1$
    /**
     * indicates the "normal" message types we handle
     */
    private static final Set<String> standardMessageTypes = Sets.newHashSet(MsgType.ORDER_CANCEL_REJECT,MsgType.EXECUTION_REPORT);
    /**
     * used to assign unique sequence numbers to artificially created messages
     */
    private final AtomicInteger seqCounter = new AtomicInteger(Integer.MIN_VALUE);
}
