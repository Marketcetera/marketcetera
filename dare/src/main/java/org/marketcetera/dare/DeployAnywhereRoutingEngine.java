package org.marketcetera.dare;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.JMException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.marketcetera.brokers.LogonAction;
import org.marketcetera.brokers.LogoutAction;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.cluster.ClusterActivateWorkUnit;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterWorkUnit;
import org.marketcetera.cluster.ClusterWorkUnitType;
import org.marketcetera.cluster.ClusterWorkUnitUid;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.Pair;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.QueueProcessor;
import org.marketcetera.core.file.DirectoryWatcherImpl;
import org.marketcetera.core.file.DirectoryWatcherSubscriber;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributes;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.OrderIntercepted;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.fix.SessionRestorePayload;
import org.marketcetera.fix.SessionRestorePayloadHandler;
import org.marketcetera.fix.SessionService;
import org.marketcetera.fix.acceptor.DynamicAcceptorSessionProvider;
import org.marketcetera.fix.event.FixSessionDisabledEvent;
import org.marketcetera.fix.event.FixSessionEnabledEvent;
import org.marketcetera.fix.event.FixSessionStartedEvent;
import org.marketcetera.fix.event.FixSessionStatusEvent;
import org.marketcetera.fix.event.FixSessionStoppedEvent;
import org.marketcetera.fix.event.SimpleFixSessionAvailableEvent;
import org.marketcetera.fix.event.SimpleFixSessionUnavailableEvent;
import org.marketcetera.fix.provisioning.FixSessionRestoreExecutor;
import org.marketcetera.ors.Messages;
import org.marketcetera.ors.PrioritizedMessageSessionRestorePayload;
import org.marketcetera.ors.filters.MessageFilter;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.RootOrderIdFactory;
import org.marketcetera.trade.event.IncomingFixMessageEvent;
import org.marketcetera.trade.event.OwnedMessage;
import org.marketcetera.trade.event.SimpleIncomingFixAdminMessageEvent;
import org.marketcetera.trade.event.SimpleIncomingFixAppMessageEvent;
import org.marketcetera.trade.event.SimpleIncomingOrderInterceptedEvent;
import org.marketcetera.trade.event.SimpleOutgoingOrderStatusEvent;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.AnalyzedMessage;
import org.marketcetera.util.ws.stateful.PortDescriptor;
import org.marketcetera.util.ws.stateful.UsesPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import com.codahale.metrics.Counter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.OperationTimeoutException;

import quickfix.ConfigError;
import quickfix.DefaultSessionFactory;
import quickfix.DynamicSessionThreadedSocketAcceptor;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketAcceptor;
import quickfix.mina.acceptor.AcceptorSessionProvider;

/* $License$ */

/**
 * Provides routing engine services for the Marketcetera Automated Trading Platform.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@EnableAutoConfiguration
@ClusterWorkUnit(id="MATP.DARE",type=ClusterWorkUnitType.SINGLETON_RUNTIME)
public class DeployAnywhereRoutingEngine
        implements quickfix.ApplicationExtended,DirectoryWatcherSubscriber,UsesPort
{
    /* (non-Javadoc)
     * @see quickfix.Application#onCreate(quickfix.SessionID)
     */
    @Override
    public void onCreate(quickfix.SessionID inSessionId)
    {
        activeSessions.add(inSessionId);
        String sessionName = brokerService.getSessionName(inSessionId);
        SLF4JLoggerProxy.debug(this,
                               "{} created", //$NON-NLS-1$
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
                        } catch (quickfix.UnsupportedMessageType e) {
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
                             generateSessionStatus(fixSession.getActiveFixSession().getFixSession(),
                                                   false,
                                                   false));
            }
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogon(quickfix.SessionID)
     */
    @Override
    public void onLogon(quickfix.SessionID inSessionId)
    {
        if(!isRunning.get()) {
            return;
        }
        String sessionName = brokerService.getSessionName(inSessionId);
        SLF4JLoggerProxy.info(FIXMessageUtil.FIX_RESTORE_LOGGER_NAME,
                              "{} logged on",
                              sessionName);
        ServerFixSession fixSession = brokerService.getServerFixSession(inSessionId);
        if(fixSession == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Ignoring logon from missing session {}",
                                  sessionName);
            return;
        }
        updateStatus(fixSession.getActiveFixSession().getFixSession(),
                     generateSessionStatus(fixSession.getActiveFixSession().getFixSession(),
                                           true,
                                           false));
        SessionCustomization sessionCustomization = brokerService.getSessionCustomization(fixSession.getActiveFixSession().getFixSession());
        if(sessionCustomization != null && sessionCustomization.getLogonActions() != null) {
            for(LogonAction action : sessionCustomization.getLogonActions()) {
                try {
                    action.onLogon(fixSession,
                                   getSender());
                } catch (Exception e) {
                    PlatformServices.handleException(this,
                                                     "Error on logon action",
                                                     e);
                }
            }
        }
//        SessionStatus status = new SessionStatus(inSessionId,
//                                                 true,
//                                                 true);
//        for(SessionStatusListener listener : sessionStatusListeners) {
//            try {
//                listener.receiveSessionStatus(status);
//            } catch (Exception e) {
//                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
//                    SLF4JLoggerProxy.warn(this,
//                                          e,
//                                          "{} on session status: {}",
//                                          sessionName,
//                                          ExceptionUtils.getRootCauseMessage(e));
//                } else {
//                    SLF4JLoggerProxy.warn(this,
//                                          "{} on session status: {}",
//                                          sessionName,
//                                          ExceptionUtils.getRootCauseMessage(e));
//                }
//            }
//        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogout(quickfix.SessionID)
     */
    @Override
    public void onLogout(quickfix.SessionID inSessionId)
    {
        if(!isRunning.get()) {
            return;
        }
        String sessionName = brokerService.getSessionName(inSessionId);
        SLF4JLoggerProxy.info(FIXMessageUtil.FIX_RESTORE_LOGGER_NAME,
                              "{} logged out",
                              sessionName);
        try {
            ServerFixSession fixSession = brokerService.getServerFixSession(inSessionId);
            if(fixSession == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Ignoring logout from missing session {}",
                                      inSessionId);
                return;
            }
            updateStatus(fixSession.getActiveFixSession().getFixSession(),
                         generateSessionStatus(fixSession.getActiveFixSession().getFixSession(),
                                               false,
                                               false));
            SessionCustomization sessionCustomization = brokerService.getSessionCustomization(fixSession.getActiveFixSession().getFixSession());
            if(sessionCustomization != null && sessionCustomization.getLogoutActions() != null) {
                for(LogoutAction action : sessionCustomization.getLogoutActions()) {
                    try {
                        action.onLogout(fixSession,
                                        getSender());
                    } catch (Exception e) {
                        if(PlatformServices.isShutdown(e)) {
                            throw e;
                        }
                        PlatformServices.handleException(this,
                                                         "Error on logout action",
                                                         e);
                    }
                }
            }
//            ActiveFixSession broker = brokerService.generateBroker(fixSession);
//            Collection<LogoutAction> logoutActions = broker.getSpringBroker().getLogoutActions();
//            if(logoutActions != null) {
//                for(LogoutAction action : logoutActions) {
//                    try {
//                        action.onLogout(broker,
//                                        getSender());
//                    } catch (Exception e) {
//                        if(isShutdown(e)) {
//                            throw e;
//                        }
//                        if(SLF4JLoggerProxy.isDebugEnabled(this)) {
//                            SLF4JLoggerProxy.warn(this,
//                                                  e,
//                                                  "{} on log out: {}",
//                                                  sessionName,
//                                                  ExceptionUtils.getRootCauseMessage(e));
//                        } else {
//                            SLF4JLoggerProxy.warn(this,
//                                                  "{} on log out: {}",
//                                                  sessionName,
//                                                  ExceptionUtils.getRootCauseMessage(e));
//                        }
//                    }
//                }
//            }
            if(fixSessionRestoreExecutor != null) {
                fixSessionRestoreExecutor.sessionLogout(inSessionId);
            }
//            SessionStatus status = new SessionStatus(inSessionId,
//                                                     true,
//                                                     false);
//            for(SessionStatusListener listener : sessionStatusListeners) {
//                try {
//                    listener.receiveSessionStatus(status);
//                } catch (Exception e) {
//                    if(isShutdown(e)) {
//                        throw e;
//                    }
//                    if(SLF4JLoggerProxy.isDebugEnabled(this)) {
//                        SLF4JLoggerProxy.warn(this,
//                                              e,
//                                              "{} on session status: {}",
//                                              sessionName,
//                                              ExceptionUtils.getRootCauseMessage(e));
//                    } else {
//                        SLF4JLoggerProxy.warn(this,
//                                              "{} on session status: {}",
//                                              sessionName,
//                                              ExceptionUtils.getRootCauseMessage(e));
//                    }
//                }
//            }
        } catch (Exception e) {
            if(PlatformServices.isShutdown(e)) {
                // this exception can be safely ignored
                return;
            }
            PlatformServices.handleException(this,
                                             "Error on log out",
                                             e);
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toAdmin(quickfix.Message inMessage,
                        quickfix.SessionID inSessionId)
    {
        if(!isRunning.get()) {
            return;
        }
        ServerFixSession serverFixSession = brokerService.getServerFixSession(inSessionId);
        if(serverFixSession == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Ignoring toAdmin for {} from missing session {}",
                                  inMessage,
                                  brokerService.getSessionName(inSessionId));
            return;
        }
        // this is necessary due to a race condition on logon for QFJ where for FIXT only where the DefaultVerID isn't set
        //  until after logon, but is necessary for logon
        if(inSessionId.isFIXT() && FIXMessageUtil.isLogon(inMessage) && !inMessage.getHeader().isSetField(quickfix.field.ApplVerID.FIELD)) {
            String defaultApplVerId = serverFixSession.getActiveFixSession().getFixSession().getSessionSettings().get("DefaultApplVerID");
            if(defaultApplVerId == null) {
                defaultApplVerId = quickfix.field.ApplVerID.FIX50SP2;
            }
            Session.lookupSession(inSessionId).setTargetDefaultApplicationVersionID(new quickfix.field.ApplVerID(defaultApplVerId));
        }
        Messages.QF_TO_ADMIN.info(getCategory(inMessage),
                                  inMessage,
                                  serverFixSession);
        logMessage(inMessage,
                   serverFixSession);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromAdmin(quickfix.Message inMessage,
                          quickfix.SessionID inSessionId)
            throws quickfix.FieldNotFound,quickfix.IncorrectDataFormat,quickfix.IncorrectTagValue,quickfix.RejectLogon
    {
        if(!isRunning.get()) {
            return;
        }
        if(FIXMessageUtil.isLogon(inMessage)) {
            if(!brokerService.isSessionTime(inSessionId)) {
                ServerFixSession session = brokerService.getServerFixSession(inSessionId);
                if(session == null) {
                    throw new quickfix.RejectLogon(inSessionId + " is not a known session");
                }
                updateStatus(session.getActiveFixSession().getFixSession(),
                             generateSessionStatus(session.getActiveFixSession().getFixSession(),
                                                   false,
                                                   false));
                throw new quickfix.RejectLogon(inSessionId + " logon is not allowed outside of session time");
            }
        }
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
    public void toApp(quickfix.Message inMessage,
                      quickfix.SessionID inSessionId)
            throws quickfix.DoNotSend
    {
        if(!isRunning.get()) {
            return;
        }
        ServerFixSession serverFixSession = brokerService.getServerFixSession(inSessionId);
        if(serverFixSession == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Ignoring toApp for {} from missing session {}",
                                  inMessage,
                                  brokerService.getSessionName(inSessionId));
            return;
        }
        Messages.QF_TO_APP.info(getCategory(inMessage),
                                inMessage,
                                serverFixSession);
        logMessage(inMessage,
                   serverFixSession);
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
    public void fromApp(quickfix.Message inMessage,
                        quickfix.SessionID inSessionId)
            throws quickfix.FieldNotFound,quickfix.IncorrectDataFormat,quickfix.IncorrectTagValue,quickfix.UnsupportedMessageType
    {
        if(!isRunning.get()) {
            return;
        }
        doFromApp(inMessage,
                  inSessionId,
                  0);
    }
    /* (non-Javadoc)
     * @see quickfix.ApplicationExtended#canLogon(quickfix.SessionID)
     */
    @Override
    public boolean canLogon(quickfix.SessionID inSessionId)
    {
        if(brokerService.isSessionTime(inSessionId)) {
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
    public void onBeforeSessionReset(quickfix.SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} resetting session",
                               inSessionId);
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
            for(String line : FileUtils.readLines(inFile,
                                                  Charset.defaultCharset())) {
                try {
                    quickfix.Message message = getMessageFromLine(line);
                    if(message == null) {
                        SLF4JLoggerProxy.info(this,
                                              "Not injecting {}",
                                              line);
                        continue;
                    }
                    quickfix.SessionID sessionId = getInjectionSessionIdFrom(message);
                    String msgType = message.getHeader().getString(quickfix.field.MsgType.FIELD);
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
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getClusterWorkUnitUid();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.UsesPort#getPortDescriptors()
     */
    @Override
    public Collection<PortDescriptor> getPortDescriptors()
    {
        return ports;
    }
    /**
     * Receive outgoing owned messages.
     *
     * @param inOwnedMessage an <code>OwnedMessage</code> value
     */
    @Subscribe
    public void receive(OwnedMessage inOwnedMessage)
    {
        boolean failed = false;
        String message = null;
        try {
            // TODO not completely sure about the behavior if the session is offline
            failed = !Session.sendToTarget(inOwnedMessage.getMessage(),
                                           inOwnedMessage.getSessionId());
        } catch (SessionNotFound e) {
            message = ExceptionUtils.getRootCauseMessage(e);
            failed = true;
        }
        if(inOwnedMessage.getMessage().isSetField(quickfix.field.ClOrdID.FIELD)) {
            try {
                eventBusService.post(new SimpleOutgoingOrderStatusEvent(message,
                                                                        failed,
                                                                        null,
                                                                        new OrderID(inOwnedMessage.getMessage().getString(quickfix.field.ClOrdID.FIELD)),
                                                                        inOwnedMessage.getMessage()));
            } catch (FieldNotFound ignored) {} // this exception can't occur because we explicitly check for the existence of the field
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "Not posting outgoing order status for {} because it doesn't have an order id",
                                   inOwnedMessage.getMessage());
        }
    }
    /**
     * Indicates that the given FIX session has been disabled.
     *
     * @param inEvent a <code>FixSessionDisabledEvent</code> value
     */
    @Subscribe
    public void sessionDisabled(FixSessionDisabledEvent inEvent)
    {
        FixSession fixSession = fixSessionProvider.findFixSessionBySessionId(inEvent.getSessionId());
        quickfix.SessionID sessionId = inEvent.getSessionId();
        String sessionName = brokerService.getSessionName(sessionId);
        if(fixSession == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring disabled session {} because it no longer exists",
                                   sessionName);
            return;
        }
        if(!isActive()) {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring disabled session {} because DARE is not active",
                                   sessionName);
            return;
        }
        SLF4JLoggerProxy.debug(this,
                               "Beginning disable for {}",
                               sessionName);
        quickfix.Session activeSession = quickfix.Session.lookupSession(sessionId);
        if(activeSession == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No existing session on this instance for {}, nothing to do",
                                   fixSession);
        } else {
            synchronized(sessionLock) {
                SLF4JLoggerProxy.debug(this,
                                       "Disabling {}",
                                       sessionName);
                if(fixSession.isAcceptor()) {
                    socketAcceptor.removeDynamicSession(sessionId);
                } else {
                    synchronized(initiators) {
                        quickfix.ThreadedSocketInitiator initiator = initiators.remove(sessionId);
                        if(initiator != null) {
                            initiator.stop(true);
                            initiator.removeDynamicSession(sessionId);
                        }
                    }
                }
            }
        }
        updateStatus(fixSession,
                     generateSessionStatus(fixSession,
                                           false,
                                           false));
    }
    /**
     * Indicates that the given FIX session has been enabled.
     *
     * @param fixSession a <code>FixSessionEnabledEvent</code> value
     */
    @Subscribe
    public void sessionEnabled(FixSessionEnabledEvent inEvent)
    {
        FixSession fixSession = fixSessionProvider.findFixSessionBySessionId(inEvent.getSessionId());
        quickfix.SessionID newSessionId = inEvent.getSessionId();
        String sessionName = brokerService.getSessionName(newSessionId);
        if(fixSession == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring enabled session {} because it no longer exists",
                                   sessionName);
            return;
        }
        if(!isActive()) {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring enabled session {} because DARE is not active",
                                   sessionName);
            return;
        }
        synchronized(sessionLock) {
            if(brokerService.isAffinityMatch(fixSession,
                                             clusterData)) {
                SLF4JLoggerProxy.debug(this,
                                       "Enabling {}",
                                       sessionName);
                if(fixSession.isAcceptor()) {
                    stoppedAcceptorSessions.remove(newSessionId);
                    // nothing to do - session will be added automatically when the dynamic provider receives the open request
                    sendInterimStatus(fixSession);
                    if(isPrimary) {
                        try {
                            initializeAcceptor();
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(this,
                                                  e,
                                                  "Could not initialize acceptor");
                            if(e instanceof RuntimeException) {
                                throw (RuntimeException)e;
                            }
                            throw new RuntimeException(e);
                        }
                        // session doesn't exist yet, check to see if there's a session that matches in the DB
                        if(fixSession.isEnabled()) {
                            quickfix.Session session = Session.lookupSession(newSessionId);
                            if(session == null) {
                                // session exists in the DB, generate FIX settings for it
                                quickfix.SessionSettings newSettings = brokerService.generateSessionSettings(Lists.newArrayList(fixSession));
                                quickfix.SessionSettings existingSettings = socketAcceptor.getSettings();
                                FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
                                try {
                                    Properties newSessionProperties = newSettings.getSessionProperties(newSessionId);
                                    for(Map.Entry<Object,Object> entry : newSessionProperties.entrySet()) {
                                        String key = String.valueOf(entry.getKey());
                                        String value = String.valueOf(entry.getValue());
                                        existingSettings.setString(newSessionId,
                                                                   key,
                                                                   value);
                                    }
                                    SessionFactory factory = new DefaultSessionFactory(this,
                                                                                       fixSettingsProvider.getMessageStoreFactory(existingSettings),
                                                                                       fixSettingsProvider.getLogFactory(existingSettings),
                                                                                       fixSettingsProvider.getMessageFactory());
                                    // this will force the session to exist, which puts it back in the mix
                                    session = factory.create(newSessionId,
                                                             newSettings);
                                    socketAcceptor.addDynamicSession(session);
                                } catch (ConfigError e) {
                                    SLF4JLoggerProxy.warn(this,
                                                          e);
                                }
                            }
                        }
                    } else {
                        SLF4JLoggerProxy.debug(this,
                                               "{} enabling {}, but this node is not the primary node",
                                               this,
                                               sessionName);
                    }
                } else {
                    try {
                        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
                        quickfix.SessionSettings sessionSettings = brokerService.generateSessionSettings(Lists.newArrayList(fixSession));
                        quickfix.SessionFactory sessionFactory = new quickfix.DefaultSessionFactory(this,
                                                                                                    fixSettingsProvider.getMessageStoreFactory(sessionSettings),
                                                                                                    fixSettingsProvider.getLogFactory(sessionSettings),
                                                                                                    fixSettingsProvider.getMessageFactory());
                        quickfix.Session newSession = sessionFactory.create(newSessionId,
                                                                            sessionSettings);
                        quickfix.ThreadedSocketInitiator newInitiator = new quickfix.ThreadedSocketInitiator(this,
                                                                                                             fixSettingsProvider.getMessageStoreFactory(sessionSettings),
                                                                                                             sessionSettings,
                                                                                                             fixSettingsProvider.getLogFactory(sessionSettings),
                                                                                                             fixSettingsProvider.getMessageFactory());
                        synchronized(initiators) {
                            //                                jmxExporter.register(newInitiator);
                            newInitiator.start();
                            initiators.put(newSessionId,
                                           newInitiator);
                        }
                        SLF4JLoggerProxy.debug(this,
                                               "Adding {} for {}",
                                               newSession,
                                               fixSession);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to create new session for {}",
                                              fixSession);
                        if(e instanceof RuntimeException) {
                            throw (RuntimeException)e;
                        }
                        throw new RuntimeException(e);
                    }
                }
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Ignoring enabled session {} because of affinity mismatch",
                                       sessionName);
                // send status update
                updateStatus(fixSession,
                             generateSessionStatus(fixSession,
                                                   false,
                                                   false));
            }
        }
    }
    /**
     * Indicates that a FIX session has stopped.
     *
     * @param inEvent a <code>FixSessionStoppedEvent</code> value
     */
    @Subscribe
    public void sessionStopped(FixSessionStoppedEvent inEvent)
    {
        quickfix.SessionID sessionId = inEvent.getSessionId();
        String sessionName = brokerService.getSessionName(sessionId);
        ServerFixSession serverFixSession = brokerService.getServerFixSession(sessionId);
        if(serverFixSession == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring stopped session {} because it no longer exists",
                                   inEvent.getSessionId());
            return;
        }
        if(!isActive()) {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring stopped session {} because DARE is not active",
                                   inEvent.getSessionId());
            return;
        }
        FixSession fixSession = serverFixSession.getActiveFixSession().getFixSession();
        quickfix.Session activeSession = quickfix.Session.lookupSession(sessionId);
        if(activeSession == null) {
            SLF4JLoggerProxy.debug(this,
                                   "{} not stopping {} because it is not currently active",
                                   this,
                                   fixSession);
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "{} stopping {}",
                                   this,
                                   sessionName);
            synchronized(sessionLock) {
                if(fixSession.isAcceptor()) {
                    stoppedAcceptorSessions.add(sessionId);
                }
                try {
                    activeSession.disconnect("Stop invoked at " + new DateTime(),
                                             false);
                } catch (IOException e) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          "{} unable to stop {}",
                                          this,
                                          sessionName);
                    return;
                }
                if(!fixSession.isAcceptor()) {
                    quickfix.ThreadedSocketInitiator initiator = initiators.get(sessionId);
                    if(initiator == null) {
                        SLF4JLoggerProxy.debug(this,
                                               "{} found no intiator to stop for {}, nothing to do",
                                               this,
                                               fixSession);
                    } else {
                        initiator.stop();
                    }
                }
                updateStatus(fixSession,
                             generateSessionStatus(fixSession,
                                                   false,
                                                   true));
                SLF4JLoggerProxy.debug(this,
                                       "{} {} stopped",
                                       this,
                                       sessionName);
            }
        }
    }
    /**
     * Receive the given FIX session started event.
     *
     * @param inEvent a <code>FixSessionStartedEvent</code> value
     */
    @Subscribe
    public void sessionStarted(FixSessionStartedEvent inEvent)
    {
        quickfix.SessionID sessionId = inEvent.getSessionId();
        String sessionName = brokerService.getSessionName(sessionId);
        ServerFixSession serverFixSession = brokerService.getServerFixSession(inEvent.getSessionId());
        if(serverFixSession == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring started session {} because it no longer exists",
                                   sessionName);
            return;
        }
        if(!isActive()) {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring started session {} because DARE is not active",
                                   inEvent.getSessionId());
            return;
        }
        FixSession fixSession = serverFixSession.getActiveFixSession().getFixSession();
        synchronized(sessionLock) {
            if(fixSession.isAcceptor()) {
                SLF4JLoggerProxy.debug(this,
                                       "{} starting {}",
                                       this,
                                       sessionName);
                stoppedAcceptorSessions.remove(sessionId);
                if(brokerService.isAffinityMatch(fixSession,
                                                 clusterData)) {
                    sendInterimStatus(fixSession);
                }
            } else {
                quickfix.ThreadedSocketInitiator initiator = initiators.get(sessionId);
                if(initiator == null) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} found no intiator to start for {}, nothing to do",
                                           this,
                                           fixSession);
                } else {
                    try {
                        initiator.start();
                        SLF4JLoggerProxy.debug(this,
                                               "{} {} initiator started",
                                               this,
                                               fixSession);
                    } catch (quickfix.RuntimeError | quickfix.ConfigError e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to start {}",
                                              fixSession);
                    }
                }
            }
        }
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
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryName()));
        createdSessions.clear();
        loggedOnSessions.clear();
        clusterData = clusterService.getInstanceData();
        int instanceId = clusterData.getInstanceNumber();
        clusterWorkUnitUid = getClass().getSimpleName() + "-" + instanceId;
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
                    for(quickfix.SessionID sessionId : activeSessions) {
                        quickfix.Session session = quickfix.Session.lookupSession(sessionId);
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
        backupStatusTask = new Runnable() {
            @Override
            public void run()
            {
                try {
                    reportBackupStatus();
                    SLF4JLoggerProxy.debug(DeployAnywhereRoutingEngine.this,
                                           "{} successfully completed backup status notification",
                                           this);
                } catch (Exception e) {
                    SLF4JLoggerProxy.debug(DeployAnywhereRoutingEngine.this,
                                           e,
                                           "{} failed to report backup status, will try again",
                                           this);
                    scheduledService.schedule(backupStatusTask,
                                              1000,
                                              TimeUnit.MILLISECONDS);
                }
            }
        };
        scheduledService.schedule(backupStatusTask,
                                  1000,
                                  TimeUnit.MILLISECONDS);
        eventBusService.register(this);
        isRunning.set(true);
    }
    /**
     * Stops worker threads of the receiver.
     */
    @PreDestroy
    public void stop()
    {
        try {
            if(scheduledService != null) {
                try {
                    scheduledService.shutdownNow();
                } catch (Exception ignored) {}
                scheduledService = null;
            }
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
            synchronized(initiators) {
                for(quickfix.ThreadedSocketInitiator initiator : initiators.values()) {
                    for(quickfix.Session session : initiator.getManagedSessions()) {
                        try {
                            session.disconnect("System shutdown",
                                               false);
                        } catch (Exception ignored) {}
                    }
                    try {
                        initiator.stop(true);
                    } catch (Exception ignored) {}
                }
                initiators.clear();
            }
            if(socketAcceptor != null) {
                for(Session session : socketAcceptor.getManagedSessions()) {
                    try {
                        session.disconnect("System shutdown",
                                           false);
                    } catch (Exception ignored) {}
                }
                try {
                    socketAcceptor.stop(true);
                } catch (Exception ignored) {}
                socketAcceptor = null;
            }
//            for(SessionProcessingQueue sessionQueue : queuesBySessionId.values()) {
//                try {
//                    sessionQueue.stop();
//                } catch (Exception ignored) {}
//            }
        } finally {
//            if(notificationExecutor != null) {
//                notificationExecutor.notify(Notification.low("DARE Stopped",
//                                                             "DARE Stopped at " + new DateTime(),
//                                                             DeployAnywhereRoutingEngine.class.getSimpleName()));
//            }
//            Messages.APP_STOP_SUCCESS.info(this);
        }
    }
    /**
     * Activates the object and makes it ready for use in a clustered environment.
     *
     * @throws Exception if the method could not be activated
     */
    @ClusterActivateWorkUnit
    public void activate()
            throws Exception
    {
        try {
            synchronized(sessionLock) {
                SLF4JLoggerProxy.info(this,
                                      "Activating {}",
                                      getClusterWorkUnitUid());
                isPrimary = true;
                int totalInstances = clusterData.getTotalInstances();
                int instanceId = clusterData.getInstanceNumber();
                List<FixSession> sessions = fixSessionProvider.findFixSessions(false,
                                                                               instanceId,
                                                                               totalInstances);
                Iterator<FixSession> sessionIterator = sessions.iterator();
                while(sessionIterator.hasNext()) {
                    FixSession session = sessionIterator.next();
                    if(!session.isEnabled()) {
                        SLF4JLoggerProxy.debug(this,
                                               "Discarding disabled session {}",
                                               session);
                        brokerService.reportBrokerStatusFromAll(session,
                                                                FixSessionStatus.DISABLED);
                        sessionIterator.remove();
                    }
                }
                FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
                // Initiate broker connections.
//                try {
//                    jmxExporter = new JmxExporter();
//                    jmxExporter.setRegistrationBehavior(JmxExporter.REGISTRATION_REPLACE_EXISTING);
//                } catch (JMException e) {
//                    SLF4JLoggerProxy.warn(this,
//                                          e);
//                }
                synchronized(initiators) {
                    for(FixSession initiatorSession : sessions) {
                        quickfix.SessionID initiatorSessionId = new quickfix.SessionID(initiatorSession.getSessionId());
                        quickfix.SessionSettings initiatorSessionSettings = brokerService.generateSessionSettings(Lists.newArrayList(initiatorSession));
                        quickfix.ThreadedSocketInitiator initiator = new quickfix.ThreadedSocketInitiator(this,
                                                                                                          fixSettingsProvider.getMessageStoreFactory(initiatorSessionSettings),
                                                                                                          initiatorSessionSettings,
                                                                                                          fixSettingsProvider.getLogFactory(initiatorSessionSettings),
                                                                                                          fixSettingsProvider.getMessageFactory());
                        initiator.start();
                        // TODO try/catch?
                        initiators.put(initiatorSessionId,
                                       initiator);
                    }
                }
                initializeAcceptor();
//                // Initiate JMX (for application MBeans).
//                MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
//                mbeanServer.registerMBean(new ORSAdmin(quickFixSender,
//                                                       idFactory,
//                                                       userManager),
//                                                       new ObjectName(JMX_NAME));
                activated = true;
            }
        } catch (Exception e) {
            PlatformServices.handleException(this,
                                             "Unable to activate DARE",
                                             e);
            throw e;
        }
    }
    /**
     * Gets the cluster work unit UID.
     *
     * @return a <code>String</code> value
     */
    @ClusterWorkUnitUid
    public String getClusterWorkUnitUid()
    {
        return clusterWorkUnitUid;
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
     * Indicate if this node has been activated.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isActive()
    {
        return activated;
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
     * Get the sender value.
     *
     * @return a <code>QuickFIXSender</code> value
     */
    public QuickFIXSender getSender()
    {
        return sender;
    }
    /**
     * Sets the sender value.
     *
     * @param inSender a <code>QuickFIXSender</code> value
     */
    public void setSender(QuickFIXSender inSender)
    {
        sender = inSender;
    }
    /**
     * Initializes the acceptor if necessary.
     *
     * @throws ConfigError if an error occurs initializing the session
     * @throws JMException if an error occurs creating the JMX exporter
     */
    private void initializeAcceptor()
            throws ConfigError, JMException
    {
        if(socketAcceptor == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Initializing acceptor");
            int totalInstances = clusterData.getTotalInstances();
            int instanceId = clusterData.getInstanceNumber();
            List<FixSession> sessions = fixSessionProvider.findFixSessions(true,
                                                                           instanceId,
                                                                           totalInstances);
            Iterator<FixSession> sessionIterator = sessions.iterator();
            while(sessionIterator.hasNext()) {
                FixSession session = sessionIterator.next();
                if(!session.isEnabled()) {
                    SLF4JLoggerProxy.debug(this,
                                           "Discarding disabled session {}",
                                           session);
                    brokerService.reportBrokerStatusFromAll(session,
                                                            FixSessionStatus.DISABLED);
                    sessionIterator.remove();
                }
            }
            if(sessions.isEmpty()) {
                SLF4JLoggerProxy.debug(this,
                                       "No sessions to initialize at this time");
                return;
            }
            SessionSettings fixSessionSettings = brokerService.generateSessionSettings(sessions);
            fixSessionSettings.setString(quickfix.Acceptor.SETTING_SOCKET_ACCEPT_ADDRESS,
                                         String.valueOf(dareAcceptorHostname));
            fixSessionSettings.setString(quickfix.Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                                         String.valueOf(dareAcceptorPort));
            FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
            if(SLF4JLoggerProxy.isInfoEnabled(this)) {
                Set<String> sessionsForDisplay = new TreeSet<>();
                Iterator<SessionID> sectionIterator = fixSessionSettings.sectionIterator();
                while(sectionIterator.hasNext()) {
                    sessionsForDisplay.add(sectionIterator.next().toString());
                }
                SLF4JLoggerProxy.info(this,
                                      "{} has the following sessions: {}",
                                      this,
                                      sessionsForDisplay);
            }
            socketAcceptor = new DynamicSessionThreadedSocketAcceptor(this,
                                                                      fixSettingsProvider.getMessageStoreFactory(fixSessionSettings),
                                                                      fixSessionSettings,
                                                                      fixSettingsProvider.getLogFactory(fixSessionSettings),
                                                                      fixSettingsProvider.getMessageFactory(),
                                                                      sessionService);
            provider = new DynamicAcceptorSessionProvider(this,
                                                          brokerService,
                                                          fixSessionProvider,
                                                          sessionNameProvider,
                                                          fixSettingsProvider,
                                                          clusterData);
            socketAcceptor.setSessionProvider(new InetSocketAddress(dareAcceptorHostname,
                                                                    dareAcceptorPort),
                                              provider);
//            jmxExporter = new JmxExporter();
//            jmxExporter.setRegistrationBehavior(JmxExporter.REGISTRATION_REPLACE_EXISTING);
//            exportedAcceptorName = jmxExporter.register(socketAcceptor);
            socketAcceptor.start();
            ports.add(new PortDescriptor(dareAcceptorPort,
                                         "DARE Acceptor"));
        }
    }
    /**
     * Reports backup status, if necessary, for the sessions for which this instance is serving as backup.
     */
    private void reportBackupStatus()
    {
        // deliberately select all sessions because we need to register as disabled if appropriate, even if the instance isn't an affinity match
        for(FixSession fixSession : fixSessionProvider.findFixSessions(1,
                                                                       1)) {
            if(fixSession.isEnabled()) {
                if(brokerService.isAffinityMatch(fixSession,
                                                 clusterData)) {
                    if(isPrimary) {
                        // report nothing, status will be reported elsewhere
                    } else {
                        // this is an enabled session which is an affinity match for this instance, but we not the primary in the cluster, report backup
                        SLF4JLoggerProxy.debug(this,
                                               "{} reporting backup status for {}",
                                               this,
                                               fixSession);
                        FixSessionStatusEvent fixSessionStatusEvent = new SimpleFixSessionUnavailableEvent(new quickfix.SessionID(fixSession.getSessionId()),
                                                                                                           new BrokerID(fixSession.getBrokerId()),
                                                                                                           FixSessionStatus.BACKUP);
                        eventBusService.post(fixSessionStatusEvent);
                    }
                } else {
                    // this session is enabled, but not an affinity match for this instance, nothing to do
                }
            } else {
                // report disabled status
                SLF4JLoggerProxy.debug(this,
                                       "{} reporting disabled status for {}",
                                       this,
                                       fixSession);
                FixSessionStatusEvent fixSessionStatusEvent = new SimpleFixSessionUnavailableEvent(new quickfix.SessionID(fixSession.getSessionId()),
                                                                                                   new BrokerID(fixSession.getBrokerId()),
                                                                                                   FixSessionStatus.DISABLED);
                eventBusService.post(fixSessionStatusEvent);
            }
        }
    }
    /**
     * Gets the FIX message from the given string, if any.
     *
     * @param inLine a <code>String</code> value
     * @return a <code>quickfix.Message</code> value or <code>null</code>
     * @throws quickfix.InvalidMessage if the message cannot be constructed
     */
    private quickfix.Message getMessageFromLine(String inLine)
            throws quickfix.InvalidMessage
    {
        if(inLine.contains("8=")) {
            return new quickfix.Message(inLine.substring(inLine.indexOf("8=")));
        } else {
            return null;
        }
    }
    /**
     * Gets the quickfix.SessionID value appropriate for FIX injection from the given message.
     *
     * @param inMessage a <code>quickfix.Message</code> value
     * @return a <code>quickfix.SessionID</code> value
     * @throws quickfix.FieldNotFound if the session ID cannot be constructed
     */
    private quickfix.SessionID getInjectionSessionIdFrom(quickfix.Message inMessage)
            throws quickfix.FieldNotFound
    {
        String beginString = inMessage.getHeader().getString(quickfix.field.BeginString.FIELD);
        String senderCompId = inMessage.getHeader().getString(quickfix.field.SenderCompID.FIELD);
        String targetCompId = inMessage.getHeader().getString(quickfix.field.TargetCompID.FIELD);
        return new quickfix.SessionID(beginString,
                             targetCompId,
                             senderCompId);
    }
    /**
     * Process the given application message.
     *
     * @param inMessage a <code>quickfix.Message</code> value
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inPriority an <code>int</code> value
     * @throws quickfix.UnsupportedMessageType if the message is of an unsupported message type
     */
    private void doFromApp(quickfix.Message inMessage,
                           quickfix.SessionID inSessionId,
                           int inPriority)
            throws quickfix.UnsupportedMessageType
    {
        ServerFixSession fixSession = brokerService.getServerFixSession(inSessionId);
        Messages.QF_FROM_APP.info(getCategory(inMessage),
                                  inMessage,
                                  fixSession);
        FIXMessageUtil.logMessage(inMessage);
        logMessage(inMessage,
                   fixSession);
        // accept only certain message types
        if(getSupportedMessages() != null && !getSupportedMessages().isAccepted(inMessage)) {
            Messages.QF_DISALLOWED_MESSAGE.info(getCategory(inMessage));
            throw new quickfix.UnsupportedMessageType();
        }
        addToQueue(new MessagePackage(inMessage,
                                      MessageType.FROM_APP,
                                      inSessionId,
                                      Hierarchy.Flat,
                                      inPriority));
    }
    /**
     * Logs the given message, analyzed using the receiver's data dictionary, at the debugging level.
     *
     * @param inMessage a <code>quickfix.Message</code> value
     * @param inBroker a <code>ServerFixSession</code> value
     */
    private void logMessage(quickfix.Message inMessage,
                            ServerFixSession inBroker)
    {
        Object category = getCategory(inMessage);
        if(SLF4JLoggerProxy.isDebugEnabled(category)) {
            Messages.ANALYZED_MESSAGE.debug(category,
                                            new AnalyzedMessage(inBroker.getDataDictionary(),
                                                                inMessage).toString());
        }
    }
    /**
     * Processes the given message package.
     *
     * @param messagePackage a <code>MessagePackage</code> value
     */
    private void processMessage(MessagePackage messagePackage)
    {
        quickfix.SessionID session = messagePackage.sessionId;
        quickfix.Message msg = messagePackage.message;
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
                                                                  dataDictionary.getHumanFieldValue(quickfix.field.TradSesStatus.FIELD,
                                                                                                    msg.getString(quickfix.field.TradSesStatus.FIELD)));
                        }
                        // Send message to client.
                        sendToClientTrades(false,
                                           serverFixSession,
                                           msg,
                                           Originator.Broker,
                                           messagePackage.hierarchy);
//                        if(!allowDeliverToCompID && msg.getHeader().isSetField(DeliverToCompID.FIELD)) {
//                            // OpenFIX certification: we reject all DeliverToCompID since we don't redeliver.
//                            try {
//                                Message reject = serverFixSession.getFIXMessageFactory().createSessionReject(msg,
//                                                                                                             SessionRejectReason.COMPID_PROBLEM);
//                                reject.setString(Text.FIELD,
//                                                 Messages.QF_COMP_ID_REJECT.getText(msg.getHeader().getString(DeliverToCompID.FIELD)));
//                                getSender().sendToTarget(reject,
//                                                         session);
//                            } catch (SessionNotFound ex) {
//                                Messages.QF_COMP_ID_REJECT_FAILED.error(getCategory(msg),
//                                                                        ex,
//                                                                        serverFixSession.toString());
//                            }
//                            break;
//                        }
                    } catch (quickfix.FieldNotFound e) {
                        SLF4JLoggerProxy.error(this,
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
     * Generate a session status value for the given attributes.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inConnected a <code>boolean</code> value
     * @param inStopped a <code>boolean</code> value
     * @return a <code>FixSessionStatus</code> value
     */
    private FixSessionStatus generateSessionStatus(FixSession inFixSession,
                                                   boolean inConnected,
                                                   boolean inStopped)
    {
        FixSessionStatus status;
        if(inStopped) {
            status = FixSessionStatus.STOPPED;
        } else {
            if(inConnected) {
                status = FixSessionStatus.CONNECTED;
            } else {
                quickfix.Session session = quickfix.Session.lookupSession(new quickfix.SessionID(inFixSession.getSessionId()));
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
                    if(brokerService.isSessionTime(new quickfix.SessionID(inFixSession.getSessionId()))) {
                        status = FixSessionStatus.NOT_CONNECTED;
                    } else {
                        status = FixSessionStatus.DISCONNECTED;
                    }
                }
            }
        }
        return status;
    }
    /**
     * Updates broker status.
     *
     * @param inBroker a <code>Broker</code> value
     * @param inStatus a <code>FixSessionStatus</code> value
     */
    private void updateStatus(FixSession inFixSession,
                              FixSessionStatus status)
    {
        BrokerID brokerId = new BrokerID(inFixSession.getBrokerId());
        quickfix.SessionID sessionId = new quickfix.SessionID(inFixSession.getSessionId());
        final FixSessionStatusEvent fixSessionStatusEvent;
        if(status.isLoggedOn()) {
            fixSessionStatusEvent = new SimpleFixSessionAvailableEvent(sessionId,
                                                                       brokerId,
                                                                       status);
        } else {
            fixSessionStatusEvent = new SimpleFixSessionUnavailableEvent(sessionId,
                                                                         brokerId,
                                                                         status);
        }
        eventBusService.post(fixSessionStatusEvent);
        Messages.QF_SENDING_STATUS.info(this,
                                        status,
                                        brokerId);
//        ServerFixSession brokerStatus = brokerService.getServerFixSession(brokerId);
//        brokerService.reportBrokerStatus(brokerId,
//                                         brokerStatus.getActiveFixSession().getStatus());
////        if(oldStatus == inStatus) {
////            return;
////        }
//        // TODO is this crap necessary?
//        ActiveFixSession broker = brokerService.generateBroker(inFixSession);
//        for(BrokerStatusListener listener : brokerStatusListeners) {
//            listener.receiveBrokerStatus(brokerStatus.getActiveFixSession());
//        }
    }
    /**
     * Send provisional status until a session can be updated with more accurate status.
     *
     * @param inSession a <code>FixSession</code> value
     */
    private void sendInterimStatus(FixSession inSession)
    {
        BrokerID brokerId = new BrokerID(inSession.getBrokerId());
        quickfix.SessionID sessionId = new quickfix.SessionID(inSession.getSessionId());
        Session liveSession = Session.lookupSession(new SessionID(inSession.getSessionId()));
        FixSessionStatus currentStatus;
        if(liveSession == null) {
            if(isPrimary) {
                currentStatus = FixSessionStatus.DISCONNECTED;
            } else {
                currentStatus = FixSessionStatus.BACKUP;
            }
        } else {
            if(liveSession.isSessionTime()) {
                currentStatus = FixSessionStatus.NOT_CONNECTED;
            } else {
                currentStatus = FixSessionStatus.DISCONNECTED;
            }
        }
        final FixSessionStatusEvent fixSessionStatusEvent;
        if(currentStatus.isLoggedOn()) {
            fixSessionStatusEvent = new SimpleFixSessionAvailableEvent(sessionId,
                                                                       brokerId,
                                                                       currentStatus);
        } else {
            fixSessionStatusEvent = new SimpleFixSessionUnavailableEvent(sessionId,
                                                                         brokerId,
                                                                         currentStatus);
        }
        eventBusService.post(fixSessionStatusEvent);
        Messages.QF_SENDING_STATUS.info(this,
                                        currentStatus,
                                        brokerId);
    }
    /**
     * Sends the given message to the subscribed clients.
     *
     * @param inIsAdmin a <code>boolean</code> value
     * @param inServerFixSession a <code>ServerFixSession</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     * @param inOriginator an <code>Originator</code> value
     * @param inHierarchy a <code>Hierarchy</code> value
     */
    private void sendToClientTrades(boolean inIsAdmin,
                                    ServerFixSession inServerFixSession,
                                    quickfix.Message inMessage,
                                    Originator inOriginator,
                                    Hierarchy inHierarchy)
    {
        final IncomingFixMessageEvent fixMessageEvent;
        if(inIsAdmin) {
            fixMessageEvent = new SimpleIncomingFixAdminMessageEvent(new quickfix.SessionID(inServerFixSession.getActiveFixSession().getFixSession().getSessionId()),
                                                                     new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getBrokerId()),
                                                                     inMessage);
        } else {
            fixMessageEvent = new SimpleIncomingFixAppMessageEvent(new quickfix.SessionID(inServerFixSession.getActiveFixSession().getFixSession().getSessionId()),
                                                                   new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getBrokerId()),
                                                                   inMessage);
        }
//        UserID actor = null;
//        if(!inIsAdmin) {
//            actor = messageOwnerService.getMessageOwner(fixMessageEvent,
//                                                        fixMessageEvent.getSessionId(),
//                                                        new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getBrokerId()));
//        }
        // Apply message modifiers.
        boolean orderIntercepted = false;
        if((inOriginator == Originator.Broker) && (inServerFixSession.getResponseModifiers() != null)) {
            for(MessageModifier responseModifier : inServerFixSession.getResponseModifiers()) {
                try {
                    responseModifier.modify(inServerFixSession,
                                            inMessage);
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
        }
//        TradeMessage reply;
//        try {
//            reply = FIXConverter.fromQMessage(inMessage,
//                                              inOriginator,
//                                              fixMessageEvent.getBrokerID(),
//                                              inHierarchy,
//                                              actor,
//                                              actor);
//        } catch (MessageCreationException ex) {
//            Messages.QF_REPORT_FAILED.error(getCategory(inMessage),
//                                            ex,
//                                            inMessage,
//                                            inServerFixSession);
//            return;
//        }
        try {
            String msgType = inMessage.getHeader().getString(quickfix.field.MsgType.FIELD);
            // check to see if the message has been blacklisted
            if(dontForwardMessages.contains(msgType)) {
                SLF4JLoggerProxy.debug(this,
                                       "{} is in the blacklist of messages {} for clients and will not be forwarded", //$NON-NLS-1$
                                       inMessage,
                                       dontForwardMessages);
                return;
            }
            if(!forwardMessages.isEmpty() && !forwardMessages.contains(msgType)) {
                SLF4JLoggerProxy.debug(this,
                                       "{} is not in the whitelist of messages {} for clients and will not be forwarded", //$NON-NLS-1$
                                       inMessage,
                                       forwardMessages);
                return;
            }
            if(orderIntercepted) {
                eventBusService.post(new SimpleIncomingOrderInterceptedEvent(fixMessageEvent.getSessionId(),
                                                                             fixMessageEvent.getMessage()));
                SLF4JLoggerProxy.debug(this,
                                       "{} intercepted and not sent to client",
                                       inMessage);
            } else {
                // TODO this could go on a special channel...
                eventBusService.post(fixMessageEvent);
            }
        } catch (quickfix.FieldNotFound e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "On send to client: {}",
                                  ExceptionUtils.getRootCauseMessage(e));
        } finally {
//            Messages.QF_SENDING_REPLY.info(getCategory(inMessage),
//                                           reply);
//            try {
//                String msgType = inMessage.getHeader().getString(MsgType.FIELD);
//                // check to see if the message has been blacklisted
//                if(dontForwardMessages.contains(msgType)) {
//                    SLF4JLoggerProxy.debug(this,
//                                           "{} is in the blacklist of messages {} for clients and will not be forwarded", //$NON-NLS-1$
//                                           inMessage,
//                                           dontForwardMessages);
//                    return;
//                }
//            } catch (FieldNotFound e) {
//                SLF4JLoggerProxy.warn(this,
//                                      e,
//                                      "On send to client: {}",
//                                      ExceptionUtils.getRootCauseMessage(e));
//                return;
//            }
//            if(orderIntercepted) {
//                SLF4JLoggerProxy.debug(this,
//                                      "{} intercepted and not sent to client",
//                                      reply);
//            } else {
//                eventBusService.post(new SimpleIncomingTradeMessage(reply));
//            }
        }
    }
    /**
     * Gets the category for the given message.
     *
     * @param inMessage a <code>quickfix.Message</code> value
     * @return an <code>Object</code> value
     */
    private Object getCategory(quickfix.Message inMessage)
    {
        if(FIXMessageUtil.isHeartbeat(inMessage)) {
            return HEARTBEAT_CATEGORY;
        } else if(FIXMessageUtil.isTestRequest(inMessage)) {
            return TEST_REQUEST_CATEGORY;
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
                    if(inMessagePackage.getMessage().isSetField(quickfix.field.ClOrdID.FIELD)) {
                        // this message absolutely should have a root order ID
                        Messages.QF_NO_ROOT_ORDER_ID.warn(DeployAnywhereRoutingEngine.this,
                                                          inMessagePackage.getMessage());
                        return;
                    }
                    // no order family and no ClOrdId on this message - this is a "special" message and will be processed in order by the standard session queue, not a root order id queue
                    key = inMessagePackage.getSessionId().toString();
                } else {
                    try {
                        String msgType = inMessagePackage.getMessage().getHeader().getString(quickfix.field.MsgType.FIELD);
                        if(!standardMessageTypes.contains(msgType)) {
                            // this is not a standard message (ER or CANCEL REJECT) - we'll pass it on, but use the standard session queue, not a root order id queue
                            key = inMessagePackage.getSessionId().toString();
                        } else {
                            key = rootOrderId.getValue();
                        }
                    } catch (quickfix.FieldNotFound ignored) {
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
         * @param inSessionId a <code>quickfix.SessionID</code> value
         */
        private SessionMessageProcessingQueue(quickfix.SessionID inSessionId)
        {
            super("SessionMessageProcessingQueue-" + inSessionId, //$NON-NLS-1$
                  new PriorityBlockingQueue<MessagePackage>());
            sessionId = inSessionId;
        }
        /**
         * session ID value for this message queue
         */
        private final quickfix.SessionID sessionId;
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
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          "{} on execution process: {}",
                                          queue.key,
                                          ExceptionUtils.getRootCauseMessage(e));
                } else {
                    SLF4JLoggerProxy.warn(this,
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
         * @param inMessage a <code>quickfix.Message</code> value
         * @param inMessageType a <code>MessageType</code> value
         * @param inSessionId a <code>SessionID</code> value
         * @param inHierarchy a <code>Hierarchy</code> value
         * @param inPriority an <code>int</code> value
         */
        private MessagePackage(quickfix.Message inMessage,
                               MessageType inMessageType,
                               quickfix.SessionID inSessionId,
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
         * @return a <code>quickfix.Message</code> value
         */
        private quickfix.Message getMessage()
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
        private quickfix.SessionID getSessionId()
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
        private final quickfix.Message message;
        /**
         * message type value
         */
        private final MessageType messageType;
        /**
         * session ID value
         */
        private final quickfix.SessionID sessionId;
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
            extends Pair<quickfix.SessionID,String>
    {
        /**
         * Create a new OrderKey instance.
         *
         * @param inSessionId a <code>quickfix.SessionID</code> value
         * @param inKeyValue a <code>String</code> value
         */
        private MessageKey(quickfix.SessionID inSessionId,
                           String inKeyValue)
        {
            super(inSessionId,
                  inKeyValue);
        }
    }
    /**
     * indicates the "normal" message types we handle
     */
    private static final Set<String> standardMessageTypes = Sets.newHashSet(quickfix.field.MsgType.ORDER_CANCEL_REJECT,
                                                                            quickfix.field.MsgType.EXECUTION_REPORT);
    /**
     * messages that should not be forwarded to clients (empty to forward all messages)
     */
    private final Set<String> dontForwardMessages = Sets.newHashSet(quickfix.field.MsgType.LOGON,
                                                                    quickfix.field.MsgType.HEARTBEAT,
                                                                    quickfix.field.MsgType.TEST_REQUEST,
                                                                    quickfix.field.MsgType.RESEND_REQUEST,
                                                                    quickfix.field.MsgType.REJECT,
                                                                    quickfix.field.MsgType.SEQUENCE_RESET,
                                                                    quickfix.field.MsgType.LOGOUT);
    /**
     * messages that should be forwarded to client (empty for all messages)
     */
    private final Set<String> forwardMessages = Sets.newHashSet();
    /**
     * logger category used for heartbeats
     */
    private static final String HEARTBEAT_CATEGORY = DeployAnywhereRoutingEngine.class.getName()+".HEARTBEATS"; //$NON-NLS-1$
    /**
     * logger category used for test requests
     */
    private static final String TEST_REQUEST_CATEGORY = DeployAnywhereRoutingEngine.class.getName()+".TEST_REQUESTS"; //$NON-NLS-1$
    /**
     * sessions that have been created
     */
    private final Set<quickfix.SessionID> activeSessions = new CopyOnWriteArraySet<>();
    /**
     * session queues by session ID
     */
    private final Map<quickfix.SessionID,SessionMessageProcessingQueue> sessionQueues = new HashMap<>();
    /**
     * holds processing queues for root order id
     */
    private final Map<MessageKey,OrderMessageProcessingQueue> orderQueues = new HashMap<>();
    /**
     * active initiators by session id
     */
    private final Map<quickfix.SessionID,quickfix.ThreadedSocketInitiator> initiators = new HashMap<>();
    /**
     * accepts incoming FIX messages
     */
    private ThreadedSocketAcceptor socketAcceptor;
    /**
     * holds session ids that have been manually stopped
     */
    private final Set<SessionID> stoppedAcceptorSessions = new HashSet<>();
    /**
     * stores sessions that have been created, may or may not be currently logged on
     */
    private final Set<SessionID> createdSessions = new HashSet<>();
    /**
     * stores sessions that are currently active
     */
    private final Set<SessionID> loggedOnSessions = new HashSet<>();
    /**
     * indicates if the object is actively running as opposed to starting, shutting down, or stopped
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    /**
     * indicates if execution pools are allowed to be allocated or not
     */
    private final AtomicBoolean suspendExecutionPools = new AtomicBoolean(false);
    /**
     * guards access to session activities
     */
    private final Object sessionLock = new Object();
    /**
     * executes jobs at scheduled times
     */
    private ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(2,new ThreadFactoryBuilder().setNameFormat("DAREScheduler%d").build());
    /**
     * supported messages value
     */
    private MessageFilter supportedMessages;
    /**
     * maximum number of order pools to have active at once
     */
    @Value("${metc.dare.max.execution.pools:5}")
    private int maxExecutionPools = 5;
    /**
     * hostname on which to listen for incoming FIX messages
     */
    @Value("${metc.dare.acceptor.hostname:0.0.0.0}")
    private String dareAcceptorHostname;
    /**
     * port on which to listen for incoming FIX messages
     */
    @Value("${metc.dare.acceptor.port:9800}")
    private int dareAcceptorPort;
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
     * indicates if this host is the primary, active host
     */
    private boolean isPrimary = false;
    /**
     * indicates if this host has been successfully activated
     */
    private boolean activated = false;
    /**
     * uniquely identifies this work unit in the cluster - determined at runtime
     */
    private String clusterWorkUnitUid;
    /**
     * task used to update status of sessions to backup, initially
     */
    private Runnable backupStatusTask;
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
     * provides access to session name services
     */
    @Autowired
    private SessionNameProvider sessionNameProvider;
    /**
     * provides access to report services 
     */
    @Autowired
    private ReportService reportService;
    /**
     * provides access to event bus services
     */
    @Autowired
    private EventBusService eventBusService;
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
     * sends messages as necessary
     */
    @Autowired
    private QuickFIXSender sender;
    /**
     * provides access to fix sessions
     */
    @Autowired
    private FixSessionProvider fixSessionProvider;
    /**
     * provides access to session services
     */
    @Autowired
    private SessionService sessionService;
    /**
     * provides dynamic sessions
     */
    private AcceptorSessionProvider provider;
    /**
     * indicates ports in use
     */
    private final Collection<PortDescriptor> ports = Lists.newArrayList();
}
