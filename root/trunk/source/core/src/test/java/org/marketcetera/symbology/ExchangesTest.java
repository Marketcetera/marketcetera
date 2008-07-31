package org.marketcetera.symbology;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ExchangesTest extends TestCase {
    public ExchangesTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(ExchangesTest.class);
    }

    public void testStandardExchanges() throws Exception {
        exchangeHelper(Exchanges.AMEX, "XASE", "US"); //$NON-NLS-1$ //$NON-NLS-2$
        exchangeHelper(Exchanges.BOSTON, "XBOS", "US"); //$NON-NLS-1$ //$NON-NLS-2$
        exchangeHelper(Exchanges.CINCINNATI, "XCIS", "US"); //$NON-NLS-1$ //$NON-NLS-2$
        exchangeHelper(Exchanges.ISE, "XISX", "US"); //$NON-NLS-1$ //$NON-NLS-2$
        exchangeHelper(Exchanges.CHICAGO, "XCHI", "US"); //$NON-NLS-1$ //$NON-NLS-2$
        exchangeHelper(Exchanges.NYSE, "XNYS", "US"); //$NON-NLS-1$ //$NON-NLS-2$
        exchangeHelper(Exchanges.ARCA, "XARC", "US"); //$NON-NLS-1$ //$NON-NLS-2$
        exchangeHelper(Exchanges.NASDAQ, "XNAS", "US"); //$NON-NLS-1$ //$NON-NLS-2$
        exchangeHelper(Exchanges.PHILADELPHIA, "XPHL", "US"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void exchangeHelper(Exchange anExchange, String MIC, String countryCode){
        assertEquals(MIC, anExchange.getMarketIdentifierCode());
        assertEquals(countryCode, anExchange.getCountryCode());
    }

}
