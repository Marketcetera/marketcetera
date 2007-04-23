package org.marketcetera.photon;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.marketcetera.bogusfeed.BogusFeedTest;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.photon.parser.LexerTest;
import org.marketcetera.photon.parser.ParserTest;
import org.marketcetera.photon.quickfix.QuickFIXTest;
import org.marketcetera.photon.scripting.ClasspathTest;
import org.marketcetera.photon.scripting.JRubyBSFTest;
import org.marketcetera.photon.scripting.ScriptChangesAdapterTest;
import org.marketcetera.photon.scripting.ScriptRegistryTest;
import org.marketcetera.photon.ui.validation.fix.DateToStringCustomConverterTest;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilderTest;
import org.marketcetera.photon.views.AveragePricesViewTest;
import org.marketcetera.photon.views.FIXMessagesViewTest;
import org.marketcetera.photon.views.FillsViewTest;
import org.marketcetera.photon.views.MarketDataViewTest;
import org.marketcetera.photon.views.StockOrderTicketViewTest;

public class TS_Photon {
	public static Test suite() {
		TestSuite suite = new WithRealmTestSuite(){

			@Override
			public void run(TestResult result) {
				PhotonPlugin.getDefault().getPhotonController().setIDFactory(new InMemoryIDFactory(21));
				super.run(result);
			}
			
		};
		suite.addTestSuite(StockOrderTicketViewTest.class);
		suite.addTestSuite(FIXMessagesViewTest.class);
		suite.addTestSuite(FillsViewTest.class);
		suite.addTestSuite(AveragePricesViewTest.class);
		suite.addTestSuite(ParserTest.class);
		suite.addTestSuite(LexerTest.class);
		suite.addTestSuite(ScriptRegistryTest.class);
		suite.addTestSuite(JRubyBSFTest.class);
		suite.addTestSuite(ClasspathTest.class);
		suite.addTestSuite(ScriptChangesAdapterTest.class);
		suite.addTestSuite(QuickFIXTest.class);
		suite.addTestSuite(MarketDataViewTest.class);
		suite.addTestSuite(BogusFeedTest.class);
		suite.addTestSuite(PriceConverterBuilderTest.class);
		suite.addTestSuite(DateToStringCustomConverterTest.class);
		return suite;
	}

}
