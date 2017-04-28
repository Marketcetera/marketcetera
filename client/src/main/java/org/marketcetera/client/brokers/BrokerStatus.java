package org.marketcetera.client.brokers;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import quickfix.SessionFactory;

/* $License$ */

/**
 * The web service representation of a single broker's status.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class BrokerStatus
        implements Serializable, Comparable<BrokerStatus>
{
    /**
     * Create a new BrokerStatus instance.
     *
     * @param inName a <code>String</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inLoggedOn a <code>boolean</code> value
     * @param inSettings a <code>Map&lt;String,String&gt;</code> value
     * @param inAlgoSpecs a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    public BrokerStatus(String inName,
                        BrokerID inBrokerId,
                        boolean inLoggedOn,
                        Map<String,String> inSettings,
                        Set<BrokerAlgoSpec> inAlgoSpecs)
    {
        name = inName;
        brokerId = inBrokerId;
        loggedOn = inLoggedOn;
        if(inSettings != null) {
            settings.putAll(inSettings);
        }
        if(inAlgoSpecs != null) {
            brokerAlgos.addAll(inAlgoSpecs);
        }
    }
    /**
     * Create a new BrokerStatus instance.
     *
     * @param inName a <code>String</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inLoggedOn a <code>boolean</code> value
     * @param inSettings a <code>Map&lt;String,String&gt;</code> value
     */
    public BrokerStatus(String inName,
                        BrokerID inBrokerId,
                        boolean inLoggedOn,
                        Map<String,String> inSettings)
    {
        this(inName,
             inBrokerId,
             inLoggedOn,
             inSettings,
             null);
    }
    /**
     * Create a new BrokerStatus instance.
     *
     * @param inName a <code>String</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inLoggedOn a <code>boolean</code> value
     * @param inAlgoSpecs a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    public BrokerStatus(String inName,
                        BrokerID inBrokerId,
                        boolean inLoggedOn,
                        Set<BrokerAlgoSpec> inAlgoSpecs)
    {
        this(inName,
             inBrokerId,
             inLoggedOn,
             null,
             inAlgoSpecs);
    }
    /**
     * Create a new BrokerStatus instance.
     *
     * @param inName a <code>String</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inLoggedOn a <code>boolean</code> value
     */
    public BrokerStatus(String inName,
                        BrokerID inBrokerId,
                        boolean inLoggedOn)
    {
        this(inName,
             inBrokerId,
             inLoggedOn,
             null,
             null);
    }
    /**
     * Create a new BrokerStatus instance.
     */
    protected BrokerStatus()
    {
        name = null;
        brokerId = null;
        loggedOn = false;
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
     * Get the brokerId value.
     *
     * @return a <code>BrokerID</code> value
     */
    public BrokerID getId()
    {
        return brokerId;
    }
    /**
     * Get the loggedOn value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getLoggedOn()
    {
        return loggedOn;
    }
    /**
     * Get the brokerAlgos value.
     *
     * @return a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    public Set<BrokerAlgoSpec> getBrokerAlgos()
    {
        return brokerAlgos;
    }
    /**
     * Get the settings value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public Map<String,String> getSettings()
    {
        return settings;
    }
    /**
     * Gets the host name value.
     *
     * @return a <code>String</code> value
     */
    public String getHost()
    {
        if(settings != null) {
            String connectionType = settings.get(SessionFactory.SETTING_CONNECTION_TYPE);
            switch(connectionType) {
                case SessionFactory.ACCEPTOR_CONNECTION_TYPE:
                    return settings.get(socketAcceptHostKey);
                case SessionFactory.INITIATOR_CONNECTION_TYPE:
                    return settings.get(socketConnectHostKey);
                default:
                    break;
            }
        }
        return NO_HOST;
    }
    /**
     * Gets the host port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        if(settings != null) {
            String connectionType = settings.get(SessionFactory.SETTING_CONNECTION_TYPE);
            switch(connectionType) {
                case SessionFactory.ACCEPTOR_CONNECTION_TYPE:
                    return Integer.parseInt(settings.get(socketAcceptPortKey));
                case SessionFactory.INITIATOR_CONNECTION_TYPE:
                    return Integer.parseInt(settings.get(socketConnectPortKey));
                default:
                    break;
            }
        }
        return -1;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(brokerId).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BrokerStatus)) {
            return false;
        }
        BrokerStatus other = (BrokerStatus) obj;
        return new EqualsBuilder().append(brokerId,other.brokerId).isEquals();
    }
    @Override
    public String toString()
    {
        return String.format("Broker: %s(%s,%s)", //$NON-NLS-1$
                             String.valueOf(getName()),
                             String.valueOf(getId()),
                             getLoggedOn());
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(BrokerStatus inO)
    {
        return new CompareToBuilder().append(brokerId,inO.brokerId).toComparison();
    }
    /**
     * value which indicates no host
     */
    public static final String NO_HOST = "none"; //$NON-NLS-1$
    /**
     * name value
     */
    private final String name;
    /**
     * broker id value
     */
    private final BrokerID brokerId;
    /**
     * logged on value
     */
    private final boolean loggedOn;
    /**
     * broker algos value
     */
    private final Set<BrokerAlgoSpec> brokerAlgos = Sets.newHashSet();
    /**
     * broker settings value
     */
    private final Map<String,String> settings = Maps.newHashMap();
    /**
     * QJF initiator host key
     */
    private static final String socketConnectHostKey = "SocketConnectHost"; //$NON-NLS-1$
    /**
     * QJF initiator port key
     */
    private static final String socketConnectPortKey = "SocketConnectPort"; //$NON-NLS-1$
    /**
     * QJF acceptor host key
     */
    private static final String socketAcceptHostKey = "SocketAcceptHost"; //$NON-NLS-1$
    /**
     * QJF acceptor port key
     */
    private static final String socketAcceptPortKey = "SocketAcceptPort"; //$NON-NLS-1$
    private static final long serialVersionUID = -4170685026349637823L;
}
