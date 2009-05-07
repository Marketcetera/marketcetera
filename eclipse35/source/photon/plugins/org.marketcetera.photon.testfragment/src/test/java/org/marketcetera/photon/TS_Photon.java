package org.marketcetera.photon;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.marketcetera.photon.actions.BrokerNotificationListenerTest;
import org.marketcetera.photon.marketdata.OptionContractDataTest;
import org.marketcetera.photon.marketdata.OptionMessageHolderTest;
import org.marketcetera.photon.parser.LexerTest;
import org.marketcetera.photon.parser.OrderFormatterTest;
import org.marketcetera.photon.parser.ParserTest;
import org.marketcetera.photon.quickfix.QuickFIXTest;
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
import org.marketcetera.photon.views.MarketDataViewItemTest;
import org.marketcetera.photon.views.OpenOrdersViewTest;
import org.marketcetera.photon.views.OptionDateHelperTest;
import org.marketcetera.photon.views.OptionOrderTicketModelTest;
import org.marketcetera.photon.views.OptionOrderTicketViewTest;
import org.marketcetera.photon.views.OptionOrderTicketXSWTTest;
import org.marketcetera.photon.views.SWTTestViewTest;
import org.marketcetera.photon.views.StockOrderTicketViewTest;
import org.marketcetera.photon.views.StockOrderTicketXSWTTest;

public class TS_Photon {
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite() {

			@Override
			public void run(TestResult result) {
				// Running this suite in Photon with logging causes a the process to hang when the
				// Photon Console fills up. This is a temporary workaround, see EG-153 for details.
				BasicConfigurator.resetConfiguration();
				Logger.getRootLogger().setLevel(Level.OFF);
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

		// parser
		suite.addTest(ParserTest.suite());
		suite.addTestSuite(LexerTest.class);
		suite.addTest(OrderFormatterTest.suite());

		// quickfix
		suite.addTestSuite(QuickFIXTest.class);

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
		suite.addTestSuite(StockOrderTicketViewTest.class);
		suite.addTestSuite(OptionOrderTicketViewTest.class);
		suite.addTestSuite(OptionDateHelperTest.class);
		suite.addTest(OptionOrderTicketModelTest.suite());
		suite.addTestSuite(OptionOrderTicketXSWTTest.class);
		suite.addTestSuite(StockOrderTicketXSWTTest.class);
		suite.addTestSuite(SWTTestViewTest.class);
		suite.addTestSuite(OpenOrdersViewTest.class);

		suite.addTest(new JUnit4TestAdapter(MarketDataViewItemTest.class));

		suite.addTest(new JUnit4TestAdapter(BrokerNotificationListenerTest.class));

		suite.addTest(new JUnit4TestAdapter(MessagesTest.class));
		suite.addTest(new JUnit4TestAdapter(TimeOfDayTest.class));
		
		suite.addTest(new JUnit4TestAdapter(PhotonPositionMarketDataTest.class));
		suite.addTest(new JUnit4TestAdapter(PhotonPositionMarketDataConcurrencyTest.class));

		return suite;
	}
}
