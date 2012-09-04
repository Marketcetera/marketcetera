package org.marketcetera.core.options;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/* $License$ */
/**
 * Tests loading of a custom {@link org.marketcetera.core.options.OptionExpiryNormalizer}.
 *
 * @version $Id: CustomExpiryNormalizerTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class CustomExpiryNormalizerTest
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void setupLog()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Tests that a custom normalizer can be used.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCustomNormalizer()
            throws Exception
    {
        // baseline test
        OptionUtils utils = new OptionUtils();
        String[] expiries = new String[] { "200910", "200911" };
        String[] expectedUsExpiry = new String[] { "20091017", "20091121" };
        int counter = 0;
        for(String expiry : expiries) {
            String usExpiry = utils.normalizeUSEquityOptionExpiry(expiry);
            String customExpiry = utils.normalizeEquityOptionExpiry(expiry);
            assertEquals(usExpiry,
                         customExpiry);
            assertEquals(expectedUsExpiry[counter++],
                         customExpiry);
        }
        counter = 0;
        utils.setNormalizer(new CustomExpiryNormalizer());
        String[] expectedCustomExpiry = new String[] { "20091001", "20091101" };
        // repeat the test
        for(String expiry : expiries) {
            String usExpiry = utils.normalizeUSEquityOptionExpiry(expiry);
            String customExpiry = utils.normalizeEquityOptionExpiry(expiry);
            assertFalse(usExpiry.equals(customExpiry));
            assertEquals(expectedCustomExpiry[counter++],
                         customExpiry);
        }
    }
}
