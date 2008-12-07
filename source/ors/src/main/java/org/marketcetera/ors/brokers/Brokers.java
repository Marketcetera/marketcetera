package org.marketcetera.ors.brokers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.client.dest.DestinationsStatus;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import quickfix.SessionID;

/**
 * The collective in-memory representation of all destinations.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class Brokers
{

    // INSTANCE DATA.

    private final SpringBrokers mSpringBrokers;
    private final List<Broker> mDestinations;
    private final Map<DestinationID,Broker> mDestinationIDMap;
    private final Map<SessionID,Broker> mSessionIDMap;


    // CONSTRUCTORS.

    /**
     * Creates a new collective representation based on the given
     * destination configurations. Any message modifiers are
     * configured to rely on the given report history services
     * provider for persistence operations.
     *
     * @param springBrokers The configurations.
     * @param historyServices The report history services provider.
     */

    public Brokers
        (SpringBrokers springBrokers,
         ReportHistoryServices historyServices)
    {
        mSpringBrokers=springBrokers;
        int capacity=getSpringBrokers().getDestinations().size();
        mDestinations=new ArrayList<Broker>(capacity);
        mDestinationIDMap=new HashMap<DestinationID,Broker>(capacity);
        mSessionIDMap=new HashMap<SessionID,Broker>(capacity);
        for (SpringBroker sd:getSpringBrokers().getDestinations()) {
            Broker d=new Broker(sd,historyServices);
            mDestinations.add(d);
            mDestinationIDMap.put(d.getDestinationID(),d);
            mSessionIDMap.put(d.getSessionID(),d);
        }
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's destination configurations.
     *
     * @return The configurations.
     */

    public SpringBrokers getSpringBrokers()
    {
        return mSpringBrokers;
    }

    /**
     * Returns the receiver's destinations.
     *
     * @return The destinations.
     */

    public List<Broker> getDestinations()
    {
        return mDestinations;
    }

    /**
     * Returns the status of the receiver's destinations.
     *
     * @return The status.
     */

    public DestinationsStatus getStatus()
    {
        List<DestinationStatus> list=
            new ArrayList<DestinationStatus>(getDestinations().size());
        for (Broker d:getDestinations()) {
            list.add(d.getStatus());
        }
        return new DestinationsStatus(list);
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
     * Returns the receiver's destination for the given QuickFIX/J
     * session ID. It logs an error and returns null if there is no
     * destination for the given ID.
     *
     * @param sessionID The ID.
     *
     * @return The destination. It may be null.
     */

    public Broker getDestination
        (SessionID sessionID)
    {
        Broker d=mSessionIDMap.get(sessionID);
        if (d==null) {
            Messages.INVALID_SESSION_ID.error(this,sessionID);
        }
        return d;
    }

    /**
     * Returns the receiver's destination for the given destination
     * ID. It logs an error and returns null if there is no
     * destination for the given ID.
     *
     * @param destinationID The ID.
     *
     * @return The destination. It may be null.
     */

    public Broker getDestination
        (DestinationID destinationID)
    {
        Broker d=mDestinationIDMap.get(destinationID);
        if (d==null) {
            Messages.INVALID_BROKER_ID.error(this,destinationID);
        }
        return d;
    }
}
