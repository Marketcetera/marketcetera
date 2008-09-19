package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.Properties;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;

/* $License$ */
/**
 * This test verifies that the correct versions of different
 * softwares, that we depend on, are being used.
 * These tests are only meant to ensure that correct
 * software versions are used in build / dev environment, to help save
  * time when wrong versions are used by error.
 * These tests are not meant to indicate that we have strong 
 * dependency on the specific versions of these softwares, the 
 * persistence infrastructure is depends on the JPA specification
 * and in no way is dependent on a specific version / implementation of
 * JPA provider or the database.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class EnvironmentConfigurationTest extends PersistTestBase {
    @Test
    public void hibernate() throws Exception {
        //Fetch the hibernate version number from the jar's manifest
        Enumeration<URL> res = ClassLoader.getSystemResources("META-INF/MANIFEST.MF"); //$NON-NLS-1$
        boolean foundCore = false;
        boolean foundEntityMgr = false;
        while(res.hasMoreElements()) {
            URL url = res.nextElement();
            SLF4JLoggerProxy.debug(this,url.toString());
            if(url.getPath().indexOf("/hibernate/hibernate/") >= 0) { //$NON-NLS-1$
                verifyPropertyValue(url,"Hibernate-Version","3.2.6.ga"); //$NON-NLS-1$ //$NON-NLS-2$
                foundCore = true;
            }
            if(url.getPath().indexOf("/hibernate-entitymanager/") >= 0) { //$NON-NLS-1$
                verifyPropertyValue(url,"Implementation-Version","3.3.2.GA"); //$NON-NLS-1$ //$NON-NLS-2$
                foundEntityMgr = true;
            }
        }
        assertTrue("Hibernate core library not found",foundCore); //$NON-NLS-1$
        assertTrue("Hibernate entity manager library not found", foundEntityMgr); //$NON-NLS-1$
    }

    /**
     * Tests the mysql version number and configuration.
     *
     * @throws Exception if there was an error fetching the mysql
     * version number
     */
    @Test
    public void mysql() throws Exception {
        DataTypes dt = new DataTypes();
        final String version = dt.fetchDBVersion();
        SLF4JLoggerProxy.debug(this,version);
        // If the mysql version number is updated, search for source
        // files containing the string mysql to find sections of code
        // that might have mysql version dependency
        // (like change in supported unicode version)
        assertTrue("Unexpected MySQL version: "+version, //$NON-NLS-1$
                version.indexOf("5.0.5") >= 0); //$NON-NLS-1$
        assertEquals("Unexpected database charset","utf8",dt.fetchDBCharset()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Unexpected connection charset","utf8",dt.fetchConnCharset()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Unexpected client charset","utf8",dt.fetchClientCharset()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Unexpected database collation","utf8_general_ci", //$NON-NLS-1$ //$NON-NLS-2$
                dt.fetchDBCollation());
        assertEquals("Unexpected connection collation","utf8_general_ci", //$NON-NLS-1$ //$NON-NLS-2$
                dt.fetchDBConnCollation());
        assertEquals("Unexpected connection timezone","+00:00",dt.fetchDBConnTz()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Unexpected locale","en_US",dt.fetchDBLocale()); //$NON-NLS-1$ //$NON-NLS-2$
    }
    @BeforeClass
    public static void setup() throws Exception {
        springSetup(new String[]{"persist.xml"}); //$NON-NLS-1$
    }

    /**
     * Verifies that the properties file pointed to by the supplied URL
     * has the specified property name value pair
     *
     * @param url the URL of the properties file
     * @param propertyName the property name
     * @param propertyValue the property value
     * 
     * @throws IOException if there was an error reading the properties file.
     */
    private void verifyPropertyValue(URL url,
                               String propertyName,
                               String propertyValue) throws IOException {
        Properties p = new Properties();
        InputStream inStream = url.openStream();
        try {
            p.load(inStream);
            assertEquals("Incorrect library version number in library " + url, //$NON-NLS-1$
                    propertyValue,
                    p.getProperty(propertyName));
        } finally {
            inStream.close();
        }
    }
}
