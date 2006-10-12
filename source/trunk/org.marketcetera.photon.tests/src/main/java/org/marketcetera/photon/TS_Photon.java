package org.marketcetera.photon;

import org.marketcetera.photon.views.FIXMessagesViewTest;
import org.marketcetera.photon.views.StockOrderTicketViewTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_Photon {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(StockOrderTicketViewTest.class);
		suite.addTestSuite(FIXMessagesViewTest.class);
		return suite;
	}

}
