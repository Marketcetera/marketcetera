package org.marketcetera.fix.impl;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.MutableFixSession;
import org.marketcetera.persist.NDEntityBase;


/* $License$ */

/**
 * Provides a simple FIX session implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FixSessionDto.java 85311 2016-05-24 14:49:00Z colin $
 * @since 1.0.1
 */
@XmlRootElement(name="fixSession")
@XmlAccessorType(XmlAccessType.NONE)
public class SimpleFixSession
        extends NDEntityBase
        implements FixSession, ActiveFixSession, MutableFixSession
{
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#getAffinity()
     */
    @Override
    public int getAffinity()
    {
        return affinity;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#setAffinity(int)
     */
    @Override
    public void setAffinity(int inAffinity)
    {
        affinity = inAffinity;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#getBrokerId()
     */
    @Override
    public String getBrokerId()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#setBrokerId(java.lang.String)
     */
    @Override
    public void setBrokerId(String inBrokerId)
    {
        brokerId = inBrokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixSession#getMappedBrokerId()
     */
    @Override
    public String getMappedBrokerId()
    {
        return mappedBrokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixSession#setMappedBrokerId(java.lang.String)
     */
    @Override
    public void setMappedBrokerId(String inBrokerId)
    {
        mappedBrokerId = inBrokerId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#getSessionId()
     */
    @Override
    public String getSessionId()
    {
        return sessionId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#setSessionId(java.lang.String)
     */
    @Override
    public void setSessionId(String inSessionId)
    {
        sessionId = inSessionId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#isAcceptor()
     */
    @Override
    public boolean isAcceptor()
    {
        return isAcceptor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#setIsAcceptor(boolean)
     */
    @Override
    public void setIsAcceptor(boolean inIsAcceptor)
    {
        isAcceptor = inIsAcceptor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#isDeleted()
     */
    @Override
    public boolean isDeleted()
    {
        return isDeleted;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#setIsEnabled(boolean)
     */
    @Override
    public void setIsEnabled(boolean inIsEnabled)
    {
        isEnabled = inIsEnabled;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#getPort()
     */
    @Override
    public int getPort()
    {
        return port;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#setPort(int)
     */
    @Override
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#getHost()
     */
    @Override
    public String getHost()
    {
        return host;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#setHost(java.lang.String)
     */
    @Override
    public void setHost(String inHost)
    {
        host = inHost;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.FixSession#getSessionSettings()
     */
    @Override
    public Map<String,String> getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * Sets the instance value.
     *
     * @param inInstance a <code>String</code> value
     */
    public void setInstance(String inInstance)
    {
        instance = inInstance;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.ActiveFixSession#getInstance()
     */
    @Override
    public String getInstance()
    {
        return instance;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.ActiveFixSession#getTargetSequenceNumber()
     */
    @Override
    public int getTargetSequenceNumber()
    {
        return targetSequenceNumber;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.ActiveFixSession#getSenderSequenceNumber()
     */
    @Override
    public int getSenderSequenceNumber()
    {
        return senderSequenceNumber;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.fix.ActiveFixSession#getStatus()
     */
    @Override
    public FixSessionStatus getStatus()
    {
        return status;
    }
    /**
     * Sets the status value.
     *
     * @param inStatus a <code>FixSessionStatus</code> value
     */
    public void setStatus(FixSessionStatus inStatus)
    {
        status = inStatus;
    }
    /**
     * Sets the sessionSettings value.
     *
     * @param inSessionSettings a <code>Map&lt;String,String&gt;</code> value
     */
    public void setSessionSettings(Map<String,String> inSessionSettings)
    {
        sessionSettings = inSessionSettings;
    }
    /**
     * Sets the isDeleted value.
     *
     * @param inIsDeleted a <code>boolean</code> value
     */
    public void setIsDeleted(boolean inIsDeleted)
    {
        isDeleted = inIsDeleted;
    }
    /**
     * Sets the senderSequenceNumber value.
     *
     * @param inSenderSequenceNumber an <code>int</code> value
     */
    public void setSenderSequenceNumber(int inSenderSequenceNumber)
    {
        senderSequenceNumber = inSenderSequenceNumber;
    }
    /**
     * Sets the targetSequenceNumber value.
     *
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    public void setTargetSequenceNumber(int inTargetSequenceNumber)
    {
        targetSequenceNumber = inTargetSequenceNumber;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleFixSession [port=").append(port).append(", host=").append(host).append(", isEnabled=")
                .append(isEnabled).append(", isDeleted=").append(isDeleted).append(", isAcceptor=").append(isAcceptor)
                .append(", sessionId=").append(sessionId).append(", brokerId=").append(brokerId)
                .append(", mappedBrokerId=").append(mappedBrokerId).append(", affinity=")
                .append(affinity).append(", instance=").append(instance).append(", status=").append(status)
                .append(", senderSequenceNumber=").append(senderSequenceNumber).append(", targetSequenceNumber=")
                .append(targetSequenceNumber).append(", sessionSettings=").append(sessionSettings).append("]");
        return builder.toString();
    }
    /**
     * port value
     */
    @XmlAttribute
    private int port;
    /**
     * host value
     */
    @XmlAttribute
    private String host;
    /**
     * session settings value
     */
    @XmlElement
    private Map<String,String> sessionSettings = new HashMap<>();
    /**
     * enabled value
     */
    @XmlAttribute
    private boolean isEnabled;
    /**
     * is deleted value
     */
    @XmlAttribute
    private boolean isDeleted;
    /**
     * is acceptor value
     */
    @XmlAttribute
    private boolean isAcceptor;
    /**
     * session id value
     */
    @XmlAttribute
    private String sessionId;
    /**
     * broker id value
     */
    @XmlAttribute
    private String brokerId;
    /**
     * mapped broker id value
     */
    @XmlAttribute
    private String mappedBrokerId;
    /**
     * affinity value
     */
    @XmlAttribute
    private int affinity;
    /**
     * cluster instance value
     */
    @XmlAttribute
    private String instance;
    /**
     * FIX session status value
     */
    @XmlAttribute
    private FixSessionStatus status;
    /**
     * sender sequence number value
     */
    @XmlAttribute
    private int senderSequenceNumber;
    /**
     * target sequence number value
     */
    @XmlAttribute
    private int targetSequenceNumber;
    private static final long serialVersionUID = 3133259062821595992L;
}
