package org.marketcetera.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

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
            if(url.getPath().indexOf("/hibernate/hibernate-core/") >= 0) { //$NON-NLS-1$
                verifyPropertyValue(url,"Implementation-Version","4.1.9.Final"); //$NON-NLS-1$ //$NON-NLS-2$
                foundCore = true;
            }
            if(url.getPath().indexOf("/hibernate-entitymanager/") >= 0) { //$NON-NLS-1$
                verifyPropertyValue(url,"Implementation-Version","4.1.9.Final"); //$NON-NLS-1$ //$NON-NLS-2$
                foundEntityMgr = true;
            }
        }
        assertTrue("Hibernate core library not found",foundCore); //$NON-NLS-1$
        assertTrue("Hibernate entity manager library not found", foundEntityMgr); //$NON-NLS-1$
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
                                     String propertyValue)
        throws IOException
    {
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
