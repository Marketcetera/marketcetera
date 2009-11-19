package org.marketcetera.options;

import org.marketcetera.util.misc.ClassVersion;
import static org.junit.Assert.assertEquals;
import org.apache.log4j.Level;

import java.net.URL;

/* $License$ */
/**
 * Tests loading of a custom {@link OptionExpiryNormalizer}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
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
        sLogAssist.assertLastEvent(Level.INFO, OptionUtils.class.getName(),
                Messages.LOG_OPTION_EXPIRY_NORMALIZER_CUSTOMIZED.getText(
                        CustomExpiryNormalizer.class.getName()), null);
    }

    @Override
    protected URL createServicesFile() throws Exception {
        return createServicesFileFor(CustomExpiryNormalizer.class);
    }
}
