package org.marketcetera.marketdata;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.AbstractMessageTranslator;
import org.marketcetera.quickfix.EventLogFactory;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MemoryStoreFactory;
import quickfix.Message;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.StringField;
import quickfix.UnsupportedMessageType;
import quickfix.Message.Header;
import quickfix.field.MarketDepth;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.SenderCompID;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TestMessageIndicator;
import quickfix.field.Text;
import quickfix.fix44.MessageFactory;

/**
 * A sample implementation of a market data feed.
 *
 * <p>This feed will return random market data for every symbol queried.
 *
 * @author <a href="mailto:colin@marketcetera.com>Colin DuPlantis</a>
 */
public class MarketceteraFeed 
    extends AbstractMarketDataFeed<MarketceteraFeedToken,
                                   MarketceteraFeedCredentials,
                                   MarketceteraFeedMessageTranslator,
                                   MarketceteraFeedEventTranslator,
                                   Message,
                                   MarketceteraFeed> 
    implements Application 
{
	public static final String SETTING_SENDER_COMP_ID = SenderCompID.class.getSimpleName();
	public static final String SETTING_TARGET_COMP_ID = TargetCompID.class.getSimpleName();
	private int serverPort;
	private String server;
	private SessionID sessionID;
	private final IDFactory idFactory;
	private FeedType feedType;
	private boolean isRunning = false;
	private SocketInitiator socketInitiator;
	private MessageFactory messageFactory;
	private Map<String, Exchanger<Message>> pendingRequests = new WeakHashMap<String, Exchanger<Message>>();
	private final Logger logger;
	private final String url;

	private FIXCorrelationFieldSubscription doQuery(Message query) {
		try {
			Integer marketDepth = null;
			try { marketDepth = query.getInt(MarketDepth.FIELD); } catch (FieldNotFound fnf) { /* do nothing */ }
			String reqID = addReqID(query);
			sendMessage(query);
			return new FIXCorrelationFieldSubscription(reqID, 
			                                           query.getHeader().getString(MsgType.FIELD), 
			                                           marketDepth);
		} catch (SessionNotFound e) {
			logger.error("Session not found while trying to execute async query: "+query);
		} catch (FieldNotFound e) {
			logger.error(e.toString()+" "+query);
		}
		return null;
	}

	private String getReqID(Message inMessage)
	{
	    String reqID = null;
	    try {
	        String msgType = inMessage.getHeader().getString(MsgType.FIELD);
	        StringField reqIDField = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, 
	                                                                    msgType);
	        reqID = inMessage.getField(reqIDField).getValue();
	    } catch (FieldNotFound e) {}

	    return reqID;
	}
	
	private String addReqID(Message query) throws FieldNotFound {
		String reqID = getReqID(query);

		String msgType = query.getHeader().getString(MsgType.FIELD);
		StringField reqIDField = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, msgType);
		try {
			query.getField(reqIDField).toString();
		} catch (FieldNotFound e1) {
		}
		if (reqIDField.getValue() == null || reqIDField.getValue().length()==0){
			try {
				reqID = idFactory.getNext();
			} catch (NoMoreIDsException e) {
				// should never happen
				assert(false);
			}
			reqIDField.setValue(reqID);
			query.setField(reqIDField);
		}
		return reqID;
	}

    private void sendMessage(Message message) 
        throws SessionNotFound 
    {
        Session.sendToTarget(message, 
                             sessionID);
    }

    public MSymbol symbolFromString(String symbolString) {
		if (MarketceteraOptionSymbol.matchesPattern(symbolString)){
			return new MarketceteraOptionSymbol(symbolString);
		}
		return new MSymbol(symbolString);
	}

	public boolean isRunning() 
	{
		return isRunning;
	}
	
	private void setIsRunning(boolean inIsRunning)
	{
	    isRunning = inIsRunning;
	}

	public void start() 
	{
		synchronized(this) {
			try {
				if (!isRunning()) {
					logger.info("Starting connection to: "+url);
					MessageStoreFactory messageStoreFactory = new MemoryStoreFactory();
					SessionSettings sessionSettings;
					sessionSettings = new SessionSettings(MarketceteraFeed.class.getClassLoader().getResourceAsStream("/fixdatafeed.properties"));
					sessionSettings.setString(sessionID, Initiator.SETTING_SOCKET_CONNECT_HOST, server);
					sessionSettings.setLong(sessionID, Initiator.SETTING_SOCKET_CONNECT_PORT, serverPort);
	
					File workspaceDir = MarketceteraFeedPlugin.getDefault().getStoreDirectory();
					File quoteFeedLogDir = new File(workspaceDir, "marketdata");
					if (!quoteFeedLogDir.exists())
					{
						quoteFeedLogDir.mkdir();
					}
					sessionSettings.setString(sessionID, FileLogFactory.SETTING_FILE_LOG_PATH, quoteFeedLogDir.getCanonicalPath());
	
					LogFactory logFactory = new EventLogFactory(sessionSettings);
					messageFactory = new MessageFactory();
					socketInitiator = new SocketInitiator(this, messageStoreFactory, sessionSettings, logFactory, messageFactory);
					socketInitiator.start();
					setIsRunning(true);
					// this method intentionally does not call super.start() because the actual start mechanism happens in fromAdmin
				}
			} catch (Throwable t) {
				logger.error("Exception trying to start Marketcetera feed",
				             t);
				setFeedStatus(FeedStatus.ERROR);
			}
		}
	}

	public void stop() 
	{
		synchronized(this) {
			if (isRunning()) {
				logger.info("Stopping connection to: "+url);
				socketInitiator.stop(true);
				setIsRunning(false);
				super.stop();
			}
		}
	}
	// quickfix.Application methods
	public void fromAdmin(Message message, 
	                      SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,RejectLogon 
	{
		Header header = message.getHeader();
		String msgType = header.getString(MsgType.FIELD);
		if (MsgType.LOGON.equals(msgType)) {
			try {
				boolean testMessageIndicator = header.getBoolean(TestMessageIndicator.FIELD);
				// TODO bolt this into the abstract feed parent 
				feedType = testMessageIndicator ? FeedType.SIMULATED
				                                : FeedType.LIVE;
			} catch (FieldNotFound fnf) {
				feedType = FeedType.LIVE;
			}
			setFeedStatus(FeedStatus.AVAILABLE);
			logger.info("Marketcetera feed received Logon");
		} else if (MsgType.LOGOUT.equals(msgType)) {
			String text = "";
			try {
				text = ": "+message.getString(Text.FIELD);
			} catch (FieldNotFound fnf){ /* do nothing */ }
			logger.info("Marketcetera feed received Logout"+text);
		} else if (!MsgType.HEARTBEAT.equals(msgType)){
			logger.info("Admin message for Marketcetera feed: "+message);
		}
	}

	public void fromApp(Message message, 
	                    SessionID sessionID) 
	    throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType 
	{
		String reqID = null;
		boolean handled = false;
		try {
			StringField correlationField = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, 
			                                                                  message.getHeader().getString(MsgType.FIELD));
			reqID = message.getString(correlationField.getTag());
		} catch (FieldNotFound fnf){
		}
		if (reqID != null && reqID.length() > 0) {
			synchronized (pendingRequests) {
				for (String requestID : pendingRequests.keySet()) {
					if (requestID.equals(reqID)){
						try {
							// the other side should wait on this before we can call exchange
							pendingRequests.get(requestID).exchange(message, 
							                                        1, 
							                                        TimeUnit.NANOSECONDS);
							handled = true;
						} catch (Exception e) {
							// calling side probably timed out...
						}
						break;
					}
				}
				pendingRequests.remove(reqID);
			}
		}
		if (!handled){
			fireMarketDataMessage(message);
		}
	}

	public void onCreate(SessionID sessionID) {
		logger.info("Marketcetera feed session created "+sessionID);
	}
	public void onLogon(SessionID sessionID) {
		setFeedStatus(FeedStatus.AVAILABLE);
	}
	public void onLogout(SessionID sessionID) {
		setFeedStatus(FeedStatus.OFFLINE);
	}
	public void toAdmin(Message message, SessionID sessionID) {
	}
	public void toApp(Message message, SessionID sessionID) throws DoNotSend {
	}
	private void fireMarketDataMessage(Message refresh) 
	{
	    String symbol;
	    try {
            symbol = refresh.getString(Symbol.FIELD);
        } catch (FieldNotFound e) {
            symbol = UNKNOWN_SYMBOL;
        }
        logger.debug(String.format("MarketceteraFeed received response for handle %s",
                                   symbol));
	    dataReceived(symbol, 
	                 refresh);
	}
	private MarketceteraFeed(String inProviderName,
	                         MarketceteraFeedCredentials inCredentials) 
	    throws URISyntaxException, MarketceteraException
	{
	    super(FeedType.UNKNOWN,
	          inProviderName,
	          inCredentials);
        PhotonPlugin plugin = PhotonPlugin.getDefault();
        logger = plugin.getMainLogger();
        url = inCredentials.getURL();
        try {
            idFactory = new InMemoryIDFactory(System.currentTimeMillis(),
                                              String.format("-%s",
                                                            InetAddress.getLocalHost().toString()));
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
        URI feedURI = new URI(url);
        if ((serverPort = feedURI.getPort()) < 0){
            throw new MarketceteraException("Port must be defined on feed URL");
        }
        server = feedURI.getHost();
        String senderCompID = inCredentials.getSenderCompID();
        if (senderCompID == null || 
                senderCompID.length() == 0) {
                senderCompID = idFactory.getNext();
            }
        String targetCompID = inCredentials.getTargetCompID();
        String scheme;
        if (!FIXDataDictionary.FIX_4_4_BEGIN_STRING.equals(scheme = feedURI.getScheme()) ) {
            throw new MarketceteraException("Only FIX.4.4 is supported");
        } else {
            sessionID = new SessionID(scheme, 
                                      senderCompID, 
                                      targetCompID);
        }
	}
	/**
	 * used in a message that does not contain a symbol
	 */
    private static final String UNKNOWN_SYMBOL = "unknown";
	/**
	 * singleton instance of the marketcetera feed
	 */
	private static MarketceteraFeed sInstance;
    /**
     * Gets an instance of <code>MarketceteraFeed</code>.
     * 
     * @param inProviderName a <code>String</code> value
     * @param inCredentials a <code>MarketceteraFeedCredentials</code> value
     * @return a <code>MarketceteraFeed</code> value
     * @throws MarketceteraException 
     * @throws URISyntaxException 
     */
    public static MarketceteraFeed getInstance(String inProviderName,
                                               MarketceteraFeedCredentials inCredentials)
        throws URISyntaxException, MarketceteraException
    {
        if(sInstance != null) {
            return sInstance;
        }
        sInstance = new MarketceteraFeed(inProviderName,
                                         inCredentials);
        return sInstance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected void doCancel(String inHandle)
    {
        logger.debug(String.format("Marketcetera feed cancelling subscriptions for handle %s",
                                   inHandle));
        List<FIXCorrelationFieldSubscription> subscriptions = removeSubscriptions(inHandle);
        logger.debug(String.format("Marketcetera feed found %d subscription(s) for handle %s",
                                   subscriptions.size(),
                                   inHandle));
        for(FIXCorrelationFieldSubscription subscription : subscriptions) {
            Message message = messageFactory.create("", 
                                                    subscription.getSubscribeMsgType());
            StringField correlationID = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, 
                                                                           subscription.getSubscribeMsgType());
            correlationID.setValue(subscription.toString());
            logger.debug(String.format("Marketcetera feed sending cancel request for %s",
                                       correlationID));
            message.setField(correlationID);
            message.setField(new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
            message.setField(new NoRelatedSym(0));
            message.setField(new NoMDEntryTypes(0));
            if (subscription.getMarketDepth() != null) {
                message.setField(new MarketDepth(subscription.getMarketDepth()));
            }
            try {
                sendMessage(message);
            } catch (SessionNotFound e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doDerivativeSecurityListRequest(java.lang.Object)
     */
    @Override
    protected List<String> doDerivativeSecurityListRequest(Message inData) 
        throws FeedException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.IMarketDataFeedCredentials)
     */
    @Override
    protected boolean doLogin(MarketceteraFeedCredentials inCredentials)
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogout()
     */
    @Override
    protected void doLogout()
    {
    }
    private final Map<String,List<FIXCorrelationFieldSubscription>> mSubscriptionsBySymbol = new HashMap<String,List<FIXCorrelationFieldSubscription>>();    
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doMarketDataRequest(java.lang.Object)
     */
    @Override
    protected List<String> doMarketDataRequest(Message inData) 
        throws FeedException
    {
        try {
            logger.debug(String.format("MarketceteraFeed received market data request %s",
                                       inData));
            List<String> handles = new ArrayList<String>();
            List<Group> groups = AbstractMessageTranslator.getGroups(inData);
            for(Group group : groups) {
                MSymbol symbol = AbstractMessageTranslator.getSymbol(group);
                handles.add(symbol.getBaseSymbol());
            }
            addSubscription(doQuery(inData),
                            handles);
            logger.debug(String.format("MarketceteraFeed posted query and received handle(s) %s",
                                       Arrays.toString(handles.toArray())));
            return handles;
        } catch (Throwable t) {
            throw new FeedException(t);
        }
    }
    /**
     * Associates the given request handles with the given subscription object.
     * 
     * @param inSub a <code>FIXCorrelationFieldSubscription</code> value
     * @param inHandles a <code>List&lt;String&gt;</code> value
     */
    private void addSubscription(FIXCorrelationFieldSubscription inSub,
                                 List<String> inHandles)
    {
        synchronized(mSubscriptionsBySymbol) {
            for(String handle : inHandles) {
                List<FIXCorrelationFieldSubscription> subs = mSubscriptionsBySymbol.get(handle);
                if(subs == null) {
                    subs = new ArrayList<FIXCorrelationFieldSubscription>();
                    mSubscriptionsBySymbol.put(handle, 
                                               subs);
                }
                logger.debug(String.format("MarketceteraFeed associating subscription %s to symbol %s",
                                           inSub.getCorrelationFieldValue(),
                                           handle));
                subs.add(inSub);
            }
        }
    }
    /**
     * the default list returned representing no subscriptions
     */
    private static final List<FIXCorrelationFieldSubscription> EMPTY_SUBSCRIPTION_LIST = new ArrayList<FIXCorrelationFieldSubscription>();
    /**
     * Removes all subscriptions for the given handle.
     * 
     * @param inHandle a <code>String</code> value
     * @return a <code>List&lt;FIXCorrelationFieldSubscription&gt;</code> value
     */
    private List<FIXCorrelationFieldSubscription> removeSubscriptions(String inHandle)
    {
        List<FIXCorrelationFieldSubscription> listToReturn;
        synchronized(mSubscriptionsBySymbol) {
            List<FIXCorrelationFieldSubscription> subs = mSubscriptionsBySymbol.get(inHandle);
            if(subs == null) {
                return EMPTY_SUBSCRIPTION_LIST;
            } else {
                listToReturn = new ArrayList<FIXCorrelationFieldSubscription>(subs);
                subs.clear();
            }
            return listToReturn;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doSecurityListRequest(java.lang.Object)
     */
    @Override
    protected List<String> doSecurityListRequest(Message inData) 
        throws FeedException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(org.marketcetera.marketdata.MarketDataFeedTokenSpec)
     */
    @Override
    protected MarketceteraFeedToken generateToken(MarketDataFeedTokenSpec<MarketceteraFeedCredentials> inTokenSpec) 
        throws FeedException
    {
        return MarketceteraFeedToken.getToken(inTokenSpec, 
                                              this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    @Override
    protected MarketceteraFeedEventTranslator getEventTranslator()
    {
        return MarketceteraFeedEventTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    @Override
    protected MarketceteraFeedMessageTranslator getMessageTranslator()
    {
        return MarketceteraFeedMessageTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#isLoggedIn(org.marketcetera.marketdata.IMarketDataFeedCredentials)
     */
    @Override
    protected boolean isLoggedIn(MarketceteraFeedCredentials inCredentials)
    {
        return isRunning();
    }
}
