package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.Statement;
import java.io.File;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import org.marketcetera.persist.PersistTestBase;

import javax.sql.DataSource;

/**
 * Tests the database ID factory to make sure that if the DB is not there
 * we still get a nice fail-over {@link InMemoryIDFactory}
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class DatabaseIDFactoryTest extends TestCase {
    private static DataSource mDataSource;

    public DatabaseIDFactoryTest(String inName) {
        super(inName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if(mDataSource == null) {
            mDataSource = (DataSource) PersistTestBase.springSetup(
                    new String[]{"persist.xml"}).getBean("mysqlpool", //$NON-NLS-1$ //$NON-NLS-2$
                    DataSource.class);
        }
    }

    public static Test suite() throws Exception {
        new File("junit").delete(); //$NON-NLS-1$
        new File("junit").deleteOnExit(); //$NON-NLS-1$
        return new MarketceteraTestSuite(DatabaseIDFactoryTest.class);
    }

    /**
     * Verify that when a DB is inaccessible we still get an in-memory set of ids
     *
     * @throws Exception if there were errors
     */
    public void testFailThroughInMemory() throws Exception {
        DatabaseIDFactory factory = new DatabaseIDFactory(mDataSource, "notable", "nocol", 13); //$NON-NLS-1$ //$NON-NLS-2$
        boolean dbInaccessble = false;
        try {
            factory.init();
        } catch(Exception ignored ) {
            dbInaccessble = true;
            SLF4JLoggerProxy.debug(this, "expected exception: {}", ignored.getMessage()); //$NON-NLS-1$
        }
        assertTrue("for some reason db access didn't error out", dbInaccessble); //$NON-NLS-1$
        String next = factory.getNext();
        assertNotNull(next);
        assertTrue("doesn't look like an inMemoryIDFactory id: "+next, next.contains(InetAddress.getLocalHost().toString())); //$NON-NLS-1$
        String previous = next;
        for(int i=0;i < 20; i++){
            String cur = factory.getNext();
            assertNotNull(cur);
            assertNotSame("getting same ids in a row", cur, previous); //$NON-NLS-1$
            previous = cur;
        }
    }



    /**
     * needs to have a valid mySQL db in order to run this code
     * @throws Exception if there were exceptions
     */
    public void testDatabaseIDs() throws Exception {
        DatabaseIDFactory factory = new DatabaseIDFactory(mDataSource,
                DatabaseIDFactory.TABLE_NAME, DatabaseIDFactory.COL_NAME, 13);

        // create the table
        Connection dbConnection = mDataSource.getConnection();
        try {
            Statement stmt = dbConnection.createStatement();
            stmt.execute("drop table if exists " + DatabaseIDFactory.TABLE_NAME); //$NON-NLS-1$
            stmt.execute("create table " + DatabaseIDFactory.TABLE_NAME + //$NON-NLS-1$
                    "(id int default null auto_increment primary key, " + //$NON-NLS-1$
                    DatabaseIDFactory.COL_NAME + " int not null default 0)"); //$NON-NLS-1$

            factory.init();
            String next = factory.getNext();
            assertNotNull(next);
            assertFalse("looks like we got an inMemoryIDFactory id: " + next, //$NON-NLS-1$
                    next.contains(InetAddress.getLocalHost().toString()));
            String previous = next;
            for(int i=0;i < DatabaseIDFactory.NUM_IDS_GRABBED*2; i++){
                String cur = factory.getNext();
                assertNotNull(cur);
                assertNotSame("getting same ids in a row", cur, previous); //$NON-NLS-1$
                assertTrue((new Integer(cur) > new Integer(previous)));
                previous = cur;
            }
        } finally {
            if(dbConnection != null) {
                dbConnection.close();
            }
        }
    }
}
