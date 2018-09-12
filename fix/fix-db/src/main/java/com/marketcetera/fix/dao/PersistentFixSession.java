package com.marketcetera.fix.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.format.DateTimeFormatter;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.fix.FixSession;
import org.marketcetera.persist.EntityBase;

import quickfix.Session;

/* $License$ */

/**
 * Provides a persistent <code>FixSession</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@Entity(name="FixSession")
@Table(name="fix_sessions")
@XmlAccessorType(XmlAccessType.NONE)
public class PersistentFixSession
        extends EntityBase
        implements FixSession
{
    /**
     * Validates the object.
     *
     * @throws IllegalArgumentException if an attribute is not valid
     */
    public void validate()
    {
        Validate.isTrue(affinity > 0,
                        "Affinity must be greater than zero");
        Validate.notNull(brokerId,
                         "Broker ID is required");
        Validate.notNull(host,
                         "Host is required");
        Validate.notNull(name,
                         "Name is required");
        Validate.isTrue(port > 0 && port < 65536,
                        "Port must be greater than 0 and less than 65536");
        Validate.notNull(sessionId,
                         "Session ID is required");
        String startTime = sessionSettings.get(Session.SETTING_START_TIME);
        Validate.notNull(startTime,
                         "Start time is required");
        String endTime = sessionSettings.get(Session.SETTING_END_TIME);
        Validate.notNull(endTime,
                         "End time is required");
        try {
            startEndTimeFormatter.parseDateTime(startTime);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid start time: " + startTime);
        }
        try {
            startEndTimeFormatter.parseDateTime(endTime);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid end time: " + endTime);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#getAffinity()
     */
    @Override
    public int getAffinity()
    {
        return affinity;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setAffinity(int)
     */
    @Override
    public void setAffinity(int inAffinity)
    {
        affinity = inAffinity;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#getBrokerId()
     */
    @Override
    public String getBrokerId()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setBrokerId(java.lang.String)
     */
    @Override
    public void setBrokerId(String inBrokerId)
    {
        brokerId = StringUtils.trimToNull(inBrokerId);
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
        mappedBrokerId = StringUtils.trimToNull(inBrokerId);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setName(java.lang.String)
     */
    @Override
    public void setName(String inName)
    {
        name = StringUtils.trimToNull(inName);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#isAcceptor()
     */
    @Override
    public boolean isAcceptor()
    {
        return isAcceptor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setIsAcceptor(boolean)
     */
    @Override
    public void setIsAcceptor(boolean inIsAcceptor)
    {
        isAcceptor = inIsAcceptor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setIsEnabled(boolean)
     */
    @Override
    public void setIsEnabled(boolean inIsEnabled)
    {
        isEnabled = inIsEnabled;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setIsDeleted(boolean)
     */
    @Override
    public boolean isDeleted()
    {
        return isDeleted;
    }
    /**
     * Delete the session.
     */
    public void delete()
    {
        isDeleted = true;
    }
    /**
     * Undelete the session.
     */
    public void undelete()
    {
        isDeleted = false;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#getPort()
     */
    @Override
    public int getPort()
    {
        return port;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#getHost()
     */
    @Override
    public String getHost()
    {
        return host;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#getSessionSettings()
     */
    @Override
    public Map<String,String> getSessionSettings()
    {
        return sessionSettings;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setPort(int)
     */
    @Override
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setHost(java.lang.String)
     */
    @Override
    public void setHost(String inHost)
    {
        host = StringUtils.trimToNull(inHost);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#getSessionId()
     */
    @Override
    public String getSessionId()
    {
        return sessionId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSession#setSessionId(java.lang.String)
     */
    @Override
    public void setSessionId(String inSessionId)
    {
        sessionId = StringUtils.trimToNull(inSessionId);
    }
    /**
     * Updates this object with the attributes from the given object.
     *
     * @param inFixSession a <code>FixSession</code> value
     */
    public void update(FixSession inFixSession)
    {
        affinity = inFixSession.getAffinity();
        brokerId = inFixSession.getBrokerId();
        description = inFixSession.getDescription();
        host = inFixSession.getHost();
        isAcceptor = inFixSession.isAcceptor();
        isDeleted = inFixSession.isDeleted();
        isEnabled = inFixSession.isEnabled();
        mappedBrokerId = inFixSession.getMappedBrokerId();
        name = inFixSession.getName();
        port = inFixSession.getPort();
        sessionId = inFixSession.getSessionId();
        sessionSettings.clear();
        sessionSettings.putAll(inFixSession.getSessionSettings());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistentFixSession [sessionId=").append(sessionId).append(", brokerId=").append(brokerId)
                .append(", name=").append(name).append(", description=").append(description).append(", host=")
                .append(host).append(", port=").append(port).append(", affinity=").append(affinity)
                .append(", isAcceptor=").append(isAcceptor).append(", isEnabled=").append(isEnabled)
                .append(", mappedBrokerId=").append(mappedBrokerId)
                .append(", isDeleted=").append(isDeleted).append(", sessionSettings=").append(sessionSettings)
                .append("]");
        return builder.toString();
    }
    /**
     * isAcceptor value
     */
    @XmlAttribute
    @Column(name="acceptor",nullable=false)
    private boolean isAcceptor;
    /**
     * isEnabled value
     */
    @XmlAttribute
    @Column(name="enabled",nullable=false)
    private boolean isEnabled;
    /**
     * isDeleted value
     */
    @XmlAttribute
    @Column(name="deleted",nullable=false)
    private boolean isDeleted = false;
    /**
     * port value
     */
    @XmlAttribute
    @Column(name="port",nullable=false)
    private int port;
    /**
     * host value
     */
    @XmlAttribute
    @Column(name="host",nullable=false)
    private String host;
    /**
     * affinity value
     */
    @XmlAttribute
    @Column(name="affinity",nullable=false)
    private int affinity;
    /**
     * broker ID value
     */
    @XmlAttribute
    @Column(name="broker_id",nullable=false)
    private String brokerId;
    /**
     * mapped broker ID value
     */
    @XmlAttribute
    @Column(name="mapped_broker_id",nullable=true)
    private String mappedBrokerId;
    /**
     * session ID value
     */
    @XmlAttribute
    @Column(name="session_id",nullable=false)
    private String sessionId;
    /**
     * name value
     */
    @XmlAttribute
    @Column(name="name",nullable=false)
    private String name;
    /**
     * description value
     */
    @XmlAttribute
    @Column(name="description",nullable=true)
    private String description;
    /**
     * session attributes
     */
    @XmlElement
    @Column(name="value")
    @MapKeyColumn(name="name")
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="fix_session_attributes", joinColumns=@JoinColumn(name="fix_session_id"))
    private final Map<String,String> sessionSettings = new HashMap<>();
    /**
     * validates the format of the start and end time value
     */
    @XmlTransient
    private static final DateTimeFormatter startEndTimeFormatter = TimeFactoryImpl.WALLCLOCK_SECONDS_LOCAL;
    private static final long serialVersionUID = -831733719159592669L;
}
