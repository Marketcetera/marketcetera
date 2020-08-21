package org.marketcetera.symbology;

import java.time.LocalDateTime;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
        Exchange anExchange = new Exchange("country", "cc","mic","inst","oth","city","web",LocalDateTime.now());
        assertEquals("country", anExchange.getCountry()); //$NON-NLS-1$
        assertEquals("cc", anExchange.getCountryCode()); //$NON-NLS-1$
        assertEquals("mic", anExchange.getMarketIdentifierCode()); //$NON-NLS-1$
        assertEquals("inst", anExchange.getInstitutionName()); //$NON-NLS-1$
        assertEquals("oth", anExchange.getOtherAcronym()); //$NON-NLS-1$
        assertEquals("city", anExchange.getCity()); //$NON-NLS-1$
        assertEquals("web", anExchange.getWebsite()); //$NON-NLS-1$
    }
}
