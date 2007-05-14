package org.marketcetera.photon.views;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.WithRealmTestSuite;

public class StockOrderTicketViewSuite {
	public static Test suite() {
		TestSuite suite = new WithRealmTestSuite(){

			@Override
			public void run(TestResult result) {
				PhotonPlugin.getDefault().getPhotonController().setIDFactory(new InMemoryIDFactory(21));
				super.run(result);
			}
			
		};
		suite.addTestSuite(StockOrderTicketViewTest.class);
		
		return suite;
	}
}
