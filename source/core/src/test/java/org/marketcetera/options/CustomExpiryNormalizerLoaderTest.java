package org.marketcetera.options;

import org.marketcetera.util.misc.ClassVersion;
import static org.junit.Assert.assertEquals;

import java.net.URL;

/* $License$ */
/**
 * Verifies that the thread context class loader is not used in production
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class CustomExpiryNormalizerLoaderTest extends CustomExpiryNormalizerTestBase {
    @Override
    protected void doTest() throws Exception {
        //Don't tell the production code that this is a unit test

        //verify that the default normalization happens
        assertEquals("20091121", OptionUtils.normalizeEquityOptionExpiry("200911"));
        //verify that no events were generated in the log
        sLogAssist.assertNoEvents();
    }

    @Override
    protected URL createServicesFile() throws Exception {
        return createServicesFileFor(CustomExpiryNormalizer.class);
    }
}