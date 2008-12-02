package org.marketcetera.photon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.client.dest.DestinationsStatus;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.test.DefaultRealm;
import org.marketcetera.trade.DestinationID;


/* $License$ */

/**
 * Tests {@link BrokerManager}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
		DestinationStatus status1 = new DestinationStatus("1", new DestinationID("1"), true);
		DestinationStatus status2 = new DestinationStatus("2", new DestinationID("2"), false);
		DestinationStatus status3 = new DestinationStatus("3", new DestinationID("3"), true);
		DestinationsStatus statuses =  new DestinationsStatus(Arrays.asList(status1, status2, status3));
		fixture.setBrokersStatus(statuses);
		assertEquals(2, fixture.getAvailableBrokers().size());
		assertSame(status1, fixture.getAvailableBrokers().get(0));
		assertSame(status3, fixture.getAvailableBrokers().get(1));		
		status2.setLoggedOn(true);
		status3.setLoggedOn(false);
		fixture.setBrokersStatus(statuses);
		assertEquals(2, fixture.getAvailableBrokers().size());
		assertSame(status1, fixture.getAvailableBrokers().get(0));
		assertSame(status2, fixture.getAvailableBrokers().get(1));	
	}
}
