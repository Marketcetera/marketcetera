package org.marketcetera.marketdata;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.quickfix.EventLogFactory;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
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
import quickfix.field.MDReqID;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.SecurityReqID;
import quickfix.field.SenderCompID;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.TargetCompID;
import quickfix.field.TestMessageIndicator;
import quickfix.fix44.MessageFactory;

public class MarketceteraFeed extends MarketDataFeedBase implements Application {

	/*package*/ static final String SETTING_SENDER_COMP_ID = SenderCompID.class.getSimpleName();
	/*package*/ static final String SETTING_TARGET_COMP_ID = TargetCompID.class.getSimpleName();
	private int serverPort;
	private SessionID sessionID;
	private IDFactory idFactory;
	private FeedType feedType;
	private boolean isRunning = false;
	private SocketInitiator socketInitiator;
	private MessageFactory messageFactory;
	private Map<String, Exchanger<Message>> pendingRequests = new WeakHashMap<String, Exchanger<Message>>();
	
	public MarketceteraFeed(String url, String userName, String password, Map<String, Object> properties) throws MarketceteraException {
		try {
			idFactory = new InMemoryIDFactory(System.currentTimeMillis(),"-"+ InetAddress.getLocalHost().toString());
			URI feedURI = new URI(url);
			if ((serverPort = feedURI.getPort()) < 0){
				throw new MarketceteraException("Port must be defined on feed URL");
			}
			String senderCompID;
			String targetCompID;
			if (!properties.containsKey(SETTING_SENDER_COMP_ID))
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
			String reqID = addReqID(query);
			Session.sendToTarget(query, sessionID);
			return new MarketceteraSubscription(reqID, query.getHeader().getString(MsgType.FIELD));
		} catch (SessionNotFound e) {
		} catch (FieldNotFound e) {
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
			try {
				Session.sendToTarget(message, sessionID);
			} catch (SessionNotFound e) {
				throw new MarketceteraException(e);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public MSymbol symbolFromString(String symbolString) {
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
		return null;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void start() {
		
		synchronized (this){
		try {
			if (!isRunning){
				MessageStoreFactory messageStoreFactory = new MemoryStoreFactory();
				SessionSettings sessionSettings;
				sessionSettings = new SessionSettings(getClass().getClassLoader().getResourceAsStream("/fixdatafeed.properties"));
				File workspaceDir = Activator.getDefault().getStoreDirectory();
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
			t.printStackTrace();
			setFeedStatus(FeedStatus.ERROR);
		}
		}		
	}

	public void stop() {
		synchronized (this) {
			if (isRunning){
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
