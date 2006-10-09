package org.marketcetera.photon.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.marketcetera.photon.tests.TS_Photon;

public class TS_PhotonAllUnitTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("RDT all unit tests");
		//$JUnit-BEGIN$

		// org.marketcetera.photon.tests
		suite.addTest(TS_Photon.suite());
		
		//$JUnit-END$
		return suite;
	}
}
