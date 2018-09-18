package org.marketcetera.modules.fix;

import java.util.Collection;
import java.util.Set;

import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.HasSessionId;
import org.marketcetera.fix.SessionSettingsGenerator;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.HasMutableStatus;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

import quickfix.Application;
import quickfix.ApplicationExtended;
import quickfix.ConfigError;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.UnsupportedMessageType;
import quickfix.mina.SessionConnector;

/* $License$ */

/**
 * Provides common behavior for FIX modules.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public abstract class AbstractFixModule
        extends AbstractDataReemitterModule
        implements ApplicationExtended
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
        super.requestData(inRequest,
                          inSupport);
        DataRequester dataRequester = new DataRequester(inRequest,
                                                        inSupport);
        if(dataRequester.isForAllSessionIds()) {
            requestsForAllSessionMessages.add(dataRequester);
        } else {
            for(SessionID sessionId : dataRequester.getFixDataRequest().getRequestedSessionIds()) {
                requestsBySessionId.getUnchecked(sessionId).add(dataRequester);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
        super.cancel(inFlowID,
                     inRequestID);
        DataRequester dataRequester = requestsByDataFlowId.getIfPresent(inFlowID);
        requestsByDataFlowId.invalidate(inFlowID);
        if(dataRequester != null) {
            if(dataRequester.isForAllSessionIds()) {
                requestsForAllSessionMessages.remove(dataRequester);
            } else {
                for(SessionID sessionId : dataRequester.getFixDataRequest().getRequestedSessionIds()) {
                    requestsBySessionId.getUnchecked(sessionId).remove(dataRequester);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onReceiveData(java.lang.Object, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected HasFIXMessage onReceiveData(Object inData,
                                          DataEmitterSupport inDataSupport)
    {
        if(!(inData instanceof HasFIXMessage)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasFIXMessage.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        if(!(inData instanceof HasSessionId)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasSessionId.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        HasFIXMessage messagePackage = (HasFIXMessage)inData;
        Message message = ((HasFIXMessage)inData).getMessage();
        SessionID targetSessionId = ((HasSessionId)inData).getSessionId();
        try {
            boolean messageSent = Session.sendToTarget(message,
                                                       targetSessionId);
            if(!messageSent) {
                if(inData instanceof HasMutableStatus) {
                    HasMutableStatus mutableStatus = (HasMutableStatus)inData;
                    mutableStatus.setFailed(true);
                    mutableStatus.setErrorMessage("FIX message not sent to broker because FIX session was unavailable");
                }
                throw new ReceiveDataException(new IllegalArgumentException("Message not sent: " + message));
            }
        } catch (SessionNotFound e) {
            throw new ReceiveDataException(e);
        }
        return messagePackage;
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onCreate(quickfix.SessionID)
     */
    @Override
    public void onCreate(SessionID inSessionId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} created",
                              inSessionId);
        sendStatus(inSessionId,
                   true,
                   false);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogon(quickfix.SessionID)
     */
    @Override
    public void onLogon(SessionID inSessionId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} logged on",
                              inSessionId);
        sendStatus(inSessionId,
                   true,
                   true);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#onLogout(quickfix.SessionID)
     */
    @Override
    public void onLogout(SessionID inSessionId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} logged out",
                              inSessionId);
        sendStatus(inSessionId,
                   false,
                   false);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toAdmin(Message inMessage,
                        SessionID inSessionId)
    {
        if(!FIXMessageUtil.isHeartbeat(inMessage)) {
            if(SLF4JLoggerProxy.isDebugEnabled(FIXMessageUtil.prettyPrintCategory)) {
                SLF4JLoggerProxy.info(this,
                                      "{} sending admin:",
                                      inSessionId);
                FIXMessageUtil.logMessage(inSessionId,
                                          inMessage);
            } else {
                SLF4JLoggerProxy.info(this,
                                      "{} sending admin: {}",
                                      inSessionId,
                                      inMessage);
            }
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
        if(!FIXMessageUtil.isHeartbeat(inMessage)) {
            if(SLF4JLoggerProxy.isDebugEnabled(FIXMessageUtil.prettyPrintCategory)) {
                SLF4JLoggerProxy.info(this,
                                      "{} received admin:",
                                      inSessionId);
                FIXMessageUtil.logMessage(inSessionId,
                                          inMessage);
            } else {
                SLF4JLoggerProxy.info(this,
                                      "{} received admin: {}",
                                      inSessionId,
                                      inMessage);
            }
        }
        sendMessageIfNecessary(inMessage,
                               inSessionId,
                               true);
    }
    /* (non-Javadoc)
     * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toApp(Message inMessage,
                      SessionID inSessionId)
            throws DoNotSend
    {
        if(SLF4JLoggerProxy.isDebugEnabled(FIXMessageUtil.prettyPrintCategory)) {
            SLF4JLoggerProxy.info(this,
                                  "{} sending app:",
                                  inSessionId);
            FIXMessageUtil.logMessage(inSessionId,
                                      inMessage);
        } else {
            SLF4JLoggerProxy.info(this,
                                  "{} sending app: {}",
                                  inSessionId,
                                  inMessage);
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromApp(Message inMessage,
                        SessionID inSessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
    {
        if(SLF4JLoggerProxy.isDebugEnabled(FIXMessageUtil.prettyPrintCategory)) {
            SLF4JLoggerProxy.info(this,
                                  "{} recevied app:",
                                  inSessionId);
            FIXMessageUtil.logMessage(inSessionId,
                                      inMessage);
        } else {
            SLF4JLoggerProxy.info(this,
                                  "{} received app: {}",
                                  inSessionId,
                                  inMessage);
        }
        sendMessageIfNecessary(inMessage,
                               inSessionId,
                               false);
    }
    /* (non-Javadoc)
     * @see quickfix.ApplicationExtended#canLogon(quickfix.SessionID)
     */
    @Override
    public boolean canLogon(SessionID inSessionID)
    {
        return true;
    }
    /* (non-Javadoc)
     * @see quickfix.ApplicationExtended#onBeforeSessionReset(quickfix.SessionID)
     */
    @Override
    public void onBeforeSessionReset(SessionID inSessionID)
    {
    }
    /**
     * Activate the module.
     *
     * @throws Exception if the module could not be activated
     */
    void activate()
            throws Exception
    {
        instanceData = clusterService.getInstanceData();
        Collection<FixSession> fixSessions = getFixSessions();
        if(!fixSessions.isEmpty()) {
            SessionSettings sessionSettings = SessionSettingsGenerator.generateSessionSettings(getFixSessions(),
                                                                                               fixSettingsProviderFactory);
            SLF4JLoggerProxy.debug(this,
                                   "Starting FIX module with session settings: {}",
                                   sessionSettings);
            engine = createEngine(this,
                                  fixSettingsProviderFactory.create(),
                                  sessionSettings);
            engine.start();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        if(engine != null) {
            engine.stop();
        }
        engine = null;
    }
    /**
     * Create a new AbstractFixModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    protected AbstractFixModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * Create the underlying engine with the given attributes.
     *
     * @param inApplication an <code>Application</code> value
     * @param inFixSettingsProvider a <code>FixSettingsProvider</code> value
     * @param inSessionSettings a <code>SessionSettings</code> value
     * @return a <code>SessionConnector</code> value
     * @throws ConfigError if the engine cannot be created
     */
    protected abstract SessionConnector createEngine(Application inApplication,
                                                     FixSettingsProvider inFixSettingsProvider,
                                                     SessionSettings inSessionSettings)
            throws ConfigError;
    /**
     * Get the FIX sessions for this module.
     *
     * @return a <code>Collection&lt;FixSession&gt;</code> value
     */
    protected abstract Collection<FixSession> getFixSessions();
    /**
     * Get the broker service value.
     *
     * @return a <code>BrokerService</code> value
     */
    protected BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sends status for the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inCreated a <code>boolean</code> value
     * @param inLoggedOn a <code>boolean</code> value
     */
    private void sendStatus(SessionID inSessionId,
                            boolean inCreated,
                            boolean inLoggedOn)
    {
        Broker broker = brokerService.getBroker(inSessionId);
        if(broker == null) {
            SLF4JLoggerProxy.warn(this,
                                  "No broker for {}, cannot report status",
                                  inSessionId);
            return;
        }
        FixSession fixSession = broker.getFixSession();
        FixSessionStatus status;
        if(inLoggedOn) {
            status = FixSessionStatus.CONNECTED;
        } else {
            Session session = Session.lookupSession(new SessionID(fixSession.getSessionId()));
            if(session == null) {
                if(fixSession.isEnabled()) {
                    if(brokerService.isAffinityMatch(fixSession,
                                                     instanceData)) {
                        status = FixSessionStatus.BACKUP;
                    } else {
                        status = FixSessionStatus.AFFINITY_MISMATCH;
                    }
                } else {
                    status = FixSessionStatus.DISABLED;
                }
            } else {
                if(brokerService.isSessionTime(new SessionID(fixSession.getSessionId()))) {
                    status = FixSessionStatus.NOT_CONNECTED;
                } else {
                    status = FixSessionStatus.DISCONNECTED;
                }
            }
        }
        BrokerStatus brokerStatus = brokerService.generateBrokerStatus(broker.getFixSession(),
                                                                       instanceData,
                                                                       status,
                                                                       inLoggedOn);
        synchronized(brokerStatusBroadcasterLock) {
            brokerService.postBrokerStatus(brokerStatus);
        }
    }
    /**
     * Send the given message with the given attributes, to all interested data flows.
     *
     * @param inMessage a <code>Message</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inIsAdmin a <code>boolean</code> value
     * @throws FieldNotFound if the message cannot be processed
     */
    private void sendMessageIfNecessary(Message inMessage,
                                        SessionID inSessionId,
                                        boolean inIsAdmin)
            throws FieldNotFound
    {
        for(DataRequester requester : requestsForAllSessionMessages) {
            if(requesterIsInterested(requester,
                                     inSessionId,
                                     inMessage,
                                     inIsAdmin)) {
                requester.getDataEmitterSupport().send(new FIXMessageHolder(inSessionId,
                                                                            inMessage));
            }
        }
        Set<DataRequester> requestersForSession = requestsBySessionId.getIfPresent(inSessionId);
        if(requestersForSession != null) {
            for(DataRequester requester : requestersForSession) {
                if(requesterIsInterested(requester,
                                         inSessionId,
                                         inMessage,
                                         inIsAdmin)) {
                    requester.getDataEmitterSupport().send(new FIXMessageHolder(inSessionId,
                                                                                inMessage));
                }
            }
        }
    }
    /**
     * Determine if the given requester is interested in the given message.
     *
     * @param inRequester a <code>DataRequester</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     * @param isAdmin a <code>boolean</code> value
     * @return a <code>boolean</code> value
     * @throws FieldNotFound if the message cannot be processed
     */
    private boolean requesterIsInterested(DataRequester inRequester,
                                          SessionID inSessionId,
                                          Message inMessage,
                                          boolean isAdmin)
            throws FieldNotFound
    {
        return ((isAdmin && inRequester.getFixDataRequest().getIncludeAdmin()) || (!isAdmin && inRequester.getFixDataRequest().getIncludeApp())) &&
                isAcceptedByWhiteList(inRequester.getFixDataRequest(),inMessage) &&
                isAcceptedByBlackList(inRequester.getFixDataRequest(),inMessage);
    }
    /**
     * Determine if the given message is accepted by the data request whitelist.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
     * @throws FieldNotFound if the message cannot be processed
     */
    private boolean isAcceptedByWhiteList(FixDataRequest inFixDataRequest,
                                          Message inMessage)
            throws FieldNotFound
    {
        return (inFixDataRequest.getMessageWhiteList().isEmpty() || inFixDataRequest.getMessageWhiteList().contains(inMessage.getHeader().getString(quickfix.field.MsgType.FIELD)));
    }
    /**
     * Determine if the given message is accepted by the data request blacklist.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
     * @throws FieldNotFound if the message cannot be processed
     */
    private boolean isAcceptedByBlackList(FixDataRequest inFixDataRequest,
                                          Message inMessage)
            throws FieldNotFound
    {
        return (inFixDataRequest.getMessageBlackList().isEmpty() || !inFixDataRequest.getMessageBlackList().contains(inMessage.getHeader().getString(quickfix.field.MsgType.FIELD)));
    }
    /**
     * Tracks data for each data flow request
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class DataRequester
    {
        /**
         * Indicates if this request applies to all sessions or not.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isForAllSessionIds()
        {
            return fixDataRequest.getRequestedSessionIds().isEmpty();
        }
        /**
         * Get the fixDataRequest value.
         *
         * @return a <code>FixDataRequest</code> value
         */
        private FixDataRequest getFixDataRequest()
        {
            return fixDataRequest;
        }
        /**
         * Get the dataRequest value.
         *
         * @return a <code>DataRequest</code> value
         */
        @SuppressWarnings("unused")
        private DataRequest getDataRequest()
        {
            return dataRequest;
        }
        /**
         * Get the dataEmitterSupport value.
         *
         * @return a <code>DataEmitterSupport</code> value
         */
        private DataEmitterSupport getDataEmitterSupport()
        {
            return dataEmitterSupport;
        }
        /**
         * Create a new DataRequester instance.
         *
         * @param inRequest a <code>DataRequest</code> value
         * @param inDataEmitterSupport a <code>DataEmitterSupport</code> value
         */
        private DataRequester(DataRequest inRequest,
                              DataEmitterSupport inDataEmitterSupport)
        {
            dataRequest = inRequest;
            dataEmitterSupport = inDataEmitterSupport;
            if(!(inRequest.getData() instanceof FixDataRequest)) {
                throw new RequestDataException(new IllegalArgumentException("Request data must be instance of FixDataRequest")); // TODO message
            }
            fixDataRequest = (FixDataRequest)inRequest.getData();
        }
        /**
         * underlying data flow request value
         */
        private final FixDataRequest fixDataRequest;
        /**
         * data flow request value
         */
        private final DataRequest dataRequest;
        /**
         * data emitter support value
         */
        private final DataEmitterSupport dataEmitterSupport;
    }
    /**
     * identifies this cluster instance
     */
    private ClusterData instanceData;
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * guards access to {@link #brokerStatusBroadcasters}
     */
    private final Object brokerStatusBroadcasterLock = new Object();
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * underlying FIX engine
     */
    private SessionConnector engine;
    /**
     * provides static settings for all FIX sessions (log factory, message store factory, etc)
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * data requests that request data for all sessions
     */
    private final Collection<DataRequester> requestsForAllSessionMessages = Sets.newConcurrentHashSet();
    /**
     * data requests by session id
     */
    private final LoadingCache<SessionID,Set<DataRequester>> requestsBySessionId = CacheBuilder.newBuilder().build(new CacheLoader<SessionID,Set<DataRequester>>() {
        @Override
        public Set<DataRequester> load(SessionID inKey)
                throws Exception
        {
            return Sets.newConcurrentHashSet();
        }}
    );
    /**
     * data requests by data flow id
     */
    private final Cache<DataFlowID,DataRequester> requestsByDataFlowId = CacheBuilder.newBuilder().build();
}
