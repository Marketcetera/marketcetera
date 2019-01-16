package org.marketcetera.marketdata.exsim;

import javax.annotation.PostConstruct;

import org.marketcetera.core.CoreException;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Initiator;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides configuration information for the {@link ExsimFeedModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ExsimFeedConfig
{
    /**
     * Get the senderCompId value.
     *
     * @return a <code>String</code> value
     */
    public String getSenderCompId()
    {
        return senderCompId;
    }
    /**
     * Sets the senderCompId value.
     *
     * @param inSenderCompId a <code>String</code> value
     */
    public void setSenderCompId(String inSenderCompId)
    {
        senderCompId = inSenderCompId;
    }
    /**
     * Get the targetCompId value.
     *
     * @return a <code>String</code> value
     */
    public String getTargetCompId()
    {
        return targetCompId;
    }
    /**
     * Sets the targetCompId value.
     *
     * @param inTargetCompId a <code>String</code> value
     */
    public void setTargetCompId(String inTargetCompId)
    {
        targetCompId = inTargetCompId;
    }
    /**
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    public String getHostname()
    {
        return hostname;
    }
    /**
     * Sets the hostname value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * Get the fixVersion value.
     *
     * @return a <code>String</code> value
     */
    public String getFixVersion()
    {
        return fixVersion;
    }
    /**
     * Sets the fixVersion value.
     *
     * @param inFixVersion a <code>String</code> value
     */
    public void setFixVersion(String inFixVersion)
    {
        fixVersion = inFixVersion;
    }
    /**
     * Get the fixAplVersion value.
     *
     * @return a <code>String</code> value
     */
    public String getFixAplVersion()
    {
        return fixAplVersion;
    }
    /**
     * Sets the fixAplVersion value.
     *
     * @param inFixAplVersion a <code>String</code> value
     */
    public void setFixAplVersion(String inFixAplVersion)
    {
        fixAplVersion = inFixAplVersion;
    }
    /**
     * Get the appDataDictionary value.
     *
     * @return a <code>String</code> value
     */
    public String getAppDataDictionary()
    {
        return appDataDictionary;
    }
    /**
     * Sets the appDataDictionary value.
     *
     * @param inAppDataDictionary a <code>String</code> value
     */
    public void setAppDataDictionary(String inAppDataDictionary)
    {
        appDataDictionary = inAppDataDictionary;
    }
    /**
     * Get the reconnectInterval value.
     *
     * @return an <code>int</code> value
     */
    public int getReconnectInterval()
    {
        return reconnectInterval;
    }
    /**
     * Sets the reconnectInterval value.
     *
     * @param inReconnectInterval an <code>int</code> value
     */
    public void setReconnectInterval(int inReconnectInterval)
    {
        reconnectInterval = inReconnectInterval;
    }
    /**
     * Get the heartBtInt value.
     *
     * @return an <code>int</code> value
     */
    public int getHeartBtInt()
    {
        return heartBtInt;
    }
    /**
     * Sets the heartBtInt value.
     *
     * @param inHeartBtInt an <code>int</code> value
     */
    public void setHeartBtInt(int inHeartBtInt)
    {
        heartBtInt = inHeartBtInt;
    }
    /**
     * Get the startTime value.
     *
     * @return a <code>String</code> value
     */
    public String getStartTime()
    {
        return startTime;
    }
    /**
     * Sets the startTime value.
     *
     * @param inStartTime a <code>String</code> value
     */
    public void setStartTime(String inStartTime)
    {
        startTime = inStartTime;
    }
    /**
     * Get the endTime value.
     *
     * @return a <code>String</code> value
     */
    public String getEndTime()
    {
        return endTime;
    }
    /**
     * Sets the endTime value.
     *
     * @param inEndTime a <code>String</code> value
     */
    public void setEndTime(String inEndTime)
    {
        endTime = inEndTime;
    }
    /**
     * Get the timeZone value.
     *
     * @return a <code>String</code> value
     */
    public String getTimeZone()
    {
        return timeZone;
    }
    /**
     * Sets the timeZone value.
     *
     * @param inTimeZone a <code>String</code> value
     */
    public void setTimeZone(String inTimeZone)
    {
        timeZone = inTimeZone;
    }
    /**
     * Get the dataDictionary value.
     *
     * @return a <code>String</code> value
     */
    public String getDataDictionary()
    {
        return dataDictionary;
    }
    /**
     * Sets the dataDictionary value.
     *
     * @param inDataDictionary a <code>String</code> value
     */
    public void setDataDictionary(String inDataDictionary)
    {
        dataDictionary = inDataDictionary;
    }
    /**
     * Get the sessionId value.
     *
     * @return a <code>SessionID</code> value
     */
    public SessionID getSessionId()
    {
        return sessionId;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        if(senderCompId == null) {
            throw new CoreException(Messages.SENDER_COMPID_REQURED);
        }
        sessionId = new SessionID(fixVersion,
                                  senderCompId,
                                  targetCompId);
    }
    /**
     * Populates the given session settings value with the settings established for this config.
     *
     * @param inSessionSettings a <code>SessionSettings</code> value
     */
    void populateSessionSettings(SessionSettings inSessionSettings)
    {
        inSessionSettings.setString(sessionId,
                                    Initiator.SETTING_SOCKET_CONNECT_HOST,
                                    hostname);
        inSessionSettings.setLong(sessionId,
                                  Initiator.SETTING_SOCKET_CONNECT_PORT,
                                  port);
        inSessionSettings.setLong(sessionId,
                                  Session.SETTING_HEARTBTINT,
                                  heartBtInt);
        inSessionSettings.setString(sessionId,
                                    Session.SETTING_START_TIME,
                                    startTime);
        inSessionSettings.setString(sessionId,
                                    Session.SETTING_END_TIME,
                                    endTime);
        inSessionSettings.setString(sessionId,
                                    Session.SETTING_TIMEZONE,
                                    timeZone);
        inSessionSettings.setString(sessionId,
                                    Session.SETTING_RESET_ON_LOGON,
                                    "Y");
        inSessionSettings.setString(sessionId,
                                    Session.SETTING_RESET_ON_LOGOUT,
                                    "Y");
        inSessionSettings.setString(sessionId,
                                    Session.SETTING_RESET_ON_DISCONNECT,
                                    "Y");
        inSessionSettings.setString(sessionId,
                                    Session.SETTING_RESET_ON_ERROR,
                                    "Y");
        inSessionSettings.setString(sessionId,
                                    Session.SETTING_DATA_DICTIONARY,
                                    dataDictionary);
        inSessionSettings.setString(sessionId,
                                    SessionSettings.BEGINSTRING,
                                    sessionId.getBeginString());
        inSessionSettings.setString(sessionId,
                                    SessionSettings.SENDERCOMPID,
                                    sessionId.getSenderCompID());
        inSessionSettings.setString(sessionId,
                                    SessionSettings.TARGETCOMPID,
                                    sessionId.getTargetCompID());
        inSessionSettings.setLong(sessionId,
                                  Initiator.SETTING_RECONNECT_INTERVAL,
                                  reconnectInterval);
        inSessionSettings.setString(sessionId,
                                    Session.SETTING_PERSIST_MESSAGES,
                                    "N");
        if(appDataDictionary != null) {
            inSessionSettings.setString(sessionId,
                                        Session.SETTING_APP_DATA_DICTIONARY,
                                        appDataDictionary);
        }
        if(fixAplVersion != null) {
            inSessionSettings.setString(sessionId,
                                        Session.SETTING_DEFAULT_APPL_VER_ID,
                                        fixAplVersion);
        }
    }
    /**
     * session id value
     */
    private SessionID sessionId;
    /**
     * sender comp id value to use
     */
    private String senderCompId;
    /**
     * target comp id value to use
     */
    private String targetCompId = "MRKTC-EXCH";
    /**
     * hostname to connect to
     */
    private String hostname = "exchange.marketcetera.com";
    /**
     * port to connect to
     */
    private int port = 7001;
    /**
     * FIX version to use for exchange traffic
     */
    private String fixVersion = FIXVersion.FIX44.getVersion();
    /**
     * FIX application version if using FIXT11 for the {{@link #fixVersion}}
     */
    private String fixAplVersion = null;
    /**
     * interval at which to connect to the exchange
     */
    private int reconnectInterval = 5;
    /**
     * session heart beat interval
     */
    private int heartBtInt = 30;
    /**
     * session start time
     */
    private String startTime = "00:00:00";
    /**
     * session end time
     */
    private String endTime = "22:45:00";
    /**
     * session time zone
     */
    private String timeZone = "US/Pacific";
    /**
     * session FIX dictionary
     */
    private String dataDictionary = "FIX44.xml";
    /**
     * session FIX application data dictionary
     */
    private String appDataDictionary = null;
}
