package org.marketcetera.core;

import org.marketcetera.core.MarketceteraTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

import java.net.URL;
import java.net.InetAddress;
import java.io.File;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class HttpDatabaseIDFactoryTest extends TestCase {
    public HttpDatabaseIDFactoryTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(HttpDatabaseIDFactoryTest.class);
    }

    public void testFactory_existingURL() throws Exception {
        URL url= new File("src/test/resources/next_id_batch.xml").toURL();
        HttpDatabaseIDFactory factory = new HttpDatabaseIDFactory(url);
        factory.grabIDs();
        assertEquals("1", factory.getNext());
        for(int i=2;i<100; i++)
        {
            assertEquals("comparing for " +i, ""+i, factory.getNext());
        }
    }

    public void testInvalidURL() throws Exception
    {
        URL url = new URL("http://www.bogus.example.com/no/such/url");
        final HttpDatabaseIDFactory factory = new HttpDatabaseIDFactory(url);
        new ExpectedTestFailure(NoMoreIDsException.class, "bogus.example") {
            protected void execute() throws Throwable {
                factory.grabIDs();
            }
        }.run();
        assertFalse("1".equals(factory.getNext()));
        assertTrue("id is "+factory.getNext(), factory.getNext().contains(InetAddress.getLocalHost().toString()));

        // now verify subsequent are different
        String prev = factory.getNext();
        for(int i=0;i<20; i++)
        {
            String cur = factory.getNext();
            assertFalse("previous id equals to next id", prev.equals(cur));
            prev = cur;
        }
    }

    public void testUnconnectableURL() throws Exception {
        URL url = new URL("http://localhost:3456/no/such/url");
        final HttpDatabaseIDFactory factory = new HttpDatabaseIDFactory(url);
        new ExpectedTestFailure(NoMoreIDsException.class, "Connection refused") {
            protected void execute() throws Throwable {
                factory.grabIDs();
            }
        }.run();
        assertNotNull(factory.getNext());
    }
}
