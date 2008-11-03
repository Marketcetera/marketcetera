package org.marketcetera.photon.module;

import java.util.concurrent.TimeUnit;

import javax.management.JMX;
import javax.management.ObjectName;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleManagerMXBean;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.test.SWTTestUtil;

/* $License$ */

/**
 * Test {@link SinkConsoleController}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SinkConsoleControllerTest {

	private static ModuleManager moduleManager;

	@BeforeClass
	public static void setupOnce() {
		System.setProperty(AbstractMarketDataFeed.MARKETDATA_SIMULATION_KEY,
				"true");
		PropertiesTree properties = ModulePlugin.getDefault()
				.getModuleProperties();
		properties.put("mdata.marketcetera.single.URL",
				"FIX.4.4://exchange.marketcetera.com:7004");
		properties.put("mdata.marketcetera.single.SenderCompID", "sender");
		properties.put("mdata.marketcetera.single.TargetCompID", "MRKT-"
				+ System.nanoTime());
		ModulePlugin.getDefault().saveModuleProperties(properties);
		moduleManager = ModulePlugin.getDefault().getModuleManager();
	}

	@AfterClass
	public static void teardownOnce() {
		System.setProperty(AbstractMarketDataFeed.MARKETDATA_SIMULATION_KEY,
				"false");
	}

	/**
	 * Verifies that the sink console can be opened and a data flow can be set
	 * up without exceptions. No GUI validation is done at this point.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCSVDataFlow() throws Exception {
		new SinkConsoleController().openConsole();
		ModuleManagerMXBean mm = JMX.newMXBeanProxy(ModulePlugin.getDefault()
				.getMBeanServerConnection(), new ObjectName(
				ModuleManager.MODULE_MBEAN_NAME), ModuleManagerMXBean.class);
		final String request = "metc:csv:system:single;src/test/resources/table.csv";
		mm.createDataFlow(request);
		SWTTestUtil.delay(5, TimeUnit.SECONDS);
	}

	/**
	 * Verifies that the sink console can be opened and a data flow can be set
	 * up without exceptions. No GUI validation is done at this point.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBogusMarketDataFlow() throws Exception {
		org.marketcetera.marketdata.DataRequest request = MarketDataRequest
				.newFullBookRequest("GOOG");
		final DataFlowID flowID = moduleManager
				.createDataFlow(new DataRequest[] { new DataRequest(
						new ModuleURN("metc:mdata:bogus:single"), request
								.toString()) });
		SWTTestUtil.delay(5, TimeUnit.SECONDS);
		moduleManager.cancel(flowID);
	}

	/**
	 * Verifies that the sink console can be opened and a data flow can be set
	 * up without exceptions. No GUI validation is done at this point.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMarketDataFlow() throws Exception {
		org.marketcetera.marketdata.DataRequest request = MarketDataRequest
				.newFullBookRequest("GOOG");
		final DataFlowID flowID = moduleManager
				.createDataFlow(new DataRequest[] { new DataRequest(
						new ModuleURN("metc:mdata:marketcetera:single"),
						request.toString()) });
		SWTTestUtil.delay(5, TimeUnit.SECONDS);
		moduleManager.cancel(flowID);
	}

}
