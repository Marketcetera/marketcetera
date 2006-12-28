package org.marketcetera.photon;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.marketcetera.photon.parser.LexerTest;
import org.marketcetera.photon.parser.ParserTest;
import org.marketcetera.photon.scripting.ClasspathTest;
import org.marketcetera.photon.scripting.JRubyBSFTest;
import org.marketcetera.photon.scripting.ScriptChangesAdapterTest;
import org.marketcetera.photon.scripting.ScriptRegistryTest;
import org.marketcetera.photon.views.AveragePricesViewTest;
import org.marketcetera.photon.views.FIXMessagesViewTest;
import org.marketcetera.photon.views.FillsViewTest;
import org.marketcetera.photon.views.StockOrderTicketViewTest;

public class TS_Photon {
	public static Test suite() {
		TestSuite suite = new TestSuite();
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
		return suite;
	}

}
