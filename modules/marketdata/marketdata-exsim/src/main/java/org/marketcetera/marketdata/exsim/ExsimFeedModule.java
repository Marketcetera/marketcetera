package org.marketcetera.marketdata.exsim;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.BatchQueueProcessor;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventType;
import org.marketcetera.event.HasEventType;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.quickfix.EventLogFactory;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogFactory;
import quickfix.MemoryStoreFactory;
import quickfix.Message;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Supplies market data from the Marketcetera Exchange Simulator.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class ExsimFeedModule
        extends Module
        implements DataEmitter,AbstractMarketDataModuleMXBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#getFeedStatus()
     */
    @Override
    public String getFeedStatus()
    {
        if(feedStatus == null) {
            return FeedStatus.UNKNOWN.name();
        }
        return feedStatus.name();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#reconnect()
     */
    @Override
    public void reconnect()
    {
        preStop();
        preStart();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return supportedCapabilities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#getAssetClasses()
     */
    @Override
    public Set<AssetClass> getAssetClasses()
    {
        return supportedAssetClasses;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
        SLF4JLoggerProxy.debug(this,
                               "Received a data flow request: {}",
                               inRequest);
        if(!feedStatus.isRunning()) {
            // TODO message
            throw new RequestDataException(new IllegalArgumentException("Feed is unavailable"));
        }
        Object payload = inRequest.getData();
        try {
            Validate.notNull(payload,
                             "Data request data required");
            if(payload instanceof String) {
                String stringPayload = (String)payload;
                try {
                    doMarketDataRequest(MarketDataRequestBuilder.newRequestFromString(stringPayload),
                                        inRequest,
                                        inSupport);
                } catch (Exception e) {
                    throw new UnsupportedOperationException(stringPayload + " is not a valid market data request or a valid feed status request");
                }
            } else if(payload instanceof MarketDataRequest) {
                doMarketDataRequest((MarketDataRequest)payload,
                                    inRequest,
                                    inSupport);
            } else {
                throw new UnsupportedOperationException("Unsupported data request data type: " + payload.getClass().getSimpleName());
            }
        } catch (Exception e) {
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.debug(this,
                                       e,
                                       "Market data request failed: {}",
                                       ExceptionUtils.getRootCauseMessage(e));
            } else {
                SLF4JLoggerProxy.warn(this,
                                      "Market data request failed: {}",
                                      ExceptionUtils.getRootCauseMessage(e));
            }
            throw new RequestDataException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        Validate.notNull(exsimFeedConfig,
                         "Exsim feed configuration required");
        try {
            fixMessageProcessor = new FixMessageProcessor();
            fixMessageProcessor.start();
            FIXVersion version = exsimFeedConfig.getFixVersion();
            messageFactory = version.getMessageFactory();
            application = new FixApplication();
            sessionId = exsimFeedConfig.getSessionId();
            MessageStoreFactory messageStoreFactory = new MemoryStoreFactory();
            SessionSettings sessionSettings = new SessionSettings();
            exsimFeedConfig.populateSessionSettings(sessionSettings);
            File workspaceDir = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
            File quoteFeedLogDir = new File(workspaceDir,
                                            "marketdata-exsim-" + UUID.randomUUID().toString());
            FileUtils.forceMkdir(quoteFeedLogDir);
            sessionSettings.setString(sessionId,
                                      FileLogFactory.SETTING_FILE_LOG_PATH,
                                      quoteFeedLogDir.getAbsolutePath());
            sessionSettings.setString(SessionFactory.SETTING_CONNECTION_TYPE,
                                      SessionFactory.INITIATOR_CONNECTION_TYPE);
            SLF4JLoggerProxy.debug(this,
                                   "Session settings: {}",
                                   sessionSettings);
            LogFactory logFactory = new EventLogFactory(sessionSettings);
            socketInitiator = new SocketInitiator(application,
                                                  messageStoreFactory,
                                                  sessionSettings,
                                                  logFactory,
                                                  messageFactory.getUnderlyingMessageFactory());
            socketInitiator.start();
        } catch (IOException | ConfigError e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new ModuleException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        updateFeedStatus(FeedStatus.OFFLINE);
        if(fixMessageProcessor != null) {
            try {
                fixMessageProcessor.stop();
            } catch (Exception ignored) {}
            fixMessageProcessor = null;
        }
        if(socketInitiator != null) {
            try {
                socketInitiator.stop();
            } catch (Exception ignored) {}
            socketInitiator = null;
        }
        messageFactory = null;
        sessionId = null;
        application = null;
    }
    /**
     * Create a new ExsimFeedModule instance.
     *
     * @param inInstanceUrn a <code>ModuleURN</code> value
     */
    protected ExsimFeedModule(ModuleURN inInstanceUrn)
    {
        super(inInstanceUrn,
              false);
        feedStatus = FeedStatus.OFFLINE;
    }
    /**
     * Perform the market data request
     *
     * @param inPayload
     * @param inRequest
     * @param inSupport
     * @throws ExecutionException 
     * @throws FieldNotFound 
     * @throws SessionNotFound 
     */
    private void doMarketDataRequest(MarketDataRequest inPayload,
                                     DataRequest inRequest,
                                     DataEmitterSupport inSupport)
            throws FieldNotFound, ExecutionException, SessionNotFound
    {
        // build some number of market data request object and fire it off
        List<Instrument> requestedInstruments = Lists.newArrayList();
        for(String symbol : inPayload.getSymbols()) {
            Instrument instrument = symbolResolverService.resolveSymbol(symbol);
            if(instrument == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Could not resolve symbol {}",
                                      instrument);
            } else {
                requestedInstruments.add(instrument);
            }
        }
        String id = UUID.randomUUID().toString();
        Message marketDataRequest = messageFactory.newMarketDataRequest(id,
                                                                        requestedInstruments,
                                                                        inPayload.getExchange(),
                                                                        Lists.newArrayList(inPayload.getContent()),
                                                                        quickfix.field.SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES);
        SLF4JLoggerProxy.debug(this,
                               "Built {} for {} from {}",
                               marketDataRequest,
                               inRequest,
                               inPayload);
        receiversByMdReqId.put(id,
                               inSupport);
        if(!Session.sendToTarget(marketDataRequest,
                                 sessionId)) {
            receiversByMdReqId.remove(id);
            throw new StopDataFlowException(null); // TODO
        }
    }
    private final Map<String,DataEmitterSupport> receiversByMdReqId = Maps.newHashMap();
    /**
     * Update the feed status to the new given value.
     *
     * @param inNewStatus a <code>FeedStatus</code> value
     */
    private void updateFeedStatus(FeedStatus inNewStatus)
    {
        if(inNewStatus == feedStatus) {
            return;
        }
        feedStatus = inNewStatus;
    }
    /**
     * 
     *
     *
     * @param inMessage
     * @param inMsgType
     * @return
     * @throws FieldNotFound 
     */
    private Deque<Event> getEventsFromMessage(Message inMessage,
                                              String inMsgType)
            throws FieldNotFound
    {
        Deque<Event> events = Lists.newLinkedList();
        // if the message is a snapshot, then it's one instrument/exchange per message. if it's an incremental refresh, then it can be multiple instrument/exchange tuples per message
        switch(inMsgType) {
            case quickfix.field.MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH:
                Instrument instrument = FIXMessageUtil.getInstrumentFromMessageFragment(inMessage);
                if(instrument == null) {
                    throw new UnsupportedOperationException("Message does not specify an instrument");
                }
                String exchange = FIXMessageUtil.getSecurityExchangeFromMessageFragment(inMessage);
                // what types of events do we have here?
                List<Group> mdEntries = getMdEntriesFromMessage(inMessage);
                SLF4JLoggerProxy.debug(this,
                                       "Extracted {} from {}",
                                       mdEntries,
                                       inMessage);
                break;
            case quickfix.field.MsgType.MARKET_DATA_INCREMENTAL_REFRESH:
                mdEntries = getMdEntriesFromMessage(inMessage);
                SLF4JLoggerProxy.debug(this,
                                       "Extracted {} from {}",
                                       mdEntries,
                                       inMessage);
                break;
            default:
                throw new UnsupportedOperationException("Cannot retrieve events from message type " + inMsgType);
        }
        return events;
    }
    private List<Group> getMdEntriesFromMessage(Message inMessage)
            throws FieldNotFound
    {
        List<Group> mdEntries = Lists.newArrayList();
        int noMdEntries = inMessage.getInt(quickfix.field.NoMDEntries.FIELD);
        for(int i=1;i<=noMdEntries;i++) {
            Group mdEntryGroup = messageFactory.createGroup(inMessage.getHeader().getString(quickfix.field.MsgType.FIELD),
                                                            quickfix.field.NoMDEntries.FIELD);
            mdEntryGroup = inMessage.getGroup(i,
                                              mdEntryGroup);
            mdEntries.add(mdEntryGroup);
        }
        return mdEntries;
    }
    /**
     * 
     *
     *
     * @param inEvents
     * @param inRequestId
     * @param inIsSnapshot
     */
    private void publishEvents(Deque<Event> inEvents,
                               String inRequestId,
                               boolean inIsSnapshot)
    {
        if(inRequestId == null) {
            throw new UnsupportedOperationException("Cannot process response without MDReqID (262)");
        }
        if(inEvents.isEmpty()) {
            throw new UnsupportedOperationException("Cannot process response with no events");
        }
        DataEmitterSupport receiver = receiversByMdReqId.get(inRequestId);
        if(receiver == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Not publishing {} to {} because it seems to have just been canceled",
                                   inEvents,
                                   inRequestId);
            return;
        }
        Event lastEvent = inEvents.getLast();
        if(lastEvent instanceof HasEventType) {
            ((HasEventType)lastEvent).setEventType(inIsSnapshot?EventType.SNAPSHOT_FINAL:EventType.UPDATE_FINAL);
        }
        for(Event event : inEvents) {
            try {
                // this fulfills the EVENT_BOUNDARY contract
                if(event instanceof HasEventType) {
                    HasEventType eventWithEventType = (HasEventType)event;
                    if(eventWithEventType.getEventType() == null || !eventWithEventType.getEventType().isComplete()) {
                        eventWithEventType.setEventType(inIsSnapshot?EventType.SNAPSHOT_PART:EventType.UPDATE_PART);
                    }
                }
                receiver.send(event);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Caught and ignored exception while sending {} to {}",
                                      event,
                                      receiver);
            }
        }
    }
    /**
     * Process incoming FIX messages received from the exchange simulator.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class FixMessageProcessor
            extends BatchQueueProcessor<Message>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.BatchQueueProcessor#processData(java.util.Deque)
         */
        @Override
        protected void processData(Deque<Message> inData)
                throws Exception
        {
            for(Message message : inData) {
                try {
                    SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                           "{} procssing {}",
                                           this,
                                           message);
                    String msgType = message.getHeader().getString(quickfix.field.MsgType.FIELD);
                    String requestId = null;
                    if(message.isSetField(quickfix.field.MDReqID.FIELD)) {
                        requestId = message.getString(quickfix.field.MDReqID.FIELD);
                    }
                    boolean isSnapshot = false;
                    switch(msgType) {
                        case quickfix.field.MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH:
                            isSnapshot = true;
                            // fall through on purpose
                        case quickfix.field.MsgType.MARKET_DATA_INCREMENTAL_REFRESH:
                            publishEvents(getEventsFromMessage(message,
                                                               msgType),
                                          requestId,
                                          isSnapshot);
                            break;
                        case quickfix.field.MsgType.MARKET_DATA_REQUEST_REJECT:
                            // TODO cancel corresponding request?
                            break;
                        default:
                            SLF4JLoggerProxy.warn(ExsimFeedModule.this,
                                                  "{} ignoring unexpected message {}",
                                                  this,
                                                  message);
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(ExsimFeedModule.this,
                                          e,
                                          "{} unable to process {}: {}",
                                          this,
                                          message,
                                          ExceptionUtils.getRootCauseMessage(e));
                }
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.BatchQueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(Message inData)
        {
            super.add(inData);
        }
        /**
         * Create a new FixMessageProcessor instance.
         */
        private FixMessageProcessor()
        {
            super(ExsimFeedModule.class.getSimpleName()+"-MessageProcessor");
        }
    }
    /**
     * Provides an API to the FIX connection to the exchange simulator.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class FixApplication
            implements Application
    {
        /* (non-Javadoc)
         * @see quickfix.Application#onCreate(quickfix.SessionID)
         */
        @Override
        public void onCreate(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(ExsimFeedModule.this,
                                   "Session {} created",
                                   inSessionId);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#onLogon(quickfix.SessionID)
         */
        @Override
        public void onLogon(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(ExsimFeedModule.this,
                                   "Session {} logon",
                                   inSessionId);
            updateFeedStatus(FeedStatus.AVAILABLE);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#onLogout(quickfix.SessionID)
         */
        @Override
        public void onLogout(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(ExsimFeedModule.this,
                                   "Session {} logout",
                                   inSessionId);
            updateFeedStatus(FeedStatus.OFFLINE);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void toAdmin(Message inMessage,
                            SessionID inSessionId)
        {
            SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                   "{} sending admin {}",
                                   inSessionId,
                                   inMessage);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void fromAdmin(Message inMessage,
                              SessionID inSessionId)
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
        {
            SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                   "{} received admin {}",
                                   inSessionId,
                                   inMessage);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void toApp(Message inMessage,
                          SessionID inSessionId)
                throws DoNotSend
        {
            SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                   "{} sending app {}",
                                   inSessionId,
                                   inMessage);
            FIXMessageUtil.logMessage(inMessage);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void fromApp(Message inMessage,
                            SessionID inSessionId)
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
        {
            SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                   "{} received app {}",
                                   inSessionId,
                                   inMessage);
            FIXMessageUtil.logMessage(inMessage);
            if(fixMessageProcessor == null) {
                
            } else {
                fixMessageProcessor.add(inMessage);
            }
        }
    }
    /**
     * processes incoming FIX messages
     */
    private FixMessageProcessor fixMessageProcessor;
    /**
     * session ID value that the module will use to connect
     */
    private SessionID sessionId;
    /**
     * handles physical connection to the simualted exchange
     */
    private SocketInitiator socketInitiator;
    /**
     * message factory for the specified FIX version
     */
    private FIXMessageFactory messageFactory;
    /**
     * manages the FIX connection to the exchange
     */
    private FixApplication application;
    /**
     * configuration used to connect to the exchange
     */
    @Autowired
    private ExsimFeedConfig exsimFeedConfig;
    /**
     * resolves symbols to instruments
     */
    @Autowired
    private SymbolResolverService symbolResolverService;
    /**
     * current status of the feed
     */
    private volatile FeedStatus feedStatus;
    /**
     * supported asset classes for this provider
     */
    private static final Set<AssetClass> supportedAssetClasses = EnumSet.of(AssetClass.CONVERTIBLE_BOND,AssetClass.CURRENCY,AssetClass.EQUITY,AssetClass.FUTURE,AssetClass.OPTION);
    /**
     * supported capabilities for this provider
     */
    private static final Set<Capability> supportedCapabilities = EnumSet.of(Capability.AGGREGATED_DEPTH,Capability.BBO10,Capability.EVENT_BOUNDARY,Capability.LATEST_TICK,Capability.MARKET_STAT,Capability.TOP_OF_BOOK);
}
