package org.marketcetera.marketdata.exsim;

import javax.annotation.PostConstruct;

import org.marketcetera.core.CoreException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Provides configuration information for the {@link ExsimFeedModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@EnableAutoConfiguration
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
    public quickfix.SessionID getSessionId()
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
        sessionId = new quickfix.SessionID(fixVersion,
                                           senderCompId,
                                           targetCompId);
    }
    /**
     * Populates the given session settings value with the settings established for this config.
     *
     * @param inSessionSettings a <code>quickfix.SessionSettings</code> value
     */
    void populateSessionSettings(quickfix.SessionSettings inSessionSettings)
    {
        inSessionSettings.setString(sessionId,
                                    quickfix.Initiator.SETTING_SOCKET_CONNECT_HOST,
                                    hostname);
        inSessionSettings.setLong(sessionId,
                                  quickfix.Initiator.SETTING_SOCKET_CONNECT_PORT,
                                  port);
        inSessionSettings.setLong(sessionId,
                                  quickfix.Session.SETTING_HEARTBTINT,
                                  heartBtInt);
        inSessionSettings.setString(sessionId,
                                    quickfix.Session.SETTING_START_TIME,
                                    startTime);
        inSessionSettings.setString(sessionId,
                                    quickfix.Session.SETTING_END_TIME,
                                    endTime);
        inSessionSettings.setString(sessionId,
                                    quickfix.Session.SETTING_TIMEZONE,
                                    timeZone);
        inSessionSettings.setString(sessionId,
                                    quickfix.Session.SETTING_RESET_ON_LOGON,
                                    "Y");
        inSessionSettings.setString(sessionId,
                                    quickfix.Session.SETTING_RESET_ON_LOGOUT,
                                    "Y");
        inSessionSettings.setString(sessionId,
                                    quickfix.Session.SETTING_RESET_ON_DISCONNECT,
                                    "Y");
        inSessionSettings.setString(sessionId,
                                    quickfix.Session.SETTING_RESET_ON_ERROR,
                                    "Y");
        inSessionSettings.setString(sessionId,
                                    quickfix.Session.SETTING_DATA_DICTIONARY,
                                    dataDictionary);
        inSessionSettings.setString(sessionId,
                                    quickfix.SessionSettings.BEGINSTRING,
                                    sessionId.getBeginString());
        inSessionSettings.setString(sessionId,
                                    quickfix.SessionSettings.SENDERCOMPID,
                                    sessionId.getSenderCompID());
        inSessionSettings.setString(sessionId,
                                    quickfix.SessionSettings.TARGETCOMPID,
                                    sessionId.getTargetCompID());
        inSessionSettings.setLong(sessionId,
                                  quickfix.Initiator.SETTING_RECONNECT_INTERVAL,
                                  reconnectInterval);
        inSessionSettings.setString(sessionId,
                                    quickfix.Session.SETTING_PERSIST_MESSAGES,
                                    "N");
        if(appDataDictionary != null) {
            inSessionSettings.setString(sessionId,
                                        quickfix.Session.SETTING_APP_DATA_DICTIONARY,
                                        appDataDictionary);
        }
        if(fixAplVersion != null) {
            inSessionSettings.setString(sessionId,
                                        quickfix.Session.SETTING_DEFAULT_APPL_VER_ID,
                                        fixAplVersion);
        }
    }
    /**
     * session ID value that the module will use to connect
     */
    private quickfix.SessionID sessionId;
    /**
     * sender comp id value to use
     */
    @Value("${metc.marketdata.exsim.senderCompId}")
    private String senderCompId;
    /**
     * target comp id value to use
     */
    @Value("${metc.marketdata.exsim.targetCompId:MRKTC-EXCH}")
    private String targetCompId;
    /**
     * hostname to connect to
     */
    @Value("${metc.marketdata.exsim.hostname:exchange.marketcetera.com}")
    private String hostname;
    /**
     * port to connect to
     */
    @Value("${metc.marketdata.exsim.port:7001}")
    private int port;
    /**
     * FIX version to use for exchange traffic
     */
    @Value("${metc.marketdata.exsim.fixVersion:FIX.4.4}")
    private String fixVersion;
    /**
     * FIX application version if using FIXT11 for the {@link #fixVersion}
     */
    @Value("${metc.marketdata.exsim.fixAplVersion}")
    private String fixAplVersion;
    /**
     * interval at which to connect to the exchange
     */
    @Value("${metc.marketdata.exsim.reconnectInterval:5}")
    private int reconnectInterval;
    /**
     * session heart beat interval
     */
    @Value("${metc.marketdata.exsim.heartBtInt:30}")
    private int heartBtInt;
    /**
     * session start time
     */
    @Value("${metc.marketdata.exsim.startTime:00:00:00}")
    private String startTime;
    /**
     * session end time
     */
    @Value("${metc.marketdata.exsim.endTime:22:45:00}")
    private String endTime;
    /**
     * session time zone
     */
    @Value("${metc.marketdata.exsim.timeZone:US/Pacific}")
    private String timeZone;
    /**
     * session FIX dictionary
     */
    @Value("${metc.marketdata.exsim.dataDictionary:FIX44.xml}")
    private String dataDictionary;
    /**
     * session FIX application data dictionary
     */
    @Value("${metc.marketdata.exsim.appDataDictionary}")
    private String appDataDictionary;
    /**
     * number of milliseconds to wait for the feed to become available if a request is made while it is offline
     */
    @Value("${metc.marketdata.exsim.feedAvailableTimeout:10000}")
    private long feedAvailableTimeout;
}
