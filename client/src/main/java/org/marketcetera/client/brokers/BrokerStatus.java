package org.marketcetera.client.brokers;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The web service representation of a single broker's status.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@ClassVersion("$Id$")
public class BrokerStatus
    implements Serializable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private final String mName;
    private final BrokerID mId;
    private final boolean mLoggedOn;
    private final Set<BrokerAlgoSpec> mBrokerAlgos;


    // CONSTRUCTORS.

    /**
     * Creates a new status representation, given the broker
     * information.
     *
     * @param name The broker name.
     * @param id The broker ID.
     * @param loggedOn The logon flag.
     * @param inAlgoSpecs a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    public BrokerStatus(String name,
                        BrokerID id,
                        boolean loggedOn,
                        Set<BrokerAlgoSpec> inAlgoSpecs)
    {
        mName=name;
        mId=id;
        mLoggedOn=loggedOn;
        mBrokerAlgos = inAlgoSpecs;
    }
    /**
     * Create a new BrokerStatus instance.
     *
     * @param inName a <code>String</code> value
     * @param inId a <code>BrokerID</code> value
     * @param inLoggedOn a <code>boolean</code> value
     */
    public BrokerStatus(String inName,
                        BrokerID inId,
                        boolean inLoggedOn)
    {
        mName = inName;
        mId = inId;
        mLoggedOn = inLoggedOn;
        mBrokerAlgos = null;
    }
    /**
     * Creates a new status representation. This empty constructor is
     * intended for use by JAXB.
     */

    protected BrokerStatus()
    {
        mName=null;
        mId=null;
        mLoggedOn=false;
        mBrokerAlgos = null;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's name.
     *
     * @return The name.
     */

    public String getName()
    {
        return mName;
    }

    /**
     * Returns the receiver's broker ID.
     *
     * @return The ID.
     */

    public BrokerID getId()
    {
        return mId;
    }

    /**
     * Returns the receiver's logon flag.
     *
     * @return The flag.
     */

    public boolean getLoggedOn()
    {
        return mLoggedOn;
    }
    /**
     * Get the brokerAlgos value.
     *
     * @return a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    public Set<BrokerAlgoSpec> getBrokerAlgos()
    {
        return mBrokerAlgos;
    }
    // Object.

    @Override
    public String toString()
    {
        return String.format
            ("Broker: %s(%s,%s)", //$NON-NLS-1$
             String.valueOf(getName()),String.valueOf(getId()),getLoggedOn());
    }
}
