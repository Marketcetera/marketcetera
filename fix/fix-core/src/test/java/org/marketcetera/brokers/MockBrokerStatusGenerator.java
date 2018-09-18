package org.marketcetera.brokers;

import java.util.UUID;

import org.marketcetera.cluster.ClusterData;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.MutableFixSession;
import org.marketcetera.fix.impl.SimpleFixSession;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Generates test {@link BrokerStatus} values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockBrokerStatusGenerator
{
    /**
     * Generate a broker status value.
     *
     * @param inName a <code>String</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inIsLoggedOn a <code>boolean</code> value
     * @return a <code>ClusteredBrokerStatus</code> value
     */
    public static ClusteredBrokerStatus generateBrokerStatus(String inName,
                                                             BrokerID inBrokerId,
                                                             boolean inIsLoggedOn)
    {
        MutableFixSession fixSession = new SimpleFixSession();
        fixSession.setAffinity(1);
        fixSession.setBrokerId(inBrokerId.getValue());
        fixSession.setHost("localhost");
        fixSession.setIsAcceptor(false);
        fixSession.setIsEnabled(true);
        fixSession.setName(inName);
        fixSession.setPort(9000);
        fixSession.setSessionId("FIX.4.2:MATP->RECEIVER1");
        return new ClusteredBrokerStatus(fixSession,
                                         clusterData,
                                         inIsLoggedOn?FixSessionStatus.CONNECTED:FixSessionStatus.NOT_CONNECTED,
                                         inIsLoggedOn);
    }
    /**
     * generated cluster data
     */
    private static final ClusterData clusterData = new ClusterData(1,"host1",1,1,UUID.randomUUID().toString());
}
