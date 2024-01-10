package org.marketcetera.fix.impl;

import java.io.Serializable;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.SimpleClusterData;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.MutableActiveFixSession;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides a POJO {@link MutableActiveFixSession} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="activeFixSession")
@XmlAccessorType(XmlAccessType.NONE)
public class SimpleActiveFixSession
        implements MutableActiveFixSession,Serializable,Comparable<SimpleActiveFixSession>
{
    /**
     * Create a new SimpleActiveFixSession instance.
     *
     * @param inActiveFixSession an <code>ActiveFixSession</code> value
     */
    public SimpleActiveFixSession(ActiveFixSession inActiveFixSession)
    {
        setClusterData(inActiveFixSession.getClusterData());
        setSenderSequenceNumber(inActiveFixSession.getSenderSequenceNumber());
        setStatus(inActiveFixSession.getStatus());
        setTargetSequenceNumber(inActiveFixSession.getTargetSequenceNumber());
        setBrokerAlgos(inActiveFixSession.getBrokerAlgos());
    }
    /**
     * Create a new SimpleActiveFixSession instance.
     */
    public SimpleActiveFixSession()
    {
        fixSession = new SimpleFixSession();
    }
    /**
     * Create a new SimpleActiveFixSession instance.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inInstanceData a <code>ClusterData</code> value
     * @param inBrokerStatus a <code>FixSessionStatus</code> value
     * @param inSessionCustomization a <code>SessionCustomization</code> value or <code>null</code>
     */
    public SimpleActiveFixSession(FixSession inFixSession,
                                  ClusterData inInstanceData,
                                  FixSessionStatus inBrokerStatus,
                                  SessionCustomization inSessionCustomization)
    {
        if(inSessionCustomization != null) {
            setBrokerAlgos(inSessionCustomization.getBrokerAlgos());
        }
        setClusterData(inInstanceData);
        setFixSession(inFixSession);
        setStatus(inBrokerStatus);
    }
    /**
     * Create a new SimpleActiveFixSession instance.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inInstanceData a <code>ClusterData</code> value
     * @param inBrokerStatus a <code>FixSessionStatus</code> value
     */
    public SimpleActiveFixSession(FixSession inFixSession,
                                  ClusterData inInstanceData,
                                  FixSessionStatus inBrokerStatus)
    {
        this(inFixSession,
             inInstanceData,
             inBrokerStatus,
             null);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getTargetSequenceNumber()
     */
    @Override
    public int getTargetSequenceNumber()
    {
        return targetSequenceNumber;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getSenderSequenceNumber()
     */
    @Override
    public int getSenderSequenceNumber()
    {
        return senderSequenceNumber;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getStatus()
     */
    @Override
    public FixSessionStatus getStatus()
    {
        return fixSessionStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setTargetSequenceNumber(int)
     */
    @Override
    public void setTargetSequenceNumber(int inTargetSequenceNumber)
    {
        targetSequenceNumber = inTargetSequenceNumber;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setSenderSequenceNumber(int)
     */
    @Override
    public void setSenderSequenceNumber(int inSenderSequenceNumber)
    {
        senderSequenceNumber = inSenderSequenceNumber;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setStatus(org.marketcetera.fix.FixSessionStatus)
     */
    @Override
    public void setStatus(FixSessionStatus inFixSessionStatus)
    {
        fixSessionStatus = inFixSessionStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getClusterData()
     */
    @Override
    public ClusterData getClusterData()
    {
        return clusterData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getFixSession()
     */
    @Override
    public FixSession getFixSession()
    {
        return fixSession;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.HasMutableView#getMutableView()
     */
    @Override
    public MutableActiveFixSession getMutableView()
    {
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setFixSession(org.marketcetera.fix.FixSession)
     */
    @Override
    public void setFixSession(FixSession inFixSession)
    {
        if(inFixSession instanceof SimpleFixSession) {
            fixSession = (SimpleFixSession)inFixSession;
        }
        fixSession = new SimpleFixSession(inFixSession);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setClusterData(org.marketcetera.cluster.ClusterData)
     */
    @Override
    public void setClusterData(ClusterData inClusterData)
    {
        if(inClusterData == null) {
            clusterData = new SimpleClusterData();
            return;
        }
        if(inClusterData instanceof SimpleClusterData) {
            clusterData = (SimpleClusterData)inClusterData;
        }
        clusterData = new SimpleClusterData(inClusterData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ActiveFixSession#getBrokerAlgos()
     */
    @Override
    public Set<BrokerAlgoSpec> getBrokerAlgos()
    {
        return brokerAlgos;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.MutableActiveFixSession#setBrokerAlgos(java.util.Set)
     */
    @Override
    public void setBrokerAlgos(Set<BrokerAlgoSpec> inBrokerAlgoSpecs)
    {
        brokerAlgos = inBrokerAlgoSpecs;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Session: ").append(getFixSession().getName()).append(" (").append(getFixSession().getBrokerId()).append(")");
        if(fixSessionStatus == null) {
            builder.append("is unknown on ");
        } else {
            switch(fixSessionStatus) {
                case AFFINITY_MISMATCH:
                    builder.append("is not bound on ");
                    builder.append(clusterData);
                    break;
                case BACKUP:
                    builder.append("is backup on ");
                    builder.append(clusterData);
                    break;
                case CONNECTED:
                    builder.append("is available on ");
                    builder.append(clusterData);
                    break;
                case DELETED:
                    builder.append("has been deleted");
                    break;
                case DISABLED:
                    builder.append("is disabled");
                    break;
                case DISCONNECTED:
                    builder.append("is disconnected on ");
                    builder.append(clusterData);
                    break;
                case NOT_CONNECTED:
                    builder.append("is not connected on ");
                    builder.append(clusterData);
                    break;
                case STOPPED:
                    builder.append("is stopped on ");
                    builder.append(clusterData);
                    break;
                default:
                    break;
            }
        }
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SimpleActiveFixSession inO)
    {
        return new CompareToBuilder().append(getFixSession().getBrokerId(),inO.getFixSession().getBrokerId()).toComparison();
    }
    /**
     * FIX session value
     */
    @XmlElement(name="fixSession")
    private SimpleFixSession fixSession;
    /**
     * cluster data value
     */
    @XmlElement
    private SimpleClusterData clusterData;
    /**
     * target sequence number value
     */
    @XmlAttribute
    private int targetSequenceNumber;
    /**
     * sender sequence number value
     */
    @XmlAttribute
    private int senderSequenceNumber;
    /**
     * fix session status value
     */
    @XmlAttribute
    private FixSessionStatus fixSessionStatus;
    /**
     * broker algos value
     */
    @XmlElementWrapper
    private Set<BrokerAlgoSpec> brokerAlgos = Sets.newHashSet();
    private static final long serialVersionUID = 2114962365315641093L;
}
