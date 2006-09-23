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
        String jdbcDriver = config.get(JdbcSetting.SETTING_JDBC_DRIVER, null);
        String jdbcURL = config.get(JdbcSetting.SETTING_JDBC_CONNECTION_URL, null);
        String jdbcUser = config.get(JdbcSetting.SETTING_JDBC_USER, null);
        String jdbcPassword = config.get(JdbcSetting.SETTING_JDBC_PASSWORD, null);

        boolean useJDBC = (jdbcDriver != null &&
                jdbcURL != null &&
                jdbcUser!= null &&
                jdbcPassword != null
                );

        QuickFIXDescriptor descriptor = sFIXVersionMap.get(mCurFixVersion);
        if (descriptor == null) {
            throw new ClassNotFoundException(
                    "Could not find class for fix version " + mCurFixVersion);
        }
        mMessageFactory = descriptor.getMessageFactory();

        // populate the default FIX session settings
        Map<String, Object> defaults = new HashMap<String, Object>();
        defaults.put("ConnectionType","initiator");
        defaults.put("HeartBtInt",Long.toString(30));
        defaults.put("FileStorePath","store");
        defaults.put("StartTime","00:00:00");
        defaults.put("EndTime","00:00:00");
        defaults.put(Session.SETTING_DATA_DICTIONARY, dataDictionary);
        defaults.put("UseDataDictionary",useDataDictionary);
        defaults.put("ReconnectInterval",Long.toString(15));
        defaults.put(Session.SETTING_RESET_ON_LOGOUT, resetOnLogout);
        defaults.put(Session.SETTING_RESET_ON_DISCONNECT, resetOnDisconnect);
        defaults.put(Session.SETTING_RESET_WHEN_INITIATING_LOGON, sendResetSeqNumFlag);

        defaults.put(JdbcSetting.SETTING_JDBC_DRIVER,config.get(JdbcSetting.SETTING_JDBC_DRIVER, ""));
        defaults.put(JdbcSetting.SETTING_JDBC_CONNECTION_URL,config.get(JdbcSetting.SETTING_JDBC_CONNECTION_URL, ""));
        defaults.put(JdbcSetting.SETTING_JDBC_USER,config.get(JdbcSetting.SETTING_JDBC_USER, ""));
        defaults.put(JdbcSetting.SETTING_JDBC_PASSWORD,config.get(JdbcSetting.SETTING_JDBC_PASSWORD, ""));

        defaults.put(SOCKET_CONNECT_HOST,mFixServerAddress);
        defaults.put(SOCKET_CONNECT_PORT,Long.toString(fixServerPort));


        String [] keys = config.keys();
        for (String key : keys) {
            if (key.startsWith(ConnectionConstants.FIX_SERVER_ADDRESS) && key.length() > ConnectionConstants.FIX_SERVER_ADDRESS.length() + 1)
            {
                String suffix = key.substring(ConnectionConstants.FIX_SERVER_ADDRESS.length() + 1);
                String newKey = SOCKET_CONNECT_HOST + suffix;
                defaults.put(newKey, config.get(key, ""));
            }
        }
        for (String key : keys) {
            if (key.startsWith(ConnectionConstants.FIX_SERVER_PORT) && key.length() > ConnectionConstants.FIX_SERVER_PORT.length())
            {
                String suffix = key.substring(ConnectionConstants.FIX_SERVER_PORT.length() + 1);
                String newKey = SOCKET_CONNECT_PORT + suffix;
                defaults.put(newKey, config.get(key, ""));
            }
        }

        SessionSettings settings = new SessionSettings();
        // this poorly named set method sets the defaults for the session settings
        settings.set(defaults);

        SessionID id = new SessionID(mCurFixVersion, senderCompID, targetCompID, "");
        settings.setString(id, "BeginString", mCurFixVersion);
        settings.setString(id, "SenderCompID", senderCompID);
        settings.setString(id, "TargetCompID", targetCompID);
        settings.setString(id, "SessionQualifier", "");

        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = null;
        if (useJDBC)
        {
            logFactory = new CompositeLogFactory(new LogFactory[] {new JdbcLogFactory(settings),
                    new ScreenLogFactory(settings)});
        } else {
            logFactory = new ScreenLogFactory(settings);
        }

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

