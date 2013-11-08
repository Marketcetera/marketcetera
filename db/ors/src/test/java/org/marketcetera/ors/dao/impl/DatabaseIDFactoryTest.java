package org.marketcetera.ors.dao.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.LoggerConfiguration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Tests the database ID factory to make sure that if the DB is not there
 * we still get a nice fail-over {@link InMemoryIDFactory}
 * @author toli
 * @version $Id$
 */
public class DatabaseIDFactoryTest
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void setUp()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        context = new FileSystemXmlApplicationContext(new String[] { "file:src/test/sample_data/conf/test_persist.xml" },
                                                      null); 
    }
    /**
     * Runs once after all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @AfterClass
    public static void tearDown()
            throws Exception
    {
        context.stop();
    }
    /**
     * Tests database ID factory.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDatabaseIDs()
            throws Exception
    {
        IDFactory factory = context.getBean(IDFactory.class);
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
     * test spring context
     */
    private static AbstractApplicationContext context;
}
