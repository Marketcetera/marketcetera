package org.marketcetera.test.fix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.service.BrokerServiceImpl;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.IntegrationTestBase;
import org.marketcetera.trade.BrokerID;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import quickfix.SessionID;

/* $License$ */

/**
 * Tests {@link BrokerServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BrokerServiceTest
        extends IntegrationTestBase
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        initiators = Maps.newHashMap();
        acceptors = Maps.newHashMap();
        for(Broker broker : brokerService.getBrokers()) {
            if(broker.getFixSession().isAcceptor()) {
                acceptors.put(broker.getBrokerId(),
                              broker);
            } else {
                initiators.put(broker.getBrokerId(),
                               broker);
            }
        }
    }
    /**
     * Test the ability to get brokers or a single broker.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetBrokers()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                brokerService.getBroker((SessionID)null);
            }
        };
        assertNull(brokerService.getBroker(new SessionID(FIXVersion.FIX42.getVersion(),
                                                         "not-a-sender",
                                                         "not-a-target")));
        Collection<Broker> brokers = brokerService.getBrokers();
        assertEquals(4,
                     brokers.size());
        Broker sampleBroker = brokers.iterator().next();
        Broker sampleBrokerCopy = brokerService.getBroker(new SessionID(sampleBroker.getFixSession().getSessionId()));
        assertEquals(sampleBroker.getBrokerId(),
                     sampleBrokerCopy.getBrokerId());
    }
    /**
     * Test the ability to resolve sessions into name aliases.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBrokerServiceName()
            throws Exception
    {
        List<FixSession> sessions = brokerService.findFixSessions();
        for(FixSession session : sessions) {
            assertEquals(session.getName(),
                         sessionNameProvider.getSessionName(new SessionID(session.getSessionId())));
        }
    }
    /**
     * provides access to session names
     */
    @Autowired
    private SessionNameProvider sessionNameProvider;
    /**
     * initiator brokers
     */
    private Map<BrokerID,Broker> initiators;
    /**
     * acceptor brokers
     */
    private Map<BrokerID,Broker> acceptors;
    /**
     * test broker service
     */
    @Autowired
    private BrokerServiceImpl brokerService;
}
