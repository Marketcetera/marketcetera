package org.marketcetera.options;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests loading of a custom {@link OptionExpiryNormalizer}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class CustomExpiryNormalizerTest extends CustomExpiryNormalizerTestBase {

    @Override
    protected void doTest() throws Exception {
        //Tell the production code that this is a unit test
        OptionUtils.setupForTest();
        assertEquals("20091001", OptionUtils.normalizeEquityOptionExpiry("200910"));
        assertEquals("20091010", OptionUtils.normalizeEquityOptionExpiry("20091010"));
        assertEquals("2009", OptionUtils.normalizeEquityOptionExpiry("2009"));
    }

    @Override
    protected URL createServicesFile() throws Exception {
        return createServicesFileFor(CustomExpiryNormalizer.class);
    }
}
