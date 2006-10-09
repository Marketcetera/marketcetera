package org.marketcetera.photon.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.marketcetera.photon.tests.TS_Photon;


public class TS_PhotonAllFunctionalTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Photon all functional tests");
		//$JUnit-BEGIN$

		// org.marketcetera.photon functional tests.
		suite.addTest(TS_Photon.suite());
		
		//$JUnit-END$
		return suite;
	}
}
