package org.marketcetera.client.brokers;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.misc.ClassVersion;

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
        implements Serializable
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
        settings = inSettings;
        brokerAlgos = inAlgoSpecs;
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
    @SuppressWarnings("unused")
    private BrokerStatus()
    {
        name = null;
        brokerId = null;
        loggedOn = false;
        brokerAlgos = null;
        settings = null;
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
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Broker: %s(%s,%s)", //$NON-NLS-1$
                             String.valueOf(getName()),
                             String.valueOf(getId()),
                             getLoggedOn());
    }
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
    private final Set<BrokerAlgoSpec> brokerAlgos;
    /**
     * broker settings value
     */
    private final Map<String,String> settings;
    private static final long serialVersionUID = -4170685026349637823L;
}
