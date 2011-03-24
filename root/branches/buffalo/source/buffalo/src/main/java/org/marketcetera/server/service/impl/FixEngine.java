package org.marketcetera.server.service.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.GuardedBy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.server.service.*;
import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.trade.*;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.AnalyzedMessage;
import org.marketcetera.util.quickfix.SpringSessionDescriptor;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;

import quickfix.*;
import quickfix.field.*;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class FixEngine
        implements InitializingBean, Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        if(isRunning()) {
            SLF4JLoggerProxy.warn(FixOrderDestination.class,
                                  "{} already started",
                                  this);
            return;
        }
        buildDestinationMap();
        try {
            socket = new SocketInitiator(fixApplication,
                                         sessionSettings.getQMessageStoreFactory(),
                                         sessionSettings.getQSettings(),
                                         sessionSettings.getQLogFactory(),
                                         new DefaultMessageFactory());
            socket.start();
        } catch (Exception e) {
            SLF4JLoggerProxy.error(FixOrderDestination.class,
                                   e,
                                   "Unable to start {}",
                                   this);
            stop();
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        if(!isRunning()) {
            SLF4JLoggerProxy.warn(FixOrderDestination.class,
                                  "{} already stopped",
                                  this);
            return;
        }
        try {
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "{} stopping {}",
                                   this,
                                   fixApplication);
            socket.stop();
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "{} successfully stopped",
                                   fixApplication);
        } finally {
            running.set(false);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /**
     * Get the sessionSettings value.
     *
     * @return a <code>SpringSessionSettings</code> value
     */
    public SpringSessionSettings getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * Sets the sessionSettings value.
     *
     * @param a <code>SpringSessionSettings</code> value
     */
    public void setSessionSettings(SpringSessionSettings inSessionSettings)
    {
        sessionSettings = inSessionSettings;
    }
    /**
     * 
     *
     *
     * @return
     */
    public Map<OrderDestinationID,SpringSessionDescriptor> getSessionMap()
    {
        synchronized(sessionMap) {
            return Collections.unmodifiableMap(sessionMap);
        }
    }
    /**
     * 
     *
     *
     * @param inSessionMap
     */
    public void setSessionMap(Map<OrderDestinationID,SpringSessionDescriptor> inSessionMap)
    {
        synchronized(sessionMap) {
            sessionMap.clear();
            sessionMap.putAll(inSessionMap);
        }
    }
    /**
     * Sets the name value.
     *
     * @param a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = StringUtils.trimToNull(inName);
    }
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notNull(name,
                         "Fix engine name must not be null");
        Validate.notNull(sessionSettings,
                         "The session settings for " + this + " must not be null");
        Validate.notEmpty(sessionMap,
                          "The session map for " + this + " must not be empty");
    }
    /**
     * 
     *
     *
     */
    private void buildDestinationMap()
    {
        // build the map of sessionIDs to OrderDestinations
        synchronized(destinations) {
            for(Map.Entry<OrderDestinationID,SpringSessionDescriptor> entry : sessionMap.entrySet()) {
                OrderDestination orderDestination = orderDestinationManager.getOrderDestinationFor(entry.getKey());
                if(orderDestination == null) {
                    throw new IllegalStateException("No order destination for " + entry.getKey()); // TODO throw typed exception
                }
                destinations.put(entry.getValue().getQSessionID(),
                                 orderDestination);
            }
        }
    }
    /**
     * 
     *
     *
     * @param inOrder
     */
    void send(Order inOrder,
              OrderDestination inOrderDestination)
    {
        // match the order destination ID to the session
        SessionID sessionId = getSessionIdFor(inOrderDestination);
        SLF4JLoggerProxy.debug(FixOrderDestination.class,
                               "Selected {} for {}",
                               sessionId,
                               inOrder);
        // check to see if the needed session is available
        if(!isAvailable(sessionId)) {
            throw new RuntimeException(sessionId + " is not available"); // TODO make typed exception
        }
        // the session is available
        CachedSessionInfo sessionInfo = CachedSessionInfo.getCachedSessionInfo(sessionId); 
        try {
            // transform the order to a FIX message
            Message message = FIXConverter.toQMessage(sessionInfo.getMessageFactory(),
                                                      sessionInfo.session.getDataDictionary(),
                                                      inOrder);
            // TODO ensure order is allowed
            if(inOrderDestination instanceof HasFixMessageModifiers) {
                HasFixMessageModifiers hasFixMessageModifiers = (HasFixMessageModifiers)inOrderDestination;
                List<MessageModifier> messageModifiers = hasFixMessageModifiers.getPreSendMessageModifiers();
                if(messageModifiers != null) {
                    try {
                        for(MessageModifier modifier : messageModifiers) {
                            modifier.modify(message);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error while applying message modifiers to " + message, // TODO change to typed exception
                                                   e);
                    }
                }
            }
            if(Session.sendToTarget(message,
                                    sessionId)) {
                SLF4JLoggerProxy.info(FixOrderDestination.class,
                                      "Sent {} to {}",
                                      message,
                                      sessionId);
            } else {
                SLF4JLoggerProxy.warn(FixOrderDestination.class,
                                      "Unable to send {} to {}!",
                                      message,
                                      sessionId);
            }
        } catch (I18NException e) {
            throw new RuntimeException(e); // TODO make a typed exception
        } catch (SessionNotFound e) {
            throw new RuntimeException(e); // TODO make a typed exception
        }
    }
    /**
     * Gets the <code>SessionID</code> for the given <code>OrderDestination</code>.
     *
     * @param inDestination an <code>OrderDestination</code> value
     * @return a <code>SessionID</code>
     * @throws RuntimeException if no <code>SessionID</code> exists for the given <code>OrderDestination</code>
     */
    private SessionID getSessionIdFor(OrderDestination inDestination)
    {
        synchronized(sessionMap) {
            SpringSessionDescriptor descriptor = sessionMap.get(inDestination.getId());
            if(descriptor == null) {
                // TODO add typed exception
                throw new RuntimeException("No session descriptor for " + inDestination);
            }
            return descriptor.getQSessionID();
        }
    }
    /**
     * 
     *
     *
     * @return
     */
    private boolean isAvailable(SessionID inSessionID)
    {
        synchronized(availableSessions) {
            return availableSessions.contains(inSessionID);
        }
    }
    /**
     * 
     *
     *
     * @param inSessionID
     */
    private void sessionUnavailable(SessionID inSessionID)
    {
        synchronized(availableSessions) {
            availableSessions.remove(inSessionID);
        }
    }
    /**
     * 
     *
     *
     * @param inSessionID
     */
    private void sessionAvailable(SessionID inSessionID)
    {
        synchronized(availableSessions) {
            availableSessions.add(inSessionID);
        }
    }
    /**
     * 
     *
     *
     * @param inMessage
     * @param inInfo
     */
    private static void logMessage(Message inMessage,
                                   CachedSessionInfo inInfo)
    {
        Object category = (FIXMessageUtil.isHeartbeat(inMessage) ? HEARTBEAT_CATEGORY : FixEngine.class);
        if(SLF4JLoggerProxy.isDebugEnabled(category)) {
            Messages.ANALYZED_MESSAGE.debug(category,
                                            new AnalyzedMessage(inInfo.getDataDictionary().getDictionary(),
                                                                inMessage).toString());
        }        
    }
    /**
     * 
     *
     *
     * @param inOrderDestination
     * @param inMessage
     */
    private void sendToOrderDestination(OrderDestination inOrderDestination,
                                        Message inMessage)
    {
        // Convert reply to FIX Agnostic message
        TradeMessage reply;
        try {
            // TODO start
            reply = FIXConverter.fromQMessage(inMessage,
                                              Originator.Broker,
                                              new BrokerID(inOrderDestination.getId().getValue()),
                                              null, // TODO principals.getActorID(),
                                              null); // TODO principals.getViewerID());
            // TODO end
            Messages.QF_SENDING_REPLY.info(getCategory(inMessage),
                                           reply);
            orderDestinationManager.receive(reply,
                                            inOrderDestination);
        } catch (MessageCreationException ex) {
            Messages.QF_REPORT_FAILED.error(getCategory(inMessage),
                                            ex,
                                            inMessage,
                                            inOrderDestination.toString());
            return;
        }
    }
    /**
     * 
     *
     *
     * @param msg
     * @return
     */
    private Object getCategory(Message msg)
    {
        if (FIXMessageUtil.isHeartbeat(msg)) {
            return HEARTBEAT_CATEGORY;
        }
        return FixOrderDestination.class;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private static class CachedSessionInfo
    {
        /**
         * 
         *
         *
         * @param inSessionID
         * @return
         */
        private static CachedSessionInfo getCachedSessionInfo(SessionID inSessionID)
        {
            synchronized(sessionInfoCache) {
                CachedSessionInfo sessionInfo = sessionInfoCache.get(inSessionID);
                if(sessionInfo == null) {
                    Session session = Session.lookupSession(inSessionID);
                    if(session == null) {
                        throw new RuntimeException("No session for " + inSessionID); // TODO make this a typed exception
                    }
                    sessionInfo = new CachedSessionInfo(session);
                    sessionInfoCache.put(inSessionID,
                                         sessionInfo);
                }
                return sessionInfo;
            }
        }
        /**
         * Create a new CachedSessionInfo instance.
         *
         * @param inSession
         */
        private CachedSessionInfo(Session inSession)
        {
            session = inSession;
        }
        /**
         * 
         *
         *
         * @return
         */
        private synchronized FIXMessageFactory getMessageFactory()
        {
            if(messageFactory == null) {
                FIXVersion version = FIXVersion.getFIXVersion(session.getSessionID().getBeginString());
                messageFactory = version.getMessageFactory();
            }
            return messageFactory;
        }
        /**
         * 
         *
         *
         * @return
         */
        private synchronized FIXDataDictionary getDataDictionary()
        {
            if(dataDictionary == null) {
                dataDictionary = new FIXDataDictionary(session.getDataDictionary());
            }
            return dataDictionary;
        }
        /**
         * 
         *
         *
         * @return
         */
        private Session getSession()
        {
            return session;
        }
        /**
         * 
         */
        private volatile FIXMessageFactory messageFactory;
        /**
         * 
         */
        private volatile FIXDataDictionary dataDictionary;
        /**
         * the session associated with the sessionID
         */
        private final Session session;
        /**
         * lookup of cached session info by sessionID
         */
        @GuardedBy("sessionInfoCache")
        private static final Map<SessionID,CachedSessionInfo> sessionInfoCache = new HashMap<SessionID,CachedSessionInfo>();
    }
    /**
     * Manages messages to and from a FIX application.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private class FixApplication
            implements Application
    {
        /* (non-Javadoc)
         * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void fromAdmin(Message inMessage,
                              SessionID inSessionId)
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
        {
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "fromAdmin {} for {}",
                                   inMessage,
                                   inSessionId);
            OrderDestination destination = getDestinationFor(inSessionId);
            Messages.QF_FROM_ADMIN.info(getCategory(inMessage),
                                        inMessage,
                                        destination);
            sendToOrderDestination(destination,
                                   inMessage);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void fromApp(Message inMessage,
                            SessionID inSessionId)
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
        {
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "fromApp {} for {}",
                                   inMessage,
                                   inSessionId);
            OrderDestination destination = getDestinationFor(inSessionId);
            Messages.QF_FROM_APP.info(getCategory(inMessage),
                                      inMessage,
                                      destination);
            CachedSessionInfo info = CachedSessionInfo.getCachedSessionInfo(inSessionId);
            logMessage(inMessage,
                       info);
            // Accept only certain message types.
            // TODO start
            //           if (!getSupportedMessages().isAccepted(msg)){
            //               Messages.QF_DISALLOWED_MESSAGE.info(getCategory(msg));
            //               throw new UnsupportedMessageType();
            //           }
            // TODO end
            // Report trading session status in a human-readable format.
            if(FIXMessageUtil.isTradingSessionStatus(inMessage)) {
                Messages.QF_TRADE_SESSION_STATUS.info(getCategory(inMessage),
                                                      info.getDataDictionary().getHumanFieldValue(TradSesStatus.FIELD,
                                                                                                  inMessage.getString(TradSesStatus.FIELD)));
            }
            // Send message to client.
            sendToOrderDestination(destination,
                                   inMessage);
            // OpenFIX certification: we reject all DeliverToCompID since we don't re-deliver.
            if(inMessage.getHeader().isSetField(DeliverToCompID.FIELD)) {
                try {
                    Message reject = info.getMessageFactory().createSessionReject(inMessage,
                                                                                  SessionRejectReason.COMPID_PROBLEM);
                    reject.setString(Text.FIELD,
                                     Messages.QF_COMP_ID_REJECT.getText(inMessage.getHeader().getString(DeliverToCompID.FIELD)));
                    // TODO start
//                    getSender().sendToTarget(reject,
//                                             info.getSession());
                    // TODO end
                } catch (SessionNotFound ex) {
                    Messages.QF_COMP_ID_REJECT_FAILED.error(getCategory(inMessage),
                                                            ex,
                                                            destination.toString());
                }
                return;
            }
            // Record execution reports
            if(FIXMessageUtil.isExecutionReport(inMessage)) {
                char ordStatus = inMessage.getChar(OrdStatus.FIELD);
                if((ordStatus==OrdStatus.FILLED) ||
                   (ordStatus==OrdStatus.PARTIALLY_FILLED)) {
                    // TODO start
//                    sendTradeRecord(inMessage);
                    // TODO end
                }
            }
        }
        /* (non-Javadoc)
         * @see quickfix.Application#onCreate(quickfix.SessionID)
         */
        @Override
        public void onCreate(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "FIX session created for {}",
                                   inSessionId);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#onLogon(quickfix.SessionID)
         */
        @Override
        public void onLogon(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "onLogon for {}",
                                   inSessionId);
            sessionAvailable(inSessionId);
            OrderDestination destination = getDestinationFor(inSessionId);
            if(destination instanceof UpdatableStatus) {
                UpdatableStatus statusReceiver = (UpdatableStatus)destination;
                statusReceiver.setStatus(DestinationStatus.AVAILABLE);
            } else {
                SLF4JLoggerProxy.warn(FixEngine.class,
                                      "Unable to update the status of {} to {}",
                                      destination,
                                      DestinationStatus.AVAILABLE);
            }
        }
        /* (non-Javadoc)
         * @see quickfix.Application#onLogout(quickfix.SessionID)
         */
        @Override
        public void onLogout(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "onLogout for {}",
                                   inSessionId);
            sessionUnavailable(inSessionId);
            OrderDestination destination = getDestinationFor(inSessionId);
            if(destination instanceof UpdatableStatus) {
                UpdatableStatus statusReceiver = (UpdatableStatus)destination;
                statusReceiver.setStatus(DestinationStatus.UNAVAILABLE);
            } else {
                SLF4JLoggerProxy.warn(FixEngine.class,
                                      "Unable to update the status of {} to {}",
                                      destination,
                                      DestinationStatus.UNAVAILABLE);
            }
        }
        /* (non-Javadoc)
         * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void toAdmin(Message inMessage,
                            SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "toAdmin {} for {}",
                                   inMessage,
                                   inSessionId);
            OrderDestination destination = getDestinationFor(inSessionId);
            Messages.QF_TO_ADMIN.info(getCategory(inMessage),
                                      inMessage,
                                      destination);
            CachedSessionInfo info = CachedSessionInfo.getCachedSessionInfo(inSessionId);
            logMessage(inMessage,
                       info);
            // If the QuickFIX/J engine is sending a reject (e.g. the counterparty sent us a malformed execution report, for
            // example, and we are rejecting it), we notify the client of the rejection.
            if(FIXMessageUtil.isReject(inMessage)) {
                try {
                    String msgType=(inMessage.isSetField(MsgType.FIELD) ? null : inMessage.getString(RefMsgType.FIELD));
                    String msgTypeName = info.getDataDictionary().getHumanFieldValue(MsgType.FIELD,
                                                                                     msgType);
                    inMessage.setString(Text.FIELD,
                                        Messages.QF_IN_MESSAGE_REJECTED.getText(msgTypeName,
                                                                                inMessage.getString(Text.FIELD)));
                } catch (FieldNotFound ex) {
                    Messages.QF_MODIFICATION_FAILED.warn(getCategory(inMessage),
                                                         ex,
                                                         inMessage,
                                                         destination.toString());
                    // Send original message instead of modified one.
                }
                sendToOrderDestination(destination,
                                       inMessage);
            }
        }
        /* (non-Javadoc)
         * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void toApp(Message inMessage,
                          SessionID inSessionId)
                throws DoNotSend
        {
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "toApp {} for {}",
                                   inMessage,
                                   inSessionId);
            OrderDestination destination = getDestinationFor(inSessionId);
            Messages.QF_TO_APP.info(getCategory(inMessage),
                                    inMessage,
                                    destination);
            CachedSessionInfo info = CachedSessionInfo.getCachedSessionInfo(inSessionId);
            logMessage(inMessage,
                       info);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "FixApplication";
        }
        /**
         * 
         *
         *
         * @param inSessionId
         * @return
         */
        private OrderDestination getDestinationFor(SessionID inSessionId)
        {
            OrderDestination destination = destinations.get(inSessionId);
            if(destination == null) {
                throw new RuntimeException("No destination for " + inSessionId); // TODO replace with typed exception
            }
            return destination;
        }
    }
    /**
     * 
     */
    private final Set<SessionID> availableSessions = new HashSet<SessionID>();
    /**
     * the actual connection to the FIX acceptor
     */
    private final FixApplication fixApplication = new FixApplication();
    /**
     * the name of the order destination, human-readable
     */
    private volatile String name;
    /**
     * the socket connection to the FIX acceptor
     */
    private volatile SocketInitiator socket;
    /**
     * the QFix session settings 
     */
    private volatile SpringSessionSettings sessionSettings;
    /**
     * 
     */
    @Autowired
    private OrderDestinationManager orderDestinationManager;
    /**
     * correlates <code>OrderDestinationID</code> to <code>SessionDescriptor</code> values
     */
    private final Map<OrderDestinationID,SpringSessionDescriptor> sessionMap = new HashMap<OrderDestinationID,SpringSessionDescriptor>();
    /**
     * 
     */
    private final Map<SessionID,OrderDestination> destinations = new HashMap<SessionID,OrderDestination>();
    /**
     * 
     */
    private static final String HEARTBEAT_CATEGORY = FixOrderDestination.class.getName() + ".HEARTBEATS"; //$NON-NLS-1$
    /**
     * indicates if the destination is running
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
