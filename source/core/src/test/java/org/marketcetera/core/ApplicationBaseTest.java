package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;
import org.jcyclone.core.cfg.JCycloneConfig;
import org.jcyclone.core.cfg.MapConfig;

import java.util.Properties;

/**
 * Test of some useful functions in the top-level {@link ApplicationBase} class.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class ApplicationBaseTest extends TestCase {
    public ApplicationBaseTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(ApplicationBaseTest.class);
    }


    /** Verifies that the config file that is being parsed for jcyclone config
     * is being read correctly
     * @throws Exception
     */
    public void testGenerateJCycloneConfig() throws Exception {
        Properties props = ConfigPropertiesLoader.loadProperties("jcyclone");
        MapConfig jCycloneConfig = ApplicationBase.generateJCycloneConfig(props);
        assertEquals(2, jCycloneConfig.getStageNames().length);
        assertEquals(1, jCycloneConfig.getPluginNames().length);

        assertEquals("default thread manager not set correctly",
                JCycloneConfig.THREADMGR_TPSTM_CONCURRENT,
                jCycloneConfig.getString(ApplicationBase.JCYCLONE_DFLT_THREAD_MGR_FIELD_NAME));

        // now preset the default threadMgr
        props = ConfigPropertiesLoader.loadProperties("jcyclone");
        props.setProperty(ApplicationBase.JCYCLONE_PREFIX+ApplicationBase.JCYCLONE_DFLT_THREAD_MGR_FIELD_NAME, "bob");
        jCycloneConfig = ApplicationBase.generateJCycloneConfig(props);

        assertEquals("default threadMgr if specified is lost",
                "bob", jCycloneConfig.getString(ApplicationBase.JCYCLONE_DFLT_THREAD_MGR_FIELD_NAME));
    }
}
