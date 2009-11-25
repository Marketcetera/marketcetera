package org.marketcetera.ors.brokers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import quickfix.SessionID;

/**
 * The collective in-memory representation of all brokers.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Brokers
{

    // INSTANCE DATA.

    private final SpringBrokers mSpringBrokers;
    private final List<Broker> mBrokers;
    private final Map<BrokerID,Broker> mBrokerIDMap;
    private final Map<SessionID,Broker> mSessionIDMap;


    // CONSTRUCTORS.

    /**
     * Creates a new collective representation based on the given
     * broker configurations. Any message modifiers are configured to
     * rely on the given report history services provider for
     * persistence operations.
     *
     * @param springBrokers The configurations.
     * @param historyServices The report history services provider.
     */

    public Brokers
        (SpringBrokers springBrokers,
         ReportHistoryServices historyServices)
    {
        mSpringBrokers=springBrokers;
        int capacity=getSpringBrokers().getBrokers().size();
        mBrokers=new ArrayList<Broker>(capacity);
        mBrokerIDMap=new HashMap<BrokerID,Broker>(capacity);
        mSessionIDMap=new HashMap<SessionID,Broker>(capacity);
        for (SpringBroker sb:getSpringBrokers().getBrokers()) {
            Broker b=new Broker(sb,historyServices);
            mBrokers.add(b);
            mBrokerIDMap.put(b.getBrokerID(),b);
            mSessionIDMap.put(b.getSessionID(),b);
        }
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's broker configurations.
     *
     * @return The configurations.
     */

    public SpringBrokers getSpringBrokers()
    {
        return mSpringBrokers;
    }

    /**
     * Returns the receiver's brokers.
     *
     * @return The brokers.
     */

    public List<Broker> getBrokers()
    {
        return mBrokers;
    }

    /**
     * Returns the status of the receiver's brokers.
     *
     * @return The status.
     */

    public BrokersStatus getStatus()
    {
        List<BrokerStatus> list=
            new ArrayList<BrokerStatus>(getBrokers().size());
        for (Broker b:getBrokers()) {
            list.add(b.getStatus());
        }
        return new BrokersStatus(list);
    }

    /**
     * Returns the configuration of the receiver's QuickFIX/J session
     * settings.
     *
     * @return The configuration.
     */

    public SpringSessionSettings getSettings()
    {
        return getSpringBrokers().getSettings();
    }

    /**
     * Returns the receiver's broker for the given QuickFIX/J session
     * ID. It logs an error and returns null if there is no broker for
     * the given ID.
     *
     * @param sessionID The ID.
     *
     * @return The broker. It may be null.
     */

    public Broker getBroker
        (SessionID sessionID)
    {
        Broker b=mSessionIDMap.get(sessionID);
        if (b==null) {
            Messages.INVALID_SESSION_ID.error(this,sessionID);
        }
        return b;
    }

    /**
     * Returns the receiver's broker for the given broker ID. It logs
     * an error and returns null if there is no broker for the given
     * ID.
     *
     * @param brokerID The ID.
     *
     * @return The broker. It may be null.
     */

    public Broker getBroker
        (BrokerID brokerID)
    {
        Broker b=mBrokerIDMap.get(brokerID);
        if (b==null) {
            Messages.INVALID_BROKER_ID.error(this,brokerID);
        }
        return b;
    }
}
