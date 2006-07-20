package org.marketcetera.symbology;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;

import java.util.Date;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class ExchangeTest extends TestCase
{
    public ExchangeTest(String inName)
    {
        super(inName);
    }

    public static Test suite()
    {
        return new MarketceteraTestSuite(ExchangeTest.class);

    }

    public void testFields()
    {
        Exchange anExchange = new Exchange("country", "cc","mic","inst","oth","city","web",new Date());
        assertEquals("country", anExchange.getCountry());
        assertEquals("cc", anExchange.getCountryCode());
        assertEquals("mic", anExchange.getMarketIdentifierCode());
        assertEquals("inst", anExchange.getInstitutionName());
        assertEquals("oth", anExchange.getOtherAcronym());
        assertEquals("city", anExchange.getCity());
        assertEquals("web", anExchange.getWebsite());
    }
}
