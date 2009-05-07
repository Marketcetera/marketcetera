package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/* $License$ */
/**
 * Tests {@link ApplicationVersion}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ApplicationVersionTest {
    @Test
    public void basic() throws Exception {
        assertEquals(ApplicationVersion.DEFAULT_VERSION,
                ApplicationVersion.getVersion());
        assertEquals(ApplicationVersion.DEFAULT_BUILD,
                ApplicationVersion.getBuildNumber());
    }
}
