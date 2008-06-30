package org.marketcetera.symbology;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class ExchangesTest extends TestCase {
    public ExchangesTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(ExchangesTest.class);
    }

    public void testStandardExchanges() throws Exception {
        exchangeHelper(Exchanges.AMEX, "XASE", "US");
        exchangeHelper(Exchanges.BOSTON, "XBOS", "US");
        exchangeHelper(Exchanges.CINCINNATI, "XCIS", "US");
        exchangeHelper(Exchanges.ISE, "XISX", "US");
        exchangeHelper(Exchanges.CHICAGO, "XCHI", "US");
        exchangeHelper(Exchanges.NYSE, "XNYS", "US");
        exchangeHelper(Exchanges.ARCA, "XARC", "US");
        exchangeHelper(Exchanges.NASDAQ, "XNAS", "US");
        exchangeHelper(Exchanges.PHILADELPHIA, "XPHL", "US");
    }

    public void exchangeHelper(Exchange anExchange, String MIC, String countryCode){
        assertEquals(MIC, anExchange.getMarketIdentifierCode());
        assertEquals(countryCode, anExchange.getCountryCode());
    }

}
