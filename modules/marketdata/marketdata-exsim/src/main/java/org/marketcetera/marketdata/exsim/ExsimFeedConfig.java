package org.marketcetera.marketdata.exsim;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
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
     * @return a <code>FIXVersion</code> value
     */
    public FIXVersion getFixVersion()
    {
        return fixVersion;
    }
    /**
     * Sets the fixVersion value.
     *
     * @param inFixVersion a <code>FIXVersion</code> value
     */
    public void setFixVersion(FIXVersion inFixVersion)
    {
        fixVersion = inFixVersion;
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
     *
     *
     * @return
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
        Validate.notNull(senderCompId,
                         "SenderCompId required");
        sessionId = new SessionID(fixVersion.toString(),
                                  senderCompId,
                                  targetCompId);
    }
    /**
     * 
     *
     *
     * @param sessionSettings
     */
    void populateSessionSettings(SessionSettings sessionSettings)
    {
        sessionSettings.setString(sessionId,
                                  Initiator.SETTING_SOCKET_CONNECT_HOST,
                                  hostname);
        sessionSettings.setLong(sessionId,
                                Initiator.SETTING_SOCKET_CONNECT_PORT,
                                port);
        sessionSettings.setLong(sessionId,
                                Session.SETTING_HEARTBTINT,
                                heartBtInt);
        sessionSettings.setString(sessionId,
                                  Session.SETTING_START_TIME,
                                  startTime);
        sessionSettings.setString(sessionId,
                                  Session.SETTING_END_TIME,
                                  endTime);
        sessionSettings.setString(sessionId,
                                  Session.SETTING_TIMEZONE,
                                  timeZone);
        sessionSettings.setString(sessionId,
                                  Session.SETTING_RESET_ON_LOGON,
                                  "Y");
        sessionSettings.setString(sessionId,
                                  Session.SETTING_RESET_ON_LOGOUT,
                                  "Y");
        sessionSettings.setString(sessionId,
                                  Session.SETTING_RESET_ON_DISCONNECT,
                                  "Y");
        sessionSettings.setString(sessionId,
                                  Session.SETTING_RESET_ON_ERROR,
                                  "Y");
        sessionSettings.setString(sessionId,
                                  Session.SETTING_DATA_DICTIONARY,
                                  dataDictionary);
        sessionSettings.setString(sessionId,
                                  SessionSettings.BEGINSTRING,
                                  sessionId.getBeginString());
        sessionSettings.setString(sessionId,
                                  SessionSettings.SENDERCOMPID,
                                  sessionId.getSenderCompID());
        sessionSettings.setString(sessionId,
                                  SessionSettings.TARGETCOMPID,
                                  sessionId.getTargetCompID());
        sessionSettings.setLong(sessionId,
                                Initiator.SETTING_RECONNECT_INTERVAL,
                                reconnectInterval);
    }
    /**
     * 
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
    private FIXVersion fixVersion = FIXVersion.FIX42;
    /**
     * interval at which to connect to the exchange
     */
    private int reconnectInterval = 5;
    /**
     * 
     */
    private int heartBtInt = 30;
    /**
     * 
     */
    private String startTime = "00:00:00";
    /**
     * 
     */
    private String endTime = "23:50:00";
    /**
     * 
     */
    private String timeZone = "US/Pacific";
    /**
     * 
     */
    private String dataDictionary = "FIX42.xml";
}
