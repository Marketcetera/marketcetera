package org.marketcetera.photon.internal.module.ui;

import java.util.concurrent.TimeUnit;

import javax.management.JMX;
import javax.management.ObjectName;

import org.junit.Test;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleManagerMXBean;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.test.SWTTestUtil;

/* $License$ */

/**
 * Test {@link SinkConsoleController}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class SinkConsoleControllerTest {

	/**
	 * Verifies that the sink console can be opened and a data flow can be set
	 * up without exceptions.  No GUI validation is done at this point.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCSVDataFlow() throws Exception {
		ModuleManagerMXBean mm = JMX.newMXBeanProxy(ModuleSupport
				.getMBeanServerConnection(), new ObjectName(
				ModuleManager.MODULE_MBEAN_NAME), ModuleManagerMXBean.class);
		final String request = "metc:csv:system:single;src/test/resources/table.csv";
		mm.createDataFlow(request);
		SWTTestUtil.delay(2, TimeUnit.SECONDS);
	}

}
