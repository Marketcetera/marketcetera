package org.marketcetera.photon;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.marketdata.MarketceteraOptionSymbolTest;
import org.marketcetera.photon.model.ClOrdIDComparatorTest;
import org.marketcetera.photon.model.FIXMessageHistoryTest;
import org.marketcetera.photon.model.MessageHolderTest;
import org.marketcetera.photon.model.SymbolSideComparatorTest;
import org.marketcetera.photon.parser.LexerTest;
import org.marketcetera.photon.parser.ParserTest;
import org.marketcetera.photon.quickfix.QuickFIXTest;
import org.marketcetera.photon.scripting.ClasspathTest;
import org.marketcetera.photon.scripting.JRubyBSFTest;
import org.marketcetera.photon.scripting.ScriptChangesAdapterTest;
import org.marketcetera.photon.scripting.ScriptRegistryTest;
import org.marketcetera.photon.scripting.StrategyTest;
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
		
		// photon
		suite.addTestSuite(OrderManagerTest.class);
		
		// marketdata
		suite.addTestSuite(MarketceteraOptionSymbolTest.class);

		// model
		suite.addTestSuite(ClOrdIDComparatorTest.class);
		suite.addTest(FIXMessageHistoryTest.suite());
		suite.addTestSuite(MessageHolderTest.class);
		suite.addTestSuite(SymbolSideComparatorTest.class);
		
		//parser
		suite.addTest(ParserTest.suite());
		suite.addTestSuite(LexerTest.class);

		// quickfix
		suite.addTestSuite(QuickFIXTest.class);


		//scripting
		suite.addTestSuite(ClasspathTest.class);
		suite.addTestSuite(JRubyBSFTest.class);
		suite.addTestSuite(ScriptChangesAdapterTest.class);
		suite.addTestSuite(ScriptRegistryTest.class);
		suite.addTestSuite(StrategyTest.class);
		
		// ui.validation.fix
		suite.addTestSuite(DateToStringCustomConverterTest.class);
		suite.addTestSuite(PriceConverterBuilderTest.class);

		// views
		suite.addTestSuite(AveragePricesViewTest.class);
		suite.addTestSuite(FillsViewTest.class);
		suite.addTestSuite(FIXMessagesViewTest.class);
		suite.addTestSuite(MarketDataViewTest.class);
		suite.addTestSuite(StockOrderTicketViewTest.class);

		return suite;
	}

}
