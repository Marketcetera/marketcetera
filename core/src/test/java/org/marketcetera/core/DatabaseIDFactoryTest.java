package org.marketcetera.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.persist.PersistTestBase;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Tests the database ID factory to make sure that if the DB is not there
 * we still get a nice fail-over {@link InMemoryIDFactory}
 * @author toli
 * @version $Id$
 */
public class DatabaseIDFactoryTest
{
    /**
     * 
     *
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUp()
            throws Exception
    {
        context = PersistTestBase.springSetup(new String[] { "persist.xml" });
        context.start();
    }
    /**
     * 
     *
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDown()
            throws Exception
    {
        context.stop();
    }
    /**
     * 
     *
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDatabaseIDs()
            throws Exception
    {
        DatabaseIDFactory factory = new DatabaseIDFactory();
        factory.init();
        String next = factory.getNext();
        assertNotNull(next);
        assertFalse("looks like we got an inMemoryIDFactory id: " + next,
                    next.contains(InetAddress.getLocalHost().toString()));
        String previous = next;
        for(long i=0;i<100;i++){
            String cur = factory.getNext();
            assertNotNull(cur);
            assertNotSame("getting same ids in a row", cur, previous);
            assertTrue((new Long(cur) > new Long(previous)));
            previous = cur;
        }
    }
    /**
     * 
     */
    private static AbstractApplicationContext context;
}
