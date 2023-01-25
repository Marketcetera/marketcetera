package org.marketcetera.web.fix.view;

import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.quickfix.FIXVersion;

import com.google.common.collect.Maps;

import quickfix.Session;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayFixSession
{
    public static DisplayFixSession create(ActiveFixSession inActiveFixSession)
    {
        DisplayFixSession displayFixSession = new DisplayFixSession();
        displayFixSession.setAffinity(inActiveFixSession.getFixSession().getAffinity());
        displayFixSession.setBrokerId(inActiveFixSession.getFixSession().getBrokerId());
        displayFixSession.setClusterData(String.valueOf(inActiveFixSession.getClusterData()));
        displayFixSession.setConnectionType(inActiveFixSession.getFixSession().isAcceptor()?ACCEPTOR:INITIATOR);
        displayFixSession.setDescription(inActiveFixSession.getFixSession().getDescription());
        quickfix.SessionID sessionId = new quickfix.SessionID(inActiveFixSession.getFixSession().getSessionId());
        FIXVersion fixVersion;
        if(sessionId.isFIXT()) {
            String rawDefaultApplVerId = inActiveFixSession.getFixSession().getSessionSettings().get("DefaultApplVerID");
            if(rawDefaultApplVerId == null) {
                fixVersion = FIXVersion.FIX50SP2;
            } else {
                fixVersion = FIXVersion.getFIXVersion(new quickfix.field.ApplVerID(rawDefaultApplVerId));
            }
        } else {
            fixVersion = FIXVersion.getFIXVersion(sessionId);
        }
        displayFixSession.setFixVersion(fixVersion);
        displayFixSession.setHostname(inActiveFixSession.getFixSession().getHost());
        displayFixSession.setName(inActiveFixSession.getFixSession().getName());
        displayFixSession.setPort(inActiveFixSession.getFixSession().getPort());
        displayFixSession.setSessionId(inActiveFixSession.getFixSession().getSessionId());
        displayFixSession.setSessionSettings(inActiveFixSession.getFixSession().getSessionSettings());
        switch(inActiveFixSession.getFixSession().getSessionSettings().get("")) {
            
        }
//        displayFixSession.setSessionType(ACCEPTOR);
//        private String sessionType;
//        private String startTime;
//        private String endTime;
//        private String timezone;
//        private String startDay;
//        private String endDay;
        displayFixSession.setStatus(inActiveFixSession.getStatus());
        return displayFixSession;
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
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     *
     *
     * @return
     */
    public FixSessionStatus getStatus()
    {
        return status;
    }
    /**
     * Sets the status value.
     *
     * @param inStatus a <code>FixSessionStatus</code> value
     */
    private void setStatus(FixSessionStatus inStatus)
    {
        status = inStatus;
    }
    /**
     * Get the isAcceptor value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isAcceptor()
    {
        return ACCEPTOR.equals(getConnectionType());
    }
    /**
     * Get the affinity value.
     *
     * @return an <code>int</code> value
     */
    public int getAffinity()
    {
        return affinity;
    }
    /**
     * Sets the affinity value.
     *
     * @param inAffinity an <code>int</code> value
     */
    public void setAffinity(int inAffinity)
    {
        affinity = inAffinity;
    }
    /**
     * Get the fixSession value.
     *
     * @return a <code>String</code> value
     */
    public String getSessionId()
    {
        return sessionId;
    }
    /**
     * Sets the fixSession value.
     *
     * @param inFixSession a <code>String</code> value
     */
    public void setSessionId(String inFixSession)
    {
        sessionId = inFixSession;
    }
    /**
     * Get the brokerId value.
     *
     * @return a <code>String</code> value
     */
    public String getBrokerId()
    {
        return brokerId;
    }
    /**
     * Sets the brokerId value.
     *
     * @param inBrokerId a <code>String</code> value
     */
    public void setBrokerId(String inBrokerId)
    {
        brokerId = inBrokerId;
    }
    /**
     * Get the clusterData value.
     *
     * @return a <code>String</code> value
     */
    public String getClusterData()
    {
        return clusterData;
    }
    /**
     * Sets the clusterData value.
     *
     * @param inClusterData a <code>String</code> value
     */
    public void setClusterData(String inClusterData)
    {
        clusterData = inClusterData;
    }
    /**
     * Get the senderSequenceNumber value.
     *
     * @return an <code>int</code> value
     */
    public int getSenderSequenceNumber()
    {
        return senderSequenceNumber;
    }
    /**
     * Get the targetSequenceNumber value.
     *
     * @return an <code>int</code> value
     */
    public int getTargetSequenceNumber()
    {
        return targetSequenceNumber;
    }
    /**
     * Get the connectionType value.
     *
     * @return a <code>String</code> value
     */
    public String getConnectionType()
    {
        return connectionType;
    }
    /**
     * Sets the connectionType value.
     *
     * @param inConnectionType a <code>String</code> value
     */
    public void setConnectionType(String inConnectionType)
    {
        connectionType = inConnectionType;
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
     * Get the sessionSettings value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public Map<String, String> getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * Sets the sessionSettings value.
     *
     * @param inSessionSettings a <code>Map&lt;String,String&gt;</code> value
     */
    public void setSessionSettings(Map<String, String> inSessionSettings)
    {
        sessionSettings = inSessionSettings;
    }
    /**
     * Get the sessionType value.
     *
     * @return a <code>String</code> value
     */
    public String getSessionType()
    {
        return sessionType;
    }
    /**
     * Sets the sessionType value.
     *
     * @param inSessionType a <code>String</code> value
     */
    public void setSessionType(String inSessionType)
    {
        sessionType = inSessionType;
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
     * Get the timezone value.
     *
     * @return a <code>String</code> value
     */
    public String getTimezone()
    {
        return timezone;
    }
    /**
     * Sets the timezone value.
     *
     * @param inTimezone a <code>String</code> value
     */
    public void setTimezone(String inTimezone)
    {
        timezone = inTimezone;
    }
    /**
     * Get the startDay value.
     *
     * @return a <code>String</code> value
     */
    public String getStartDay()
    {
        return startDay;
    }
    /**
     * Sets the startDay value.
     *
     * @param inStartDay a <code>String</code> value
     */
    public void setStartDay(String inStartDay)
    {
        startDay = inStartDay;
    }
    /**
     * Get the endDay value.
     *
     * @return a <code>String</code> value
     */
    public String getEndDay()
    {
        return endDay;
    }
    /**
     * Sets the endDay value.
     *
     * @param inEndDay a <code>String</code> value
     */
    public void setEndDay(String inEndDay)
    {
        endDay = inEndDay;
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
     * Indicates if the given session is a weekly session.
     *
     * @param inFixSession an <code>ActiveFixSession</code> value
     * @return a <code>boolean</code> value
     */
    private boolean isWeeklySession(ActiveFixSession inFixSession)
    {
        return inFixSession.getFixSession().getSessionSettings().containsKey(Session.SETTING_START_DAY);
    }
    /**
     * Indicates if the given session is a non-stop, continuous session.
     *
     * @param inFixSession an <code>ActiveFixSession</code> value
     * @return a <code>boolean</code> value
     */
    private boolean isContinuousSession(ActiveFixSession inFixSession)
    {
        return YES.equals(inFixSession.getFixSession().getSessionSettings().get(Session.SETTING_NON_STOP_SESSION));
    }
    /**
     * value for acceptor sessions
     */
    public static final String ACCEPTOR = "Acceptor";
    /**
     * value for initiator sessions
     */
    public static final String INITIATOR = "Initiator";
    /**
     * value for continuous session
     */
    public static final String CONTINUOUS = "Continuous";
    /**
     * value for daily session
     */
    public static final String DAILY = "Daily";
    /**
     * value for weekly session
     */
    public static final String WEEKLY = "Weekly";
    /**
     * value for turning a binary FIX session attribute on
     */
    public static final String YES = "Y";
    @NotEmpty
    private String hostname;
    private int port;
    @NotEmpty
    private String sessionId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String brokerId;
    private String description;
    private String clusterData;
    @NotEmpty
    private String connectionType;
    private FixSessionStatus status;
    private int affinity;
    private int senderSequenceNumber;
    private int targetSequenceNumber;
    private FIXVersion fixVersion;
    private Map<String,String> sessionSettings = Maps.newHashMap();
    private String sessionType;
    private String startTime;
    private String endTime;
    private String timezone;
    private String startDay;
    private String endDay;
}
