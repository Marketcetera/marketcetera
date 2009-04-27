package org.marketcetera.marketdata.marketcetera;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FIXCorrelationFieldSubscription;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.quickfix.EventLogFactory;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
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
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.TestMessageIndicator;
import quickfix.fix44.MessageFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/* $License$ */

/**
 * A sample implementation of a market data feed.
 *
 * <p>This feed will return random market data for every symbol queried.
 *
 * @author <a href="mailto:colin@marketcetera.com>Colin DuPlantis</a>
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class MarketceteraFeed 
    extends AbstractMarketDataFeed<MarketceteraFeedToken,
                                   MarketceteraFeedCredentials,
                                   MarketceteraFeedMessageTranslator,
                                   MarketceteraFeedEventTranslator,
                                   MarketceteraFeed.Request,
                                   MarketceteraFeed> 
    implements Application, Messages 
{
	private SessionID sessionID;
	private final IDFactory idFactory;
	private FeedType feedType;
	private boolean isRunning = false;
	private SocketInitiator socketInitiator;
	private MessageFactory messageFactory;
	private final Map<String, Exchanger<Message>> pendingRequests = new WeakHashMap<String, Exchanger<Message>>();
    private MarketceteraFeedCredentials credentials;
    /**
     * static capabilities for this data feed
     */
    private static final Set<Capability> capabilities = Collections.unmodifiableSet(EnumSet.of(Capability.TOP_OF_BOOK,Capability.LATEST_TICK));
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return capabilities;
    }
	private FIXCorrelationFieldSubscription doQuery(Message query) {
		try {
			Integer marketDepth = null;
			try {
			    marketDepth = query.getInt(MarketDepth.FIELD); 
			} catch (FieldNotFound fnf) {
			    // do nothing, this is OK, not every query has to have a depth
			}
			String reqID = addReqID(query);
			sendMessage(query);
			return new FIXCorrelationFieldSubscription(reqID, 
			                                           query.getHeader().getString(MsgType.FIELD), 
			                                           marketDepth);
		} catch (SessionNotFound e) {
		    SESSION_NOT_FOUND.error(this,
		                            e);
		} catch (FieldNotFound e) {
		    CANNOT_EXECUTE_QUERY.error(this,
		                               e,
		                               query);
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
	    } catch (FieldNotFound e) {
	        CANNOT_FIND_REQID.error(this,
	                                e,
	                                inMessage);
	    }
	    return reqID;
	}
	
	private String addReqID(Message query) throws FieldNotFound {
		String reqID = getReqID(query);

		String msgType = query.getHeader().getString(MsgType.FIELD);
		StringField reqIDField = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, msgType);
		try {
			query.getField(reqIDField).toString();
		} catch (FieldNotFound e1) {
            CANNOT_FIND_REQID.error(this,
                                    e1,
                                    query);
		}
		if (reqIDField.getValue() == null || reqIDField.getValue().length()==0){
			try {
				reqID = idFactory.getNext();
			} catch (NoMoreIDsException e) {
				// should never happen
			    CANNOT_ACQUIRE_ID.error(this,
			                            e);
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
	/**
	 * Creates an active connection to the Marketcetera Exchange server.
	 *
	 * <p>Attempts to connect to the server with the most recent set of credentials available.  If there
	 * is already an active connection, this method does nothing.  This method will block for 30 seconds
	 * while waiting for confirmation from the server.  If at the end of 30 seconds the server has not
	 * responded, this method throws a <code>FeedException</code>.  This method updates the feed status
	 * based on the results of the connection attempt.
	 * 
	 * @throws FeedException if a connection cannot be made to the server 
	 */
	private void connectToServer()
	    throws Exception
	{
        SLF4JLoggerProxy.debug(this,
                               "Checking connection to Marketcetera Feed"); //$NON-NLS-1$
	    if(isRunning()) {
	        SLF4JLoggerProxy.debug(this,
                                   "Already connected to Marketcetera Feed"); //$NON-NLS-1$
	        return;
	    }
        if(credentials == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No credentials to work with, cancelling connection request - try again later"); //$NON-NLS-1$
        }
        SLF4JLoggerProxy.debug(this,
                               "Not connected yet, connecting with credentials [{}]...", //$NON-NLS-1$
                               credentials);
        String url = credentials.getURL();
        URI feedURI = new URI(url);
        int serverPort = feedURI.getPort();
        if ((serverPort) < 0){
            URI_MISSING_PORT.error(AbstractMarketDataFeed.DATAFEED_STATUS_MESSAGES);
            throw new FeedException(URI_MISSING_PORT);
        }
        String server = feedURI.getHost();
        String senderCompID = credentials.getSenderCompID();
        if (senderCompID == null || 
            senderCompID.trim().isEmpty()) {
            senderCompID = idFactory.getNext();
        }
        String targetCompID = credentials.getTargetCompID();
        String scheme;
        if (!FIXDataDictionary.FIX_4_4_BEGIN_STRING.equals(scheme = feedURI.getScheme()) ) {
            UNSUPPORTED_FIX_VERSION.error(AbstractMarketDataFeed.DATAFEED_STATUS_MESSAGES);
            throw new CoreException(UNSUPPORTED_FIX_VERSION);
        } else {
            sessionID = new SessionID(scheme, 
                                      senderCompID, 
                                      targetCompID);
        }
        synchronized(this) {
            try {
                setFeedStatus(FeedStatus.OFFLINE);
                CONNECTION_STARTED.info(this, 
                                        url);
                MessageStoreFactory messageStoreFactory = new MemoryStoreFactory();
                SessionSettings sessionSettings;
                sessionSettings = new SessionSettings(MarketceteraFeed.class.getClassLoader().getResourceAsStream("fixdatafeed.properties")); //$NON-NLS-1$
                sessionSettings.setString(sessionID, Initiator.SETTING_SOCKET_CONNECT_HOST, server);
                sessionSettings.setLong(sessionID, Initiator.SETTING_SOCKET_CONNECT_PORT, serverPort);

                File workspaceDir = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
                File quoteFeedLogDir = new File(workspaceDir,
                                                "marketdata"); //$NON-NLS-1$
                if (!quoteFeedLogDir.exists())
                {
                    quoteFeedLogDir.mkdir();
                }
                sessionSettings.setString(sessionID, FileLogFactory.SETTING_FILE_LOG_PATH, quoteFeedLogDir.getCanonicalPath());

                LogFactory logFactory = new EventLogFactory(sessionSettings);
                messageFactory = new MessageFactory();
                socketInitiator = new SocketInitiator(this, messageStoreFactory, sessionSettings, logFactory, messageFactory);
                socketInitiator.start();
                SLF4JLoggerProxy.debug(this,
                                       "Connected, waiting for confirmation"); //$NON-NLS-1$
                wait(1000*30);
                if(!getFeedStatus().equals(FeedStatus.AVAILABLE)) {
                    throw new FeedException(CANNOT_START_FEED);
                }
                setIsRunning(true);
                SLF4JLoggerProxy.debug(this,
                                       "Connection confirmed, ready to proceed"); //$NON-NLS-1$
            } catch (Exception e) {
                SLF4JLoggerProxy.debug(this,
                                       "Connection attempt failed!"); //$NON-NLS-1$
                CANNOT_START_FEED.error(AbstractMarketDataFeed.DATAFEED_STATUS_MESSAGES,
                                        e);
                setFeedStatus(FeedStatus.ERROR);
                throw e;
            }
        }
	}
	public void stop() 
	{
		synchronized(this) {
			if (isRunning()) {
			    CONNECTION_STOPPED.info(this,
			                            credentials.getURL());
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
			SLF4JLoggerProxy.debug(this,
			                       "Marketcetera feed received Logon"); //$NON-NLS-1$
		} else if (MsgType.LOGOUT.equals(msgType)) {
            SLF4JLoggerProxy.debug(this,
                                   "Marketcetera feed received Logout"); //$NON-NLS-1$
		} else if (!MsgType.HEARTBEAT.equals(msgType)) {
            SLF4JLoggerProxy.debug(this,
                                   "Admin message for Marketcetera feed: {}", //$NON-NLS-1$
                                   message);
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
		} catch (FieldNotFound fnf) {
		    // not every message needs to have reqID, this is OK
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
						    EXCHANGE_ERROR.error(this,
						                         e);
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

	public void onCreate(SessionID sessionID) 
	{
        SLF4JLoggerProxy.debug(this,
                               "Marketcetera feed session created {}", //$NON-NLS-1$
                               sessionID);
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
        Set<String> handles = getHandlesForSymbol(symbol);
        SLF4JLoggerProxy.debug(this,
                               "MarketceteraFeed received response for handle(s): {}", //$NON-NLS-1$
                               handles);
        for(String handle : handles) {
            dataReceived(handle,
                         refresh);
        }
	}
	private MarketceteraFeed(String inProviderName) 
	    throws URISyntaxException, CoreException
	{
	    super(FeedType.UNKNOWN,
	          inProviderName);
        try {
            idFactory = new InMemoryIDFactory(System.currentTimeMillis(),
                                              String.format("-%s-", //$NON-NLS-1$
                                                            InetAddress.getLocalHost().toString()));
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
	}
	/**
	 * used in a message that does not contain a symbol
	 */
    private static final String UNKNOWN_SYMBOL = "unknown"; //$NON-NLS-1$
	/**
	 * singleton instance of the marketcetera feed
	 */
	private static MarketceteraFeed sInstance;
    /**
     * Gets an instance of <code>MarketceteraFeed</code>.
     * 
     * @param inProviderName a <code>String</code> value
     * @return a <code>MarketceteraFeed</code> value
     * @throws CoreException 
     * @throws URISyntaxException 
     */
    public static MarketceteraFeed getInstance(String inProviderName)
        throws URISyntaxException, CoreException
    {
        if(sInstance != null) {
            return sInstance;
        }
        sInstance = new MarketceteraFeed(inProviderName);
        return sInstance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected void doCancel(String inHandle)
    {
        SLF4JLoggerProxy.debug(this,
                               "Marketcetera feed canceling subscriptions for handle {}", //$NON-NLS-1$
                               inHandle);
        Request request = removeRequest(inHandle);
        FIXCorrelationFieldSubscription subscription = request.getSubscription();
        Message message = messageFactory.create("",  //$NON-NLS-1$
                                                subscription.getSubscribeMsgType());
        StringField correlationID = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, 
                                                                       subscription.getSubscribeMsgType());
        correlationID.setValue(subscription.toString());
        SLF4JLoggerProxy.debug(this,
                               "Marketcetera feed sending cancel request for {}", //$NON-NLS-1$
                               correlationID);
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
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.IMarketDataFeedCredentials)
     */
    @Override
    protected boolean doLogin(MarketceteraFeedCredentials inCredentials)
    {
        credentials = inCredentials;
        try {
            connectToServer();
        } catch (Exception e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            return false;
        }
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogout()
     */
    @Override
    protected void doLogout()
    {
        stop();
    }
    /**
     * Associates the given request handles with the given request object.
     * 
     * @param inRequest a <code>Request</code> value
     */
    private synchronized static void addRequest(Request inRequest)
    {
        requestsByHandle.put(inRequest.getIdAsString(),
                             inRequest);
        for(String symbol : inRequest.getRequest().getSymbols()) {
            handlesBySymbol.put(symbol,
                                inRequest.getIdAsString());
        }
    }
    /**
     * Returns the handles associated with the given symbol, if any.
     *
     * @param inSymbol a <code>String</code> value
     * @return a <code>Set&lt;String&gt;</code> value
     */
    private synchronized static Set<String> getHandlesForSymbol(String inSymbol)
    {
        Set<String> handles = handlesBySymbol.get(inSymbol);
        if(handles != null) {
            return handles;
        }
        return Collections.emptySet();
    }
    /**
     * Returns the <code>Request</code> associated with the given handle.
     *
     * @param inHandle a <code>String</code> value
     * @return a <code>Request</code> value or null
     */
    synchronized static Request getRequestByHandle(String inHandle)
    {
        return requestsByHandle.get(inHandle);
    }
    /**
     * Removes all subscriptions for the given handle.
     * 
     * @param inHandle a <code>String</code> value
     * @return a <code>Request</code> value
     */
    private synchronized Request removeRequest(String inHandle)
    {
        Request request = requestsByHandle.remove(inHandle);
        for(String symbol : request.getRequest().getSymbols()) {
            Set<String> handles = handlesBySymbol.get(symbol);
            handles.remove(inHandle);
        }
        return request;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(org.marketcetera.marketdata.MarketDataFeedTokenSpec)
     */
    @Override
    protected MarketceteraFeedToken generateToken(MarketDataFeedTokenSpec inTokenSpec) 
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
    protected boolean isLoggedIn()
    {
        return isRunning();
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doMarketDataRequest(java.lang.Object)
     */
    @Override
    protected List<String> doMarketDataRequest(Request inData)
            throws FeedException
    {
        try {
            inData.setSubscription(doQuery(inData.getMessage()));
            addRequest(inData);
            SLF4JLoggerProxy.debug(this,
                                   "MarketceteraFeed posted query for {} and associated the request with handle {}", //$NON-NLS-1$
                                   inData.getRequest().getSymbols(),
                                   inData.getIdAsString());
            return Arrays.asList(new String[] { inData.getIdAsString() } );
        } catch (Exception e) {
            throw new FeedException(e);
        }
    }
    /**
     * active requests by handle
     */
    private static final Map<String,Request> requestsByHandle = new HashMap<String,Request>();
    /**
     * handles by associated symbol
     */
    private static final SetMultimap<String,String> handlesBySymbol = HashMultimap.create();
    /**
     * Represents a request made to the marketcetera adapter.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ClassVersion("$Id$")
    static final class Request
    {
        /**
         * the FIX message actually sent to the marketcetera feed
         */
        private final Message message;
        /**
         * the underlying request submitted to the adapter
         */
        private final MarketDataRequest request;
        /**
         * the unique identifier for this request
         */
        private final long id;
        /**
         * the subscription token returned from the submit call
         */
        private FIXCorrelationFieldSubscription subscription;
        /**
         * Create a new Request instance.
         *
         * @param inId a <code>long</code> value
         * @param inMessage a <code>Message</code> value
         * @param inRequest a <code>MarketDataRequest</code> value
         */
        Request(long inId,
                Message inMessage,
                MarketDataRequest inRequest)
        {
            id = inId;
            message = inMessage;
            request = inRequest;
        }
        /**
         * Get the id value.
         *
         * @return a <code>long</code> value
         */
        long getId()
        {
            return id;
        }
        /**
         * Gets the id as a <code>String</code>.
         *
         * @return a <code>String</code> value
         */
        String getIdAsString()
        {
            return Long.toHexString(getId());
        }
        /**
         * Get the messages value.
         *
         * @return a <code>Message</code> value
         */
        Message getMessage()
        {
            return message;
        }
        /**
         * Get the request value.
         *
         * @return a <code>MarketDataRequest</code> value
         */
        MarketDataRequest getRequest()
        {
            return request;
        }
        /**
         * Get the subscription value.
         *
         * @return a <code>FIXCorrelationFieldSubscription</code> value or null
         */
        FIXCorrelationFieldSubscription getSubscription()
        {
            return subscription;
        }
        /**
         * Sets the subscription value.
         *
         * @param a <code>FIXCorrelationFieldSubscription</code> value
         */
        private void setSubscription(FIXCorrelationFieldSubscription inSubscription)
        {
            subscription = inSubscription;
        }
    }
}
