package org.marketcetera.photon.tests;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.marketcetera.photon.FIXFieldLocalizerTest;
import org.marketcetera.photon.OrderManagerTest;
import org.marketcetera.photon.PhotonControllerTest;
import org.marketcetera.photon.StrategyClasspathTest;
import org.marketcetera.photon.parser.LexerTest;
import org.marketcetera.photon.parser.OrderFormatterTest;
import org.marketcetera.photon.parser.ParserTest;
import org.marketcetera.photon.quickfix.QuickFIXTest;
import org.marketcetera.photon.ui.databinding.FormTextObservableValueTest;
import org.marketcetera.photon.ui.databinding.HasValueConverterTest;
import org.marketcetera.photon.ui.databinding.IsNewOrderMessageConverterTest;
import org.marketcetera.photon.ui.databinding.LabelBooleanImageObservableValueTest;
import org.marketcetera.photon.ui.databinding.ObservableEventListTest;
import org.marketcetera.photon.ui.marketdata.OptionContractDataTest;
import org.marketcetera.photon.ui.marketdata.OptionMessageHolderTest;
import org.marketcetera.photon.ui.validation.DataDictionaryValidatorTest;
import org.marketcetera.photon.ui.validation.IgnoreFirstNullValidatorTest;
import org.marketcetera.photon.ui.validation.IntegerRequiredValidatorTest;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilderTest;
import org.marketcetera.photon.ui.validation.fix.PriceObservableValueTest;
import org.marketcetera.photon.ui.validation.fix.StringDateObservableValueTest;
import org.marketcetera.photon.views.AveragePricesViewTest;
import org.marketcetera.photon.views.FIXMessagesViewTest;
import org.marketcetera.photon.views.FillsViewTest;
import org.marketcetera.photon.views.OpenOrdersViewTest;
import org.marketcetera.photon.views.OptionDateHelperTest;
import org.marketcetera.photon.views.OptionOrderTicketModelTest;
import org.marketcetera.photon.views.OptionOrderTicketViewTest;
import org.marketcetera.photon.views.OptionOrderTicketXSWTTest;
import org.marketcetera.photon.views.SWTTestViewTest;
import org.marketcetera.photon.views.StockOrderTicketViewTest;
import org.marketcetera.photon.views.StockOrderTicketXSWTTest;

/* $License$ */

/**
 * Photon UI tests that require the entire application to run. Most tests are
 * legacy JUnit 3 tests to the JUnit 3 style suite() method is used in addition
 * to a custom runner.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(PhotonRunner.class)
public class PhotonApplicationSuite {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite(PhotonApplicationSuite.class.getName());

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

        // this JUnit 4 test is run here with the full Photon application
        suite.addTest(new JUnit4TestAdapter(StrategyClasspathTest.class));

        return suite;
    }
}
