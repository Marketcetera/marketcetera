package org.marketcetera.photon;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.OptionContractDataTest;
import org.marketcetera.photon.marketdata.OptionMessageHolderTest;
import org.marketcetera.photon.marketdata.mock.MockMarketDataFeed;
import org.marketcetera.photon.marketdata.mock.MockMarketDataFeedCredentials;
import org.marketcetera.photon.parser.LexerTest;
import org.marketcetera.photon.parser.OrderFormatterTest;
import org.marketcetera.photon.parser.ParserTest;
import org.marketcetera.photon.quickfix.QuickFIXTest;
import org.marketcetera.photon.scripting.ClasspathTest;
import org.marketcetera.photon.scripting.JRubyBSFTest;
import org.marketcetera.photon.scripting.ScriptChangesAdapterTest;
import org.marketcetera.photon.scripting.ScriptRegistryTest;
import org.marketcetera.photon.scripting.StrategyTest;
import org.marketcetera.photon.ui.databinding.FormTextObservableValueTest;
import org.marketcetera.photon.ui.databinding.HasValueConverterTest;
import org.marketcetera.photon.ui.databinding.IsNewOrderMessageConverterTest;
import org.marketcetera.photon.ui.databinding.LabelBooleanImageObservableValueTest;
import org.marketcetera.photon.ui.databinding.ObservableEventListTest;
import org.marketcetera.photon.ui.validation.DataDictionaryValidatorTest;
import org.marketcetera.photon.ui.validation.IgnoreFirstNullValidatorTest;
import org.marketcetera.photon.ui.validation.IntegerRequiredValidatorTest;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilderTest;
import org.marketcetera.photon.ui.validation.fix.PriceObservableValueTest;
import org.marketcetera.photon.ui.validation.fix.StringDateObservableValueTest;
import org.marketcetera.photon.views.AveragePricesViewTest;
import org.marketcetera.photon.views.FIXMessagesViewTest;
import org.marketcetera.photon.views.FillsViewTest;
import org.marketcetera.photon.views.MarketDataViewTest;
import org.marketcetera.photon.views.OptionDateHelperTest;
import org.marketcetera.photon.views.OptionOrderTicketControllerTest;
import org.marketcetera.photon.views.OptionOrderTicketModelTest;
import org.marketcetera.photon.views.OptionOrderTicketViewTest;
import org.marketcetera.photon.views.OptionOrderTicketXSWTTest;
import org.marketcetera.photon.views.SWTTestViewTest;
import org.marketcetera.photon.views.StockOrderTicketViewTest;
import org.marketcetera.photon.views.StockOrderTicketXSWTTest;
import org.marketcetera.photon.views.ViewTestBase;
import org.osgi.framework.BundleContext;

public class TS_Photon {
	public static Test suite() throws Exception {
		installMockDataFeed();
	
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
		suite.addTestSuite(OptionContractDataTest.class);
		suite.addTestSuite(OptionMessageHolderTest.class);

//		// model
//		suite.addTestSuite(GroupIDComparatorTest.class);
//		suite.addTest(FIXMessageHistoryTest.suite());
//		suite.addTestSuite(MessageHolderTest.class);
//		suite.addTestSuite(SymbolSideComparatorTest.class);
		
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
		
		suite.addTestSuite(IgnoreFirstNullValidatorTest.class);
		suite.addTestSuite(IntegerRequiredValidatorTest.class);
        // ui.validation.fix
		suite.addTestSuite(PriceConverterBuilderTest.class);
		suite.addTest(StringDateObservableValueTest.suite());
		suite.addTest(PriceObservableValueTest.suite());
		
		// ui.databinding
		suite.addTestSuite(HasValueConverterTest.class);
		suite.addTestSuite(ObservableEventListTest.class);
		suite.addTestSuite(FormTextObservableValueTest.class);
		suite.addTest(IsNewOrderMessageConverterTest.suite());
		suite.addTestSuite(LabelBooleanImageObservableValueTest.class);
		suite.addTest(DataDictionaryValidatorTest.suite());
		
		// views
		suite.addTestSuite(AveragePricesViewTest.class);
		suite.addTestSuite(FillsViewTest.class);
		suite.addTestSuite(FIXMessagesViewTest.class);
		suite.addTestSuite(MarketDataViewTest.class);
		suite.addTestSuite(StockOrderTicketViewTest.class);
		suite.addTestSuite(OptionOrderTicketViewTest.class);
		suite.addTestSuite(OptionDateHelperTest.class);
		suite.addTest(OptionOrderTicketModelTest.suite());
		suite.addTestSuite(OptionOrderTicketXSWTTest.class);
		suite.addTestSuite(StockOrderTicketXSWTTest.class);
		suite.addTestSuite(OptionOrderTicketControllerTest.class);
		suite.addTestSuite(SWTTestViewTest.class);
		
//		suite.addTest(new MarketDataViewTest("testShowQuote"));
		
		return suite;
	}

	private static void installMockDataFeed() throws FeedException, NoMoreIDsException {
		ViewTestBase.waitForJobs();
		
		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		MarketDataFeedTracker tracker = new MarketDataFeedTracker(bundleContext);
		tracker.open();

		// unregister existing feed
		MarketDataFeedService<?> service = (MarketDataFeedService<?>) tracker.getService();
		if (service != null){
			service.getServiceRegistration().unregister();
		}

		// register mock feed
		MarketDataFeedService<?> feedService = new MarketDataFeedService<MockMarketDataFeedCredentials>(new MockMarketDataFeed(), new MockMarketDataFeedCredentials(""));
		bundleContext.registerService(MarketDataFeedService.class.getName(), feedService, null);
		
	}

}
