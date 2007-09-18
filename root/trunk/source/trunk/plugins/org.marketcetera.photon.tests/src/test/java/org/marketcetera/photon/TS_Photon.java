package org.marketcetera.photon;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.marketdata.MarketceteraFeedTest;
import org.marketcetera.marketdata.MarketceteraOptionSymbolTest;
import org.marketcetera.photon.marketdata.OptionContractDataTest;
import org.marketcetera.photon.marketdata.OptionMessageHolderTest;
import org.marketcetera.photon.model.ClOrdIDComparatorTest;
import org.marketcetera.photon.model.FIXMessageHistoryTest;
import org.marketcetera.photon.model.MessageHolderTest;
import org.marketcetera.photon.model.SymbolSideComparatorTest;
import org.marketcetera.photon.parser.LexerTest;
import org.marketcetera.photon.parser.ParserTest;
import org.marketcetera.photon.quickfix.QuickFIXTest;
import org.marketcetera.photon.scripting.*;
import org.marketcetera.photon.ui.validation.fix.DateToStringCustomConverterTest;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilderTest;
import org.marketcetera.photon.ui.validation.fix.StringDateObservableValueTest;
import org.marketcetera.photon.views.*;

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
		
		// marketdata
		suite.addTestSuite(MarketceteraOptionSymbolTest.class);
		suite.addTestSuite(MarketceteraFeedTest.class);
		suite.addTestSuite(OptionContractDataTest.class);
		suite.addTestSuite(OptionMessageHolderTest.class);

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
		suite.addTest(StringDateObservableValueTest.suite());
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
