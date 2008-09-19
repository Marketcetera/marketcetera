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
        Exchange anExchange = new Exchange("country", "cc","mic","inst","oth","city","web",new Date()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        assertEquals("country", anExchange.getCountry()); //$NON-NLS-1$
        assertEquals("cc", anExchange.getCountryCode()); //$NON-NLS-1$
        assertEquals("mic", anExchange.getMarketIdentifierCode()); //$NON-NLS-1$
        assertEquals("inst", anExchange.getInstitutionName()); //$NON-NLS-1$
        assertEquals("oth", anExchange.getOtherAcronym()); //$NON-NLS-1$
        assertEquals("city", anExchange.getCity()); //$NON-NLS-1$
        assertEquals("web", anExchange.getWebsite()); //$NON-NLS-1$
    }
}
