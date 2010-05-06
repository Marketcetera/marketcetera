package org.marketcetera.photon;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;

import org.junit.Test;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.photon.BrokerManager.Broker;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Tests {@link BrokerManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class BrokerManagerTest {

    @Test
    public void testAvailableBrokers() {
        BrokerManager fixture = new BrokerManager();
        BrokerStatus status1 = new BrokerStatus("1", new BrokerID("1"), true);
        BrokerStatus status2 = new BrokerStatus("2", new BrokerID("2"), false);
        BrokerStatus status3 = new BrokerStatus("3", new BrokerID("3"), true);
        BrokersStatus statuses = new BrokersStatus(Arrays.asList(status1,
                status2, status3));
        fixture.setBrokersStatus(statuses);
        assertThat(fixture.getAvailableBrokers().size(), is(3));
        assertBroker((Broker) fixture.getAvailableBrokers().get(0),
                "Auto Select", null);
        assertBroker((Broker) fixture.getAvailableBrokers().get(1), status1);
        assertBroker((Broker) fixture.getAvailableBrokers().get(2), status3);
        status2 = new BrokerStatus("2", new BrokerID("2"), true);
        status3 = new BrokerStatus("3", new BrokerID("3"), false);
        statuses = new BrokersStatus(Arrays.asList(status1, status2, status3));
        fixture.setBrokersStatus(statuses);
        assertThat(fixture.getAvailableBrokers().size(), is(3));
        assertBroker((Broker) fixture.getAvailableBrokers().get(0),
                "Auto Select", null);
        assertBroker((Broker) fixture.getAvailableBrokers().get(1), status1);
        assertBroker((Broker) fixture.getAvailableBrokers().get(2), status2);
    }

    private void assertBroker(Broker broker, BrokerStatus status) {
        assertBroker(broker, status.getName(), status.getId());
    }

    private void assertBroker(Broker broker, String name, BrokerID id) {
        assertThat(broker.getName(), is(name));
        assertThat(broker.getId(), is(id));
    }
}
