package org.marketcetera.photon.tests;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.marketcetera.photon.FIXFieldLocalizerTest;
import org.marketcetera.photon.OrderManagerTest;
import org.marketcetera.photon.PhotonControllerTest;
import org.marketcetera.photon.StrategyClasspathTest;
import org.marketcetera.photon.quickfix.QuickFIXTest;
import org.marketcetera.photon.views.AveragePricesViewTest;
import org.marketcetera.photon.views.FIXMessagesViewTest;
import org.marketcetera.photon.views.FillsViewTest;
import org.marketcetera.photon.views.OpenOrdersViewTest;
import org.marketcetera.photon.views.OptionOrderTicketXSWTTest;
import org.marketcetera.photon.views.StockOrderTicketXSWTTest;

/* $License$ */

/**
 * Photon UI tests that require the entire application to run. Most tests are
 * legacy JUnit 3 tests to the JUnit 3 style suite() method is used in addition
 * to a custom runner.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(PhotonRunner.class)
public class PhotonApplicationSuite {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite(PhotonApplicationSuite.class.getName());

        // photon
        suite.addTestSuite(OrderManagerTest.class);
        suite.addTestSuite(PhotonControllerTest.class);
        suite.addTestSuite(FIXFieldLocalizerTest.class);

        // quickfix
        suite.addTestSuite(QuickFIXTest.class);

        // views
        suite.addTestSuite(AveragePricesViewTest.class);
        suite.addTestSuite(FillsViewTest.class);
        suite.addTestSuite(FIXMessagesViewTest.class);
        suite.addTestSuite(OptionOrderTicketXSWTTest.class);
        suite.addTestSuite(StockOrderTicketXSWTTest.class);
        suite.addTestSuite(OpenOrdersViewTest.class);

        // this JUnit 4 test is run here with the full Photon application
        suite.addTest(new JUnit4TestAdapter(StrategyClasspathTest.class));

        return suite;
    }
}
