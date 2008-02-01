package org.marketcetera.photon;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.marketdata.MarketceteraFeedTest;
import org.marketcetera.marketdata.MarketceteraOptionSymbolTest;
import org.marketcetera.messagehistory.FIXMessageHistoryTest;
import org.marketcetera.messagehistory.GroupIDComparatorTest;
import org.marketcetera.messagehistory.MessageHolderTest;
import org.marketcetera.messagehistory.SymbolSideComparatorTest;
import org.marketcetera.photon.marketdata.OptionContractDataTest;
import org.marketcetera.photon.marketdata.OptionMessageHolderTest;
import org.marketcetera.photon.parser.LexerTest;
import org.marketcetera.photon.parser.OrderFormatterTest;
import org.marketcetera.photon.parser.ParserTest;
import org.marketcetera.photon.quickfix.QuickFIXTest;
import org.marketcetera.photon.scripting.ClasspathTest;
import org.marketcetera.photon.scripting.JRubyBSFTest;
import org.marketcetera.photon.scripting.ScriptChangesAdapterTest;
import org.marketcetera.photon.scripting.ScriptRegistryTest;
import org.marketcetera.photon.scripting.StrategyTest;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilderTest;
import org.marketcetera.photon.ui.validation.fix.PriceObservableValueTest;
import org.marketcetera.photon.ui.validation.fix.StringDateObservableValueTest;
import org.marketcetera.photon.ui.FIXMessageTableFormatTest;
import org.marketcetera.photon.views.AveragePricesViewTest;
import org.marketcetera.photon.views.FIXMessagesViewTest;
import org.marketcetera.photon.views.FillsViewTest;
import org.marketcetera.photon.views.MarketDataViewTest;
import org.marketcetera.photon.views.OptionDateHelperTest;
import org.marketcetera.photon.views.OptionOrderTicketViewTest;
import org.marketcetera.photon.views.OptionSeriesCollectionTest;
import org.marketcetera.photon.views.StockOrderTicketViewTest;

public class TS_Photon {
	public static Test suite() {
		TestSuite suite = new TestSuite(){

			@Override
			public void run(TestResult result) {
				PhotonPlugin.getDefault().getPhotonController().setIDFactory(new InMemoryIDFactory(21));
				super.run(result);
			}
			
		};
		
		// photon
		suite.addTestSuite(OrderManagerTest.class);
		suite.addTestSuite(PhotonControllerTest.class);
		suite.addTestSuite(FIXFieldLocalizerTest.class);
		
		// marketdata
		suite.addTestSuite(MarketceteraOptionSymbolTest.class);
		suite.addTestSuite(MarketceteraFeedTest.class);
		suite.addTestSuite(OptionContractDataTest.class);
		suite.addTestSuite(OptionMessageHolderTest.class);

		// model
		suite.addTestSuite(GroupIDComparatorTest.class);
		suite.addTest(FIXMessageHistoryTest.suite());
		suite.addTestSuite(MessageHolderTest.class);
		suite.addTestSuite(SymbolSideComparatorTest.class);
		
		//parser
		suite.addTest(ParserTest.suite());
		suite.addTestSuite(LexerTest.class);
		suite.addTest(OrderFormatterTest.suite());

		// quickfix
		suite.addTestSuite(QuickFIXTest.class);


		//scripting
		suite.addTestSuite(ClasspathTest.class);
		suite.addTestSuite(JRubyBSFTest.class);
		suite.addTestSuite(ScriptChangesAdapterTest.class);
		suite.addTestSuite(ScriptRegistryTest.class);
		suite.addTestSuite(StrategyTest.class);
		
		// ui.
        suite.addTestSuite(FIXMessageTableFormatTest.class);

        // ui.validation.fix
		suite.addTestSuite(PriceConverterBuilderTest.class);
		suite.addTest(StringDateObservableValueTest.suite());
		suite.addTest(PriceObservableValueTest.suite());
		
		// views
		suite.addTestSuite(AveragePricesViewTest.class);
		suite.addTestSuite(FillsViewTest.class);
		suite.addTestSuite(FIXMessagesViewTest.class);
		suite.addTestSuite(MarketDataViewTest.class);
		suite.addTestSuite(StockOrderTicketViewTest.class);
		suite.addTestSuite(OptionOrderTicketViewTest.class);
		suite.addTestSuite(OptionDateHelperTest.class);
		suite.addTestSuite(OptionSeriesCollectionTest.class);

		return suite;
	}

}
