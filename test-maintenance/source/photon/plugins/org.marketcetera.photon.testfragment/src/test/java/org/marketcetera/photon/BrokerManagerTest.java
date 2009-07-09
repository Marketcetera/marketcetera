package org.marketcetera.photon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.test.DefaultRealm;
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
	
	private DefaultRealm realm;
    
    @Before
    public void setUp() throws Exception {
        realm = new DefaultRealm();
    }
    
    @After
    public void tearDown() throws Exception {
        realm.dispose();
    }
	
	@Test
	public void testAvailableBrokers() {
		BrokerManager fixture = new BrokerManager();
		BrokerStatus status1 = new BrokerStatus("1", new BrokerID("1"), true);
		BrokerStatus status2 = new BrokerStatus("2", new BrokerID("2"), false);
		BrokerStatus status3 = new BrokerStatus("3", new BrokerID("3"), true);
		BrokersStatus statuses =  new BrokersStatus(Arrays.asList(status1, status2, status3));
		fixture.setBrokersStatus(statuses);
		assertEquals(2, fixture.getAvailableBrokers().size());
		assertSame(status1, fixture.getAvailableBrokers().get(0));
		assertSame(status3, fixture.getAvailableBrokers().get(1));		
		status2 = new BrokerStatus("2", new BrokerID("2"), true);
		status3 = new BrokerStatus("3", new BrokerID("3"), false);
		statuses =  new BrokersStatus(Arrays.asList(status1, status2, status3));
		fixture.setBrokersStatus(statuses);
		assertEquals(2, fixture.getAvailableBrokers().size());
		assertSame(status1, fixture.getAvailableBrokers().get(0));
		assertSame(status2, fixture.getAvailableBrokers().get(1));	
	}
}
