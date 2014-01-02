package org.marketcetera.options;

import org.marketcetera.util.misc.ClassVersion;
import static org.junit.Assert.assertEquals;
import org.apache.log4j.Level;

import java.net.URL;

/* $License$ */
/**
 * Tests failure when loading a custom {@link OptionExpiryNormalizer}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class CustomExpiryNormalizerFailureTest extends CustomExpiryNormalizerTestBase {
    @Override
    protected void doTest() throws Exception {
        //Tell the production code that this is a unit test
        OptionUtils.setupForTest();
        //verify that the default normalization happens
        assertEquals("20091121", OptionUtils.normalizeEquityOptionExpiry("200911"));
        //verify that we failed to load the custom expiry normalizer.
        sLogAssist.assertLastEvent(Level.WARN, OptionUtils.class.getName(),
                Messages.LOG_ERROR_LOADING_OPTION_EXPIRY_NORMALIZER.getText(),
                null);
    }

    @Override
    protected URL createServicesFile() throws Exception {
        //returns a random class name to cause failure.
        return createServicesFileFor(getClass());
    }
}
