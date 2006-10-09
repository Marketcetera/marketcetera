package org.marketcetera.photon.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_Photon {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TC_TestTest.class);
		return suite;
	}

}
