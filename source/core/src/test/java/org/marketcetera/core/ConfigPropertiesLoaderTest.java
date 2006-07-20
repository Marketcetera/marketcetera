package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Sanity-check test.
 * Verify that the way we look up resources while loading configs is correct.
 * Basically, in the maven project.xml file we specify which files are resources
 * (in this case it's unit-test related resources).
 * Then we try to load them up as a bundle.
 *
 * NOTE: in IntelliJ, this test will keep failing until you add the parent
 * (ie target/test-classes) dir where the resources subdir lives to the classpath.
 * Keep in mind that if you are adding the test-classes/ dir than it may have the old
 * class files too.
 * You may need to run maven test:compile target outside of intellij to have the
 * resources be copied to appropriate locations
 *
 * This is an elaborate testbed for figuring out how classloaders work and how they find resources.
 * Basically, if the resource lives in the top-level of the classpath (ie in classes/ directory where the
 * org/marketcetera/bla lives) then you have to access the resource with a "/" prepended to it.
 * Otherwise, the classloader will look for it in the path where the class lives if you use
 * this.getClass().getResourceAsStream()
 * The sytem classloaer doesnt seem to load anything at all.
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class ConfigPropertiesLoaderTest extends TestCase
{
    public static final String CONFIG_NAME = "configLoading";

    public ConfigPropertiesLoaderTest(String inName)
    {
        super(inName);
    }

    public static Test suite()
    {
        return new MarketceteraTestSuite(ConfigPropertiesLoaderTest.class);
    }

    public void testLoadBundle() throws Exception
    {
        assertNotNull(ResourceBundle.getBundle(CONFIG_NAME));
    }

    public void testLoadAsStreamWihSuffix() throws Exception
    {
        assertNotNull(this.getClass().getResourceAsStream("/"+CONFIG_NAME+".properties"));
    }

    public void testLoadAsStreamNotPropertiesFile() throws Exception
    {
        assertNotNull(this.getClass().getResourceAsStream("/"+CONFIG_NAME+".notProperties"));
    }

    /** Values are:
            fix.sendercompid=MKTC
            fix.targetcompid=ARCA
            jms.connection.factory=connectionFactory
            jms.context.factory=com.ubermq.jms.client.JMSInitialContextFactory
            jms.incoming.queue=queue:oms-commands

            # db connection info
            db.driver=net.sourceforge.jtds.jdbc.Driver
            db.url=jdbc:jtds:sqlserver://server01/Positions-DEV;domain=MARKETCETERA
            db.user=gmiller
            db.pass=password
            db.sql.dialect=org.hibernate.dialect.SQLServerDialect
     */
    public void testLoadValues() throws Exception
    {
        Properties props = ConfigPropertiesLoader.loadProperties(CONFIG_NAME);
        String[] keys = new String[]{ "fix.sendercompid", "fix.targetcompid", "jms.connection.factory",
            "jms.context.factory", "jms.incoming.queue",
            "db.driver","db.url","db.user","db.pass","db.sql.dialect" };
        String[] vals = new String[]{ "MKTC","ARCA","connectionFactory","com.ubermq.jms.client.JMSInitialContextFactory",
            "queue:oms-commands",
            "net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sqlserver://server01/Positions-DEV;domain=MARKETCETERA","gmiller","password",
            "org.hibernate.dialect.SQLServerDialect"};
        ArrayList propKeys = new ArrayList(props.keySet());
        assertEquals(keys.length, propKeys.size());
        for(int i = 0; i < keys.length; i++) {
            assertEquals("key["+i+"]: " + keys[i], vals[i], props.getProperty(keys[i]));

        }
    }

    public static void main(String[] args) throws Exception
    {
        ConfigPropertiesLoaderTest test = new ConfigPropertiesLoaderTest("bob");
        test.testLoadBundle();
    }
}
