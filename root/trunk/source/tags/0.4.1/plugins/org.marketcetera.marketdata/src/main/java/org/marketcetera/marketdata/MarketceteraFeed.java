package org.marketcetera.marketdata;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.quickfix.EventLogFactory;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

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
import quickfix.field.SenderCompID;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.TargetCompID;
import quickfix.field.TestMessageIndicator;
import quickfix.field.Text;
import quickfix.fix44.MessageFactory;

public class MarketceteraFeed extends MarketDataFeedBase implements Application {

	public static final String SETTING_SENDER_COMP_ID = SenderCompID.class.getSimpleName();
	public static final String SETTING_TARGET_COMP_ID = TargetCompID.class.getSimpleName();
	private int serverPort;
	private String server;
	private SessionID sessionID;
	private IDFactory idFactory;
	private FeedType feedType;
	private boolean isRunning = false;
	private SocketInitiator socketInitiator;
	private MessageFactory messageFactory;
	private Map<String, Exchanger<Message>> pendingRequests = new WeakHashMap<String, Exchanger<Message>>();
	private final Logger logger;
	private String url;

	public MarketceteraFeed(String url, String userName, String password, Map<String, Object> properties, Logger logger) throws MarketceteraException {
		this.logger = logger;
		this.url = url;
		try {
			idFactory = new InMemoryIDFactory(System.currentTimeMillis(),"-"+ InetAddress.getLocalHost().toString());
			URI feedURI = new URI(url);
			if ((serverPort = feedURI.getPort()) < 0){
				throw new MarketceteraException("Port must be defined on feed URL");
			}
			server = feedURI.getHost();
			String senderCompID;
			String targetCompID;
			if (!properties.containsKey(SETTING_SENDER_COMP_ID)
					|| properties.get(SETTING_SENDER_COMP_ID).toString().length()==0)
			{
				senderCompID = idFactory.getNext();
			} else {
				senderCompID = properties.get(SETTING_SENDER_COMP_ID).toString();
			}
			if (!properties.containsKey(SETTING_TARGET_COMP_ID))
			{
				throw new MarketceteraException("Must set setting "+SETTING_TARGET_COMP_ID);
			} else {
				targetCompID = properties.get(SETTING_TARGET_COMP_ID).toString();
			}
			String scheme;
			if (!FIXDataDictionary.FIX_4_4_BEGIN_STRING.equals(scheme = feedURI.getScheme()) ){
				throw new MarketceteraException("Only FIX.4.4 is supported");
			} else {
				sessionID = new SessionID(scheme, senderCompID, targetCompID);
			}
		} catch (Exception e) {
			throw new MarketceteraException(e);
		}
	}

	public ISubscription asyncQuery(Message query) {
		try {
			Integer marketDepth = null;
			try { marketDepth = query.getInt(MarketDepth.FIELD); } catch (FieldNotFound fnf) { /* do nothing */ }
			String reqID = addReqID(query);
			sendMessage(query);
			return new MarketceteraSubscription(reqID, query.getHeader().getString(MsgType.FIELD), marketDepth);
		} catch (SessionNotFound e) {
			logger.error("Session not found while trying to execute async query: "+query);
		} catch (FieldNotFound e) {
			logger.error(e.toString()+" "+query);
		}
		return null;
	}

	private String addReqID(Message query) throws FieldNotFound {
		String reqID = null;

		String msgType = query.getHeader().getString(MsgType.FIELD);
		StringField reqIDField = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, msgType);
		try {
			query.getField(reqIDField);
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

	public void asyncUnsubscribe(ISubscription subscription) throws MarketceteraException {
		if (subscription instanceof MarketceteraSubscription) {
			MarketceteraSubscription mSubscription = (MarketceteraSubscription) subscription;
			Message message = messageFactory.create("", mSubscription.getSubscribeMsgType());
			StringField correlationID = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, mSubscription.getSubscribeMsgType());
			correlationID.setValue(mSubscription.toString());

			message.setField(correlationID);
			message.setField(new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
			message.setField(new NoRelatedSym(0));
			message.setField(new NoMDEntryTypes(0));
			if (mSubscription.getMarketDepth() != null){
				message.setField(new MarketDepth(mSubscription.getMarketDepth()));
			}
			try {
                sendMessage(message);
            } catch (SessionNotFound e) {
				throw new MarketceteraException(e);
			}
		} else {
			// Ignore
		}
	}

    /** Delegating to a method so that subclasses can override in tests */
    protected void sendMessage(Message message) throws SessionNotFound {
        Session.sendToTarget(message, sessionID);
    }

    public MSymbol symbolFromString(String symbolString) {
		if (MarketceteraOptionSymbol.matchesPattern(symbolString)){
			return new MarketceteraOptionSymbol(symbolString);
		}
		return new MSymbol(symbolString);
	}

	public List<Message> syncQuery(Message query, long timeout, TimeUnit units) throws TimeoutException, MarketceteraException {
		String reqID;
		try {
			reqID = addReqID(query);
		} catch (FieldNotFound fnf) {
			throw new MarketceteraException(fnf);
		}
		Exchanger<Message> exchanger = new Exchanger<Message>();
		synchronized (pendingRequests) {
			pendingRequests.put(reqID, exchanger);
		}
		asyncQuery(query);
		LinkedList<Message> linkedList = new LinkedList<Message>();
		try {
			linkedList.add(exchanger.exchange(null, timeout, units));
		} catch (InterruptedException e) {
			return null;
		}
		return linkedList;
	}

	public FeedStatus getFeedStatus() {
		return feedStatus;
	}

	public FeedType getFeedType() {
		return feedType;
	}

	public String getID() {
		return "Marketcetera";
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void start() {

		synchronized (this){
			try {
				if (!isRunning){
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
	
	//				LogFactory logFactory = new FileLogFactory(sessionSettings);
					LogFactory logFactory = new EventLogFactory(sessionSettings);
					messageFactory = new MessageFactory();
					socketInitiator = new SocketInitiator(this, messageStoreFactory, sessionSettings, logFactory, messageFactory);
					socketInitiator.start();
					isRunning = true;
				}
			} catch (Throwable t) {
				logger.error("Exception trying to start Marketcetera feed: "+t.getLocalizedMessage());
				setFeedStatus(FeedStatus.ERROR);
			}
		}
	}

	public void stop() {
		synchronized (this) {
			if (isRunning){
				logger.info("Stopping connection to: "+url);
				socketInitiator.stop(true);
			}
		}
	}


	// quickfix.Application methods
	public void fromAdmin(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			RejectLogon {
		Header header = message.getHeader();
		String msgType = header.getString(MsgType.FIELD);
		if (MsgType.LOGON.equals(msgType)) {
			try {
				boolean testMessageIndicator = header
						.getBoolean(TestMessageIndicator.FIELD);
				feedType = testMessageIndicator ? FeedType.SIMULATED
						: FeedType.LIVE;
			} catch (FieldNotFound fnf) {
				feedType = FeedType.LIVE;
			}
			logger.info("Marketcetera feed received Logon");
		} else if (MsgType.LOGOUT.equals(msgType)){
			String text = "";
			try {
				text = ": "+message.getString(Text.FIELD);
			} catch (FieldNotFound fnf){ /* do nothing */ }
			logger.info("Marketcetera feed received Logout"+text);
		} else if (!MsgType.HEARTBEAT.equals(msgType)){
			logger.info("Admin message for Marketcetera feed: "+message);
		}
	}

	public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {

		String reqID = null;
		boolean handled = false;
		try {
			StringField correlationField = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, message.getHeader().getString(MsgType.FIELD));
			reqID = message.getString(correlationField.getTag());
		} catch (FieldNotFound fnf){
		}
		if (reqID != null && reqID.length() > 0) {
			synchronized (pendingRequests) {
				for (String requestID : pendingRequests.keySet()) {
					if (requestID.equals(reqID)){
						try {
							// the other side should wait on this before we can call exchange
							pendingRequests.get(requestID).exchange(message, 1, TimeUnit.NANOSECONDS);
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


}
