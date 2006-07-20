package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigData;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.AccessViolator;
import quickfix.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * FIX entrypoint - registers a FIX connection, listens for incoming
 * FIX requests
 * 
 * @author gmiller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class QuickFIXInitiator implements quickfix.Application
{
    private SocketInitiator mSocketInitiator;
    private Map<SessionID, QuickFIXSessionAdapter> mSessionMap =
                                new HashMap<SessionID, QuickFIXSessionAdapter>();
    private Set<SessionID> mLoggedOnSessions = new HashSet<SessionID>();
    protected MessageFactory mMessageFactory;
    private QuickFIXSessionAdapter sessionAdapter;
    private String mFixServerAddress;    // address of the target fix server

    protected String mCurFixVersion;
    protected SessionID mDefaultSessionID;

    public static final String QUICK_FIX_ADAPTER_NAME = "QuickFIXInitiator";

    public static final String FIX_VERSION_DEFAULT = FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING;

    private static final String SOCKET_CONNECT_HOST = "SocketConnectHost";
    private static final String SOCKET_CONNECT_PORT = "SocketConnectPort";

    public QuickFIXInitiator(QuickFIXSessionAdapter inSource)
    {
        sessionAdapter = inSource;
    }

    /** Initializes the adapter with data from config file */
    public void init(ConfigData config) throws Exception
    {
        mCurFixVersion = config.get(ConnectionConstants.FIX_VERSION_KEY, FIX_VERSION_DEFAULT);
        FIXDataDictionaryManager.setFIXVersion(mCurFixVersion);

        String senderCompID = config.get(ConnectionConstants.FIX_SENDER_COMP_ID, "");
        String targetCompID = config.get(ConnectionConstants.FIX_TARGET_COMP_ID, "");
        mFixServerAddress = config.get(ConnectionConstants.FIX_SERVER_ADDRESS, "");
        String dataDictionary = config.get(Session.SETTING_DATA_DICTIONARY, "");
        String useDataDictionary = config.get(Session.SETTING_USE_DATA_DICTIONARY, "N");
        long fixServerPort = config.getLong(ConnectionConstants.FIX_SERVER_PORT, 0);
        String resetOnLogout = config.get(Session.SETTING_RESET_ON_LOGOUT, "Y");
        String resetOnDisconnect = config.get(Session.SETTING_RESET_ON_DISCONNECT, "Y");
        String sendResetSeqNumFlag = config.get(Session.SETTING_RESET_WHEN_INITIATING_LOGON, "Y");

        QuickFIXDescriptor descriptor = sFIXVersionMap.get(mCurFixVersion);
        if (descriptor == null) {
            throw new ClassNotFoundException(
                    "Could not find class for fix version " + mCurFixVersion);
        }
        mMessageFactory = descriptor.getMessageFactory();

        // populate the default FIX session settings
        AccessViolator violator = new AccessViolator(SessionSettings.class);
        SessionID defaultSessionID = (SessionID) violator.getField("DEFAULT_SESSION_ID", null);
        SessionSettings settings = new SessionSettings();
        settings.setString(defaultSessionID, "ConnectionType","initiator");
        settings.setLong(defaultSessionID, "HeartBtInt",30);
        settings.setString(defaultSessionID, "FileStorePath","store");
        settings.setString(defaultSessionID, "StartTime","00:00:00");
        settings.setString(defaultSessionID, "EndTime","00:00:00");
        settings.setString(defaultSessionID, Session.SETTING_DATA_DICTIONARY, dataDictionary);
        settings.setString(defaultSessionID, "UseDataDictionary",useDataDictionary);
        settings.setLong(defaultSessionID, "ReconnectInterval",15);
        settings.setString(defaultSessionID, Session.SETTING_RESET_ON_LOGOUT, resetOnLogout);
        settings.setString(defaultSessionID, Session.SETTING_RESET_ON_DISCONNECT, resetOnDisconnect);
        settings.setString(defaultSessionID, Session.SETTING_RESET_WHEN_INITIATING_LOGON, sendResetSeqNumFlag);

        settings.setString(defaultSessionID, SOCKET_CONNECT_HOST,mFixServerAddress);
        settings.setLong(defaultSessionID, SOCKET_CONNECT_PORT,fixServerPort);

        String [] keys = config.keys();
        for (String key : keys) {
            if (key.startsWith(ConnectionConstants.FIX_SERVER_ADDRESS) && key.length() > ConnectionConstants.FIX_SERVER_ADDRESS.length() + 1)
            {
                String suffix = key.substring(ConnectionConstants.FIX_SERVER_ADDRESS.length() + 1);
                String newKey = SOCKET_CONNECT_HOST + suffix;
                settings.setString(defaultSessionID, newKey, config.get(key, ""));
            }
        }
        for (String key : keys) {
            if (key.startsWith(ConnectionConstants.FIX_SERVER_PORT) && key.length() > ConnectionConstants.FIX_SERVER_PORT.length())
            {
                String suffix = key.substring(ConnectionConstants.FIX_SERVER_PORT.length() + 1);
                String newKey = SOCKET_CONNECT_PORT + suffix;
                settings.setString(defaultSessionID, newKey, config.get(key, ""));
            }
        }

        SessionID id = new SessionID(mCurFixVersion, senderCompID, targetCompID, "");
        settings.setString(id, "BeginString", mCurFixVersion);
        settings.setString(id, "SenderCompID", senderCompID);
        settings.setString(id, "TargetCompID", targetCompID);
        settings.setString(id, "SessionQualifier", "");

        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(false, false, true);

        mSocketInitiator = new SocketInitiator(this, storeFactory, settings,
                                               logFactory, getMessageFactory());

        mDefaultSessionID = new SessionID(mCurFixVersion, senderCompID,
                                          targetCompID, null);

        mSessionMap.put(mDefaultSessionID, sessionAdapter);
        startSocketInitiator(mSocketInitiator);
    }

    /** to be overridden by tests */
    protected void startSocketInitiator(SocketInitiator inInitiator) throws ConfigError
    {
        inInitiator.start();
    }

    public void shutdown() {
        mSocketInitiator.stop();
    }

    public boolean isLoggedOn(quickfix.SessionID sessionID) {
        return mLoggedOnSessions.contains(sessionID);
    }

    public Session getSession(SessionID sessionID) {
        return Session.lookupSession(sessionID);
    }

    /** --- Implementation of the Quickfix Application callbacks
     * Map to the right internal Quickfix session and delegate the call through to that.
     */
    public void onLogout(quickfix.SessionID sessionID) {
        mLoggedOnSessions.remove(sessionID);
        QuickFIXSessionAdapter aSession = mSessionMap.get(sessionID);
        if (aSession != null) {
            aSession.onLogout();
        }
    }

    public void onLogon(quickfix.SessionID sessionID) {
        mLoggedOnSessions.add(sessionID);
        QuickFIXSessionAdapter aSession = mSessionMap.get(sessionID);
        if (aSession != null) {
            aSession.onLogon();
        }
    }

    public void onCreate(quickfix.SessionID sessionID) {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("new session created: "+sessionID, this);
        }

        QuickFIXSessionAdapter aSession = mSessionMap.get(sessionID);
        if (aSession == null) {
            mSessionMap.put(sessionID, sessionAdapter);
        } else {
            mSessionMap.put(sessionID, aSession);
        }
    }

    public void toApp(quickfix.Message message, quickfix.SessionID sessionID)
            throws DoNotSend {
        QuickFIXSessionAdapter aSession = mSessionMap.get(sessionID);
        if (aSession != null) {
            aSession.toApp(message);
        }
    }

    public void toAdmin(quickfix.Message message, quickfix.SessionID sessionID) {
        QuickFIXSessionAdapter aSession = mSessionMap.get(sessionID);
        if (aSession != null) {
            aSession.toAdmin(message);
        }
    }

    public void fromApp(quickfix.Message message, quickfix.SessionID sessionID)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
            UnsupportedMessageType {
        QuickFIXSessionAdapter aSession = mSessionMap.get(sessionID);
        if (aSession != null) {
            aSession.fromApp(message);
        }
    }

    public void fromAdmin(quickfix.Message message, quickfix.SessionID sessionID)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
            RejectLogon {
        QuickFIXSessionAdapter aSession = mSessionMap.get(sessionID);
        if (aSession != null) {
            aSession.fromAdmin(message);
        }
    }

    public MessageFactory getMessageFactory() {
        return mMessageFactory;
    }

    /** Returns the default session */
    public SessionID getDefaultSessionID() {
        return mDefaultSessionID;
    }

    public String getFixServerAddress() {
        return mFixServerAddress;
    }

    protected static Map<String, QuickFIXDescriptor> sFIXVersionMap;
    static {

        sFIXVersionMap = new HashMap<String, QuickFIXDescriptor>();
        // sFIXVersionMap.put(FIX_4_0_BEGIN_STRING, new
        // QuickFIXDescriptor("fix40", FIX_4_0_BEGIN_STRING));
        sFIXVersionMap.put(FIXDataDictionaryManager.FIX_4_1_BEGIN_STRING, new QuickFIXDescriptor(
                "fix41", "41", FIXDataDictionaryManager.FIX_4_1_BEGIN_STRING));
        sFIXVersionMap.put(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING, new QuickFIXDescriptor(
                "fix42", "42", FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING));
        // sFIXVersionMap.put(FIX_4_3_BEGIN_STRING, new
        // QuickFIXDescriptor("fix43", FIX_4_3_BEGIN_STRING));
        // sFIXVersionMap.put(FIX_4_4_BEGIN_STRING, new
        // QuickFIXDescriptor("fix44", FIX_4_4_BEGIN_STRING));

    }

    public String getName() {
        return QUICK_FIX_ADAPTER_NAME;
    }

}

