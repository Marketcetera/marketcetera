package org.marketcetera.fix.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.MutableFixSession;

import com.google.common.collect.Maps;


/* $License$ */

/**
 * Provides a simple FIX session implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
@XmlRootElement(name="fixSession")
@XmlAccessorType(XmlAccessType.NONE)
public class SimpleFixSession
        implements MutableFixSession,Serializable
{
    /**
     * Create a new SimpleFixSession instance.
     */
    public SimpleFixSession() {}
    /**
     * Create a new SimpleFixSession instance.
     *
     * @param inFixSession a <code>FixSession</code> value
     */
    public SimpleFixSession(FixSession inFixSession)
    {
        setAffinity(inFixSession.getAffinity());
        setBrokerId(inFixSession.getBrokerId());
        setDescription(inFixSession.getDescription());
        setHost(inFixSession.getHost());
        setIsAcceptor(inFixSession.isAcceptor());
        setIsDeleted(inFixSession.isDeleted());
        setIsEnabled(inFixSession.isEnabled());
        setMappedBrokerId(inFixSession.getMappedBrokerId());
        setName(inFixSession.getName());
        setPort(inFixSession.getPort());
        setSessionId(inFixSession.getSessionId());
        setSessionSettings(Maps.newHashMap(inFixSession.getSessionSettings()));
    }
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
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixSession#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixSession#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableFixSession#setName(java.lang.String)
     */
    @Override
    public void setName(String inName)
    {
        name = inName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableFixSession#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleFixSession [name=").append(name).append(", description=").append(description)
                .append(", port=").append(port).append(", host=").append(host).append(", sessionSettings=")
                .append(sessionSettings).append(", isEnabled=").append(isEnabled).append(", isDeleted=")
                .append(isDeleted).append(", isAcceptor=").append(isAcceptor).append(", sessionId=").append(sessionId)
                .append(", brokerId=").append(brokerId).append(", mappedBrokerId=").append(mappedBrokerId)
                .append(", affinity=").append(affinity).append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixSession#getMutableView()
     */
    @Override
    public MutableFixSession getMutableView()
    {
        return this;
    }
    /**
     * name value
     */
    @XmlAttribute
    private String name;
    /**
     * description value
     */
    @XmlAttribute
    private String description;
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
    private static final long serialVersionUID = -4759643874016811467L;
}
